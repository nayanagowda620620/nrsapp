@file:OptIn(ExperimentalMaterial3Api::class)

package com.nrskonandur.school

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        scheduleNoticeChecks(this)
        val startTab = intent?.getIntExtra("tab", 0) ?: 0
        setContent { NRSTheme { NRSApp(startTab) } }
    }
}

@Composable
fun NRSApp(startTab: Int) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var tab by rememberSaveable { mutableIntStateOf(startTab) }
    var notices by remember { mutableStateOf(NoticeRepo.cached(ctx)) }
    var loading by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var seen by remember { mutableStateOf(NoticeRepo.seenId(ctx)) }

    val refresh: () -> Unit = {
        scope.launch {
            loading = true
            try {
                notices = withContext(Dispatchers.IO) { NoticeRepo.fetch(ctx) }
                failed = false
            } catch (e: Exception) {
                failed = true
            }
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }
    LaunchedEffect(tab, notices) {
        if (tab == 1 && notices.isNotEmpty()) {
            NoticeRepo.markSeen(ctx, notices.first().id)
            seen = notices.first().id
        }
    }
    val unread = notices.isNotEmpty() && notices.first().id != seen

    val items = listOf("Home" to Icons.Default.Home, "Notices" to Icons.Default.Notifications, "Admissions" to Icons.AutoMirrored.Filled.Send, "More" to Icons.Default.Info)

    Scaffold(
        containerColor = Cream,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { AppBar() },
        bottomBar = {
            NavigationBar(containerColor = Paper) {
                items.forEachIndexed { i, (label, icon) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = {
                            if (i == 1 && unread) {
                                BadgedBox(badge = { Badge() }) { Icon(icon, contentDescription = label) }
                            } else {
                                Icon(icon, contentDescription = label)
                            }
                        },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Mint,
                            selectedIconColor = Green,
                            selectedTextColor = Green
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(go = { tab = it }, latest = notices.firstOrNull())
                1 -> NoticesScreen(notices, loading, failed, refresh)
                2 -> AdmissionsScreen()
                else -> MoreScreen()
            }
        }
    }
}

@Composable
fun AppBar() {
    Surface(color = Green) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.logo_school), contentDescription = "NRS logo", modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("NRS Konandur", color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("ICSE & PU College · Shivamogga", color = Color(0xFFB6D3C3), fontSize = 11.sp)
            }
            Image(painterResource(R.drawable.badge_25), contentDescription = "25 years", modifier = Modifier.size(38.dp))
        }
    }
}

@Composable
fun NoticesScreen(notices: List<Notice>, loading: Boolean, failed: Boolean, onRefresh: () -> Unit) {
    val ctx = LocalContext.current
    val needsPermission = Build.VERSION.SDK_INT >= 33
    var granted by remember {
        mutableStateOf(
            !needsPermission ||
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { SectionTitle("School updates", "Notices") }
            TextButton(onClick = onRefresh, enabled = !loading) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (loading) "Refreshing…" else "Refresh")
            }
        }

        if (!granted) {
            OutlineCard {
                Text("Never miss a notice", color = Green, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, fontSize = 18.sp)
                BodyText("Turn on alerts and we'll tell you when the school publishes a new notice.")
                AppButton("Turn on alerts", { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }, icon = Icons.Default.Notifications)
            }
        }
        if (failed && notices.isNotEmpty()) BodyText("Couldn't refresh – showing your last saved notices.")
        if (failed && notices.isEmpty()) BodyText("Couldn't load notices. Check your internet connection and tap Refresh.")
        if (!failed && notices.isEmpty() && !loading) BodyText("No notices yet. Check back soon.")

        notices.forEach { n ->
            OutlineCard {
                Text(prettyDate(n.date), color = Clay, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(n.title, color = Green, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                BodyText(n.body)
                if (n.link.isNotBlank()) {
                    AppButton("Open link", { ctx.openUrl(n.link) }, container = Green2)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
