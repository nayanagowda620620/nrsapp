package com.nrskonandur.school

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

data class Notice(
    val id: String,
    val title: String,
    val date: String,
    val body: String,
    val link: String
)

object NoticeRepo {
    private const val PREFS = "nrs_prefs"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun parse(raw: String): List<Notice> {
        val arr = JSONObject(raw).getJSONArray("notices")
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            Notice(
                id = o.optString("id"),
                title = o.optString("title"),
                date = o.optString("date"),
                body = o.optString("body"),
                link = o.optString("link")
            )
        }
    }

    fun cached(ctx: Context): List<Notice> = try {
        parse(prefs(ctx).getString("cache", "") ?: "")
    } catch (e: Exception) {
        emptyList()
    }

    /** Blocking network call – run on Dispatchers.IO. */
    fun fetch(ctx: Context): List<Notice> {
        val conn = URL(AppConfig.NOTICES_URL).openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        try {
            val raw = conn.inputStream.bufferedReader().use { it.readText() }
            val list = parse(raw)
            prefs(ctx).edit().putString("cache", raw).apply()
            return list
        } finally {
            conn.disconnect()
        }
    }

    fun seenId(ctx: Context): String = prefs(ctx).getString("seen", "") ?: ""
    fun markSeen(ctx: Context, id: String) = prefs(ctx).edit().putString("seen", id).apply()
    fun notifiedId(ctx: Context): String? = prefs(ctx).getString("notified", null)
    fun setNotified(ctx: Context, id: String) = prefs(ctx).edit().putString("notified", id).apply()
}

class NoticeWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val list = withContext(Dispatchers.IO) { NoticeRepo.fetch(applicationContext) }
            val latest = list.firstOrNull()
            if (latest != null) {
                val last = NoticeRepo.notifiedId(applicationContext)
                if (last == null) {
                    // First run after install: remember the newest notice, don't alert.
                    NoticeRepo.setNotified(applicationContext, latest.id)
                } else if (last != latest.id) {
                    showNotification(applicationContext, latest)
                    NoticeRepo.setNotified(applicationContext, latest.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

private const val CHANNEL_ID = "notices"

private fun showNotification(ctx: Context, notice: Notice) {
    if (Build.VERSION.SDK_INT >= 33 &&
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) return

    val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= 26) {
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "School notices", NotificationManager.IMPORTANCE_DEFAULT)
        )
    }
    val intent = Intent(ctx, MainActivity::class.java)
        .putExtra("tab", 1)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pi = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(notice.title)
        .setContentText(notice.body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notice.body))
        .setContentIntent(pi)
        .setAutoCancel(true)
        .build()
    nm.notify(1, n)
}

fun scheduleNoticeChecks(ctx: Context) {
    val request = PeriodicWorkRequestBuilder<NoticeWorker>(6, TimeUnit.HOURS)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(ctx)
        .enqueueUniquePeriodicWork("notice-check", ExistingPeriodicWorkPolicy.KEEP, request)
}
