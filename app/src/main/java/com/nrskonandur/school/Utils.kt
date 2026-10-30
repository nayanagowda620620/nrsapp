package com.nrskonandur.school

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

private fun Context.safeStart(intent: Intent) {
    try {
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        Toast.makeText(this, "No app found to open this", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openUrl(url: String) = safeStart(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

fun Context.dial(number: String) = safeStart(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))

fun Context.whatsapp(message: String) =
    openUrl("https://wa.me/${AppConfig.WHATSAPP}?text=${Uri.encode(message)}")

fun Context.sendEmail(subject: String) =
    safeStart(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${AppConfig.EMAIL}?subject=${Uri.encode(subject)}")))

fun Context.shareApp() {
    val text = "National Residential School, Konandur – admissions ${AppConfig.YEAR}, notices and campus info.\n" +
        "https://play.google.com/store/apps/details?id=$packageName"
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    safeStart(Intent.createChooser(send, "Share NRS Konandur app"))
}

fun Context.rateApp() = openUrl("https://play.google.com/store/apps/details?id=$packageName")

fun prettyDate(raw: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(raw)
    if (d != null) SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(d) else raw
} catch (e: Exception) {
    raw
}
