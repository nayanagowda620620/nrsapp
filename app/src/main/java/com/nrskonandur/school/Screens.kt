@file:OptIn(ExperimentalMaterial3Api::class)

package com.nrskonandur.school

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- reusable pieces ----------

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = Green,
    content: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val m = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Card(
        modifier = m.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.5.dp, Green)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
fun SectionTitle(kicker: String, title: String) {
    Column {
        Text(kicker.uppercase(), color = Green2, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text(title, color = Green, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 28.sp)
    }
}

@Composable
fun Pill(text: String) {
    Surface(color = Saffron, shape = RoundedCornerShape(50)) {
        Text(text, color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
    }
}

@Composable
fun ChipRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { o ->
            FilterChip(selected = o == selected, onClick = { onSelect(o) }, label = { Text(o) })
        }
    }
}

@Composable
fun BodyText(text: String) = Text(text, color = Muted, fontSize = 14.sp, lineHeight = 21.sp)

@Composable
fun Expandable(title: String, content: @Composable ColumnScope.() -> Unit) {
    var open by rememberSaveable(title) { mutableStateOf(false) }
    OutlineCard(onClick = { open = !open }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), color = Green, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(if (open) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Green)
        }
        if (open) content()
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.clickable(onClick = onClick).padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(Mint), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = Green)
        }
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green)
    }
}

@Composable
private fun StatCard(num: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.5.dp, Green)
    ) {
        Column(Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(num, color = Clay, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Text(label, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProgramCard(icon: ImageVector, tag: String, title: String, text: String, cta: String, onClick: () -> Unit) {
    OutlineCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Green), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Saffron)
            }
            Column {
                Text(tag.uppercase(), color = Green2, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(title, color = Green, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
        BodyText(text)
        Text(cta + " →", color = Clay, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onClick))
    }
}

@Composable
private fun CheckLine(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Green2, modifier = Modifier.size(20.dp))
        Text(text, color = Ink, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun LogoItem(res: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(96.dp)) {
        Image(painterResource(res), contentDescription = label, modifier = Modifier.size(72.dp))
        Text(label, fontSize = 11.sp, color = Green, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, sub: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(Mint), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Green, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(label, color = Green, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(sub, color = Muted, fontSize = 13.sp)
        }
    }
}

// ---------- HOME ----------

@Composable
fun HomeScreen(go: (Int) -> Unit, latest: Notice?) {
    val ctx = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Green).padding(22.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Pill("ADMISSIONS ${AppConfig.YEAR}")
                Text(
                    "Where discipline meets excellence",
                    color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 30.sp, lineHeight = 34.sp
                )
                Text(
                    "ICSE from LKG to Class 10 and PU College (Science), with hostel and day-scholar options in Konandur, Shivamogga.",
                    color = Color(0xFFCFE3D7), fontSize = 14.sp, lineHeight = 20.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppButton("Apply now", { go(2) }, container = Saffron, content = Green, icon = Icons.AutoMirrored.Filled.ArrowForward)
                    OutlinedButton(
                        onClick = { ctx.dial(AppConfig.PHONE_1) },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Call", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (latest != null) {
            OutlineCard(onClick = { go(1) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Clay)
                    Column(Modifier.weight(1f)) {
                        Text("LATEST NOTICE", color = Clay, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(latest.title, color = Green, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Green)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            QuickAction(Icons.Default.Call, "Call") { ctx.dial(AppConfig.PHONE_1) }
            QuickAction(Icons.AutoMirrored.Filled.Send, "WhatsApp") { ctx.whatsapp("Hello, I would like to know about admissions ${AppConfig.YEAR}.") }
            QuickAction(Icons.Default.Place, "Directions") { ctx.openUrl(AppConfig.MAP_URL) }
            QuickAction(Icons.Default.Share, "Share") { ctx.shareApp() }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("25", "Years of excellence", Modifier.weight(1f))
            StatCard("50+", "State level winners", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("100+", "Awards & trophies", Modifier.weight(1f))
            StatCard("LKG–10", "ICSE curriculum", Modifier.weight(1f))
        }

        SectionTitle("Programs ${AppConfig.YEAR}", "Join the NRS family")
        ProgramCard(
            Icons.Default.Face, "LKG – Class 10", "ICSE School",
            "Activity-based early learning, conceptual primary years and rigorous ICSE high school that prepares students for board examinations.",
            "Apply to school"
        ) { go(2) }
        ProgramCard(
            Icons.Default.Star, "Science stream", "PU College",
            "Expert faculty, modern laboratories, career guidance and competitive-exam preparation for higher education.",
            "Apply to PU College"
        ) { go(2) }
        ProgramCard(
            Icons.Default.Home, "Boys & girls", "Hostel life",
            "Separate wings for boys and girls with spacious dormitories, nutritious meals, medical care and house-master supervision.",
            "Campus facilities"
        ) { go(3) }

        SectionTitle("Why NRS", "A campus built for growing up well")
        OutlineCard {
            CheckLine("Experienced, dedicated faculty guiding every student")
            CheckLine("Safe, structured residential environment")
            CheckLine("Consistently high results and competitive achievements")
            CheckLine("Smart classrooms, labs, library and playground")
            CheckLine("Safe transport covering multiple routes")
            CheckLine("Sports, arts, music and cultural activities")
        }

        OutlineCard {
            SectionTitle("Our legacy", "Rooted in values, since 1946")
            BodyText("National Residential School is a unit of National Education Society (R.), Shivamogga — born from Mahatma Gandhi's call for quality rural education.")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                LogoItem(R.drawable.logo_school, "NRS Konandur")
                LogoItem(R.drawable.logo_society, "National Education Society")
                LogoItem(R.drawable.badge_25, "25 Years")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ---------- ADMISSIONS ----------

@Composable
fun AdmissionsScreen() {
    val ctx = LocalContext.current
    var level by rememberSaveable { mutableStateOf("") }
    var stay by rememberSaveable { mutableStateOf("") }
    var student by rememberSaveable { mutableStateOf("") }
    var parent by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var course by rememberSaveable { mutableStateOf("") }
    var stay2 by rememberSaveable { mutableStateOf("Hostel") }
    var msg by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("Admissions ${AppConfig.YEAR}", "Find your path")
        OutlineCard {
            Text("1. What are you applying for?", fontWeight = FontWeight.Bold, color = Ink)
            ChipRow(listOf("ICSE (LKG–Class 10)", "PU Science"), level) { level = it }
            Text("2. Hostel or day scholar?", fontWeight = FontWeight.Bold, color = Ink)
            ChipRow(listOf("Hostel", "Day scholar"), stay) { stay = it }
            if (level.isNotEmpty() && stay.isNotEmpty()) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Green).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("$level · $stay", color = Saffron, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        if (stay == "Hostel")
                            "Residential students stay in our hostel with house-master supervision, meals and study support. Download the application form, then call or WhatsApp the office to visit the campus."
                        else
                            "Day scholars use our school transport routes or travel on their own. Download the application form, then call or WhatsApp the office to visit the campus.",
                        color = Color(0xFFD6E8DD), fontSize = 14.sp, lineHeight = 20.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppButton("Form", { ctx.openUrl(AppConfig.FORM_URL) }, container = Saffron, content = Green)
                        AppButton(
                            "WhatsApp",
                            { ctx.whatsapp("Hello, I am interested in admission ${AppConfig.YEAR}: $level ($stay). Please share details.") },
                            container = Color(0xFF1FA855)
                        )
                        AppButton("Call", { ctx.dial(AppConfig.PHONE_1) }, container = Green2)
                    }
                }
            }
        }

        SectionTitle("Process", "Four steps to join NRS")
        listOf(
            "Enquire" to "Call, WhatsApp or send the enquiry form below.",
            "Visit the campus" to "Meet our team, see classrooms, labs and the hostel.",
            "Submit the form" to "Fill in the application form and submit it at the school office.",
            "Confirm admission" to "The office guides you through the remaining formalities."
        ).forEachIndexed { i, (t, d) ->
            OutlineCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(38.dp).clip(CircleShape).background(Saffron), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", color = Green, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(t, color = Green, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, fontSize = 17.sp)
                        BodyText(d)
                    }
                }
            }
        }

        SectionTitle("Checklist", "Documents to keep ready")
        OutlineCard {
            CheckLine("Completed application form")
            CheckLine("Student's birth certificate")
            CheckLine("Previous class report card / transfer certificate")
            CheckLine("Class 10 marks card (for PU College admission)")
            CheckLine("Recent passport-size photographs")
            Text(
                "This is a typical checklist. Please confirm the exact documents, fees and dates for ${AppConfig.YEAR} with the school office.",
                color = Clay, fontSize = 12.sp, lineHeight = 17.sp
            )
            AppButton("Download application form", { ctx.openUrl(AppConfig.FORM_URL) }, container = Green, icon = Icons.Default.Info)
        }

        SectionTitle("Enquiry", "Send us a message")
        OutlineCard {
            OutlinedTextField(student, { student = it }, label = { Text("Student name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(parent, { parent = it }, label = { Text("Parent / guardian name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                phone, { phone = it }, label = { Text("Phone number") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth()
            )
            Text("Class / course", fontWeight = FontWeight.Bold, color = Ink)
            ChipRow(listOf("LKG / UKG", "Class 1–5", "Class 6–10 (ICSE)", "PUC – Science"), course) { course = it }
            Text("Hostel or day scholar", fontWeight = FontWeight.Bold, color = Ink)
            ChipRow(listOf("Hostel", "Day scholar"), stay2) { stay2 = it }
            OutlinedTextField(msg, { msg = it }, label = { Text("Message (optional)") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            val ok = student.isNotBlank() && phone.count { it.isDigit() } >= 10 && course.isNotEmpty()
            AppButton(
                "Send on WhatsApp",
                {
                    val text = buildString {
                        append("Admission enquiry ${AppConfig.YEAR}\n")
                        append("Student: $student\n")
                        if (parent.isNotBlank()) append("Parent: $parent\n")
                        append("Class/Course: $course\n")
                        append("Hostel/Day scholar: $stay2\n")
                        append("Phone: $phone")
                        if (msg.isNotBlank()) append("\n$msg")
                    }
                    ctx.whatsapp(text)
                },
                modifier = Modifier.fillMaxWidth(),
                container = Color(0xFF1FA855),
                icon = Icons.AutoMirrored.Filled.Send,
                enabled = ok
            )
            Text("Prefer to talk? Call ${AppConfig.PHONE_1_PRETTY} or ${AppConfig.PHONE_2_PRETTY}.", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ---------- MORE ----------

private val facilities = listOf(
    "Hostel Facility" to "Separate hostel wings for boys and girls with spacious, well-ventilated dormitories and supervision by house masters.",
    "Laboratories" to "Well-equipped Physics, Chemistry, Biology and Computer labs with supervised individual practical sessions.",
    "Computer Education" to "Computer education from Class 1 with programming, graphics and internet exposure.",
    "Digital Classroom" to "Smart classrooms enhance learning through visuals, experiments and interactive teaching.",
    "Conveyance Facility" to "Safe transport covering multiple routes with pickup and drop near student residences.",
    "Workshops & Seminars" to "Regular workshops and seminars conducted by eminent resource persons.",
    "Co-Curricular Activities" to "Training in sports, yoga, karate, music, dance, art and competitive events.",
    "Mid Day Meal" to "Nutritious vegetarian meals with milk and snacks for KG students.",
    "Medical Facility" to "Regular health checkups and emergency medical care with parent intimation.",
    "Tuck Policy" to "Balanced diet provided by school. Outside eatables are restricted.",
    "Library" to "Well-stocked library with academic books, reference materials and periodicals to support learning.",
    "Playground & Sports" to "Spacious playground with facilities for outdoor games, athletics and physical training."
)

private val faqs = listOf(
    "Which classes and courses does NRS offer?" to "The ICSE curriculum from LKG to Class 10, and a PU College with a Science stream.",
    "Is hostel facility available?" to "Yes. Separate hostel wings for boys and girls with house-master supervision. Day-scholar admission is also available.",
    "Is it co-educational and English medium?" to "Yes. NRS is an English-medium, co-educational institution run by National Education Society (R.), Shivamogga.",
    "Where is the school located?" to AppConfig.ADDRESS,
    "Is school transport available?" to "Yes. A conveyance facility covers multiple routes with pickup and drop near student residences.",
    "How do I apply for ${AppConfig.YEAR}?" to "Open the Admissions tab, download the application form and submit it at the school office, or call / WhatsApp us."
)

@Composable
fun MoreScreen() {
    val ctx = LocalContext.current
    val version = remember {
        try { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "" } catch (e: Exception) { "" }
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle("Explore", "About NRS Konandur")
        Expandable("About our school") {
            BodyText("National Residential School provides holistic education with modern facilities, spacious classrooms, well-equipped labs, library, sports facilities, and hostel accommodations. We focus on ICSE & PUC courses and prepare students for a bright future with experienced faculty and opportunities for competitions.")
            BodyText("National Residential School is managed by the National Education Society (Regd.), Shivamogga. The school was born with a vision to support and promote advancement of educational activities for the benefit of students of both rural and urban areas, and to build national unity and shape patriotic, responsible citizens. It is an English-medium, co-educational institution.")
        }
        Expandable("Our management") {
            BodyText("In 1946 senior freedom fighters and the public of Shimoga set up the National Education Society, answering Mahatma Gandhi's call for quality rural education. The Society started an independently managed High School in 1946, opened the first Girls' High School in the district in 1954, and gradually opened institutions in rural areas to cater to the needs of rural people.")
        }
        Expandable("Academics") {
            BodyText("Pre-Primary: activity-based learning that builds foundations in language, numeracy, creativity and social skills.")
            BodyText("Primary (I–V): conceptual understanding, communication skills, values and curiosity-driven learning.")
            BodyText("High School (VI–X, ICSE): strong academic rigor aligned with ICSE standards, preparing students for board examinations.")
            BodyText("PU College (Science): expert faculty, modern laboratories, career guidance and competitive exam preparation.")
            BodyText("Smart classrooms, experiential learning, practical exposure, regular assessments and individual attention form the backbone of our teaching.")
        }
        Expandable("Campus facilities") {
            facilities.forEach { (t, d) ->
                Text(t, color = Green, fontWeight = FontWeight.Bold)
                BodyText(d)
            }
        }
        Expandable("Admission FAQ") {
            faqs.forEach { (q, a) ->
                Text(q, color = Green, fontWeight = FontWeight.Bold)
                BodyText(a)
            }
        }

        SectionTitle("Contact", "Get in touch")
        OutlineCard {
            ContactRow(Icons.Default.Call, AppConfig.PHONE_1_PRETTY, "Admissions office") { ctx.dial(AppConfig.PHONE_1) }
            ContactRow(Icons.Default.Call, AppConfig.PHONE_2_PRETTY, "Office") { ctx.dial(AppConfig.PHONE_2) }
            ContactRow(Icons.AutoMirrored.Filled.Send, "WhatsApp", "Chat with us instantly") { ctx.whatsapp("Hello NRS Konandur,") }
            ContactRow(Icons.Default.Email, AppConfig.EMAIL, "Email us") { ctx.sendEmail("Enquiry – NRS Konandur") }
            ContactRow(Icons.Default.Place, "Konandur, Shivamogga", "Open in Google Maps") { ctx.openUrl(AppConfig.MAP_URL) }
        }

        OutlineCard {
            Text("Follow us", color = Green, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppButton("Instagram", { ctx.openUrl(AppConfig.INSTAGRAM) }, container = Green2)
                AppButton("Facebook", { ctx.openUrl(AppConfig.FACEBOOK) }, container = Green2)
            }
            AppButton("YouTube", { ctx.openUrl(AppConfig.YOUTUBE) }, container = Green2)
        }

        OutlineCard {
            ContactRow(Icons.Default.Share, "Share this app", "Tell other parents") { ctx.shareApp() }
            ContactRow(Icons.Default.Star, "Rate us on Google Play", "Your feedback helps") { ctx.rateApp() }
            ContactRow(Icons.Default.Info, "Privacy policy", "How we handle your information") { ctx.openUrl(AppConfig.PRIVACY_URL) }
            ContactRow(Icons.Default.Home, "Visit our website", AppConfig.SITE_URL) { ctx.openUrl(AppConfig.SITE_URL) }
        }
        Text(
            "NRS Konandur · version $version",
            color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
    }
}
