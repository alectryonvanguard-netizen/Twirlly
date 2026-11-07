package com.cyberlab.app.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.Content
import com.cyberlab.app.data.Glossary
import com.cyberlab.app.data.Store
import kotlin.math.log2

// ───────────── TOOLS ─────────────
@Composable
fun ToolsScreen(nav: NavHostController) {
    val cats = listOf("All") + Content.tools.map { it.category }.distinct()
    var cat by remember { mutableStateOf("All") }
    var open by remember { mutableStateOf("") }
    val list = Content.tools.filter { cat == "All" || it.category == cat }
    ScreenBg {
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = listPad()) {
            item { ScreenTitle("Use", "Tools") }
            item {
                Text(
                    "Only use security tools on systems you own or have written permission to test.",
                    color = C.Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp)
                )
            }
            item {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()).padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) { cats.forEach { c -> Pill(c, cat == c, C.Orange) { cat = c } } }
            }
            items(list, key = { it.name }) { t ->
                val expanded = open == t.name
                Column {
                    ListRow(
                        Icons.Filled.Build, C.Orange, t.name, t.what,
                        onClick = { open = if (expanded) "" else t.name },
                        trailing = {
                            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null, tint = C.Muted)
                        }
                    )
                    AnimatedVisibility(expanded) {
                        Column(Modifier.padding(start = 58.dp, bottom = 16.dp, top = 6.dp)) {
                            Kicker("Typical use", C.Orange)
                            Text(t.use, color = Color(0xFFD6D9E5), fontSize = 14.sp, lineHeight = 20.sp)
                            Spacer(Modifier.height(10.dp))
                            Kicker("Defender view", C.Green)
                            Text(t.defend, color = Color(0xFFD6D9E5), fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
            item { SectionTitle("Cheat sheets") }
            Glossary.cheats.forEach { (title, rows) ->
                item {
                    Spacer(Modifier.height(10.dp))
                    Kicker(title, C.Orange)
                    Spacer(Modifier.height(6.dp))
                    rows.forEach { (a, b) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                            Text(a, color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(128.dp))
                            Text(b, color = C.Muted, fontSize = 13.sp)
                        }
                    }
                }
            }
            item {
                SectionTitle("Learn the words")
                ListRow(
                    Icons.Filled.Book, C.Blue, "Glossary", "${Glossary.terms.size} terms explained",
                    onClick = { nav.navigate("glossary") },
                    trailing = { Icon(Icons.Filled.ChevronRight, null, tint = C.Muted) }
                )
            }
        }
    }
}

// ───────────── GLOSSARY ─────────────
@Composable
fun GlossaryScreen(nav: NavHostController) {
    var q by remember { mutableStateOf("") }
    val list = Glossary.terms.filter { q.isBlank() || it.term.contains(q, true) || it.def.contains(q, true) }
    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar("Glossary", onBack = { nav.popBackStack() })
            Column(Modifier.padding(horizontal = 20.dp)) { SearchField(q, "Search terms") { q = it } }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp)) {
                items(list, key = { it.term }) { t ->
                    Column(Modifier.padding(top = 14.dp)) {
                        Text(t.term, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Text(t.def, color = C.Muted, fontSize = 14.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(14.dp))
                        Hairline()
                    }
                }
            }
        }
    }
}

// ───────────── SECURE ─────────────
private fun analyze(pw: String): Pair<Int, String> {
    if (pw.isEmpty()) return 0 to ""
    val common = listOf("password", "123456", "12345678", "qwerty", "letmein", "admin", "welcome", "iloveyou")
    if (pw.lowercase() in common || pw.length < 6) return 10 to "Very weak"
    var pool = 0
    if (pw.any { it.isLowerCase() }) pool += 26
    if (pw.any { it.isUpperCase() }) pool += 26
    if (pw.any { it.isDigit() }) pool += 10
    if (pw.any { !it.isLetterOrDigit() }) pool += 32
    val bits = (pw.length * log2(pool.coerceAtLeast(2).toDouble())).toInt()
    val label = when {
        bits < 28 -> "Very weak"
        bits < 36 -> "Weak"
        bits < 60 -> "Fair"
        bits < 80 -> "Strong"
        else -> "Excellent"
    }
    return bits to label
}

@Composable
fun SecureScreen(store: Store) {
    val total = Content.checklist.size
    val done = store.checks.size
    val frac = done / total.toFloat()
    var pw by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val (bits, rating) = analyze(pw)
    val ratingColor = when (rating) {
        "Very weak", "Weak" -> C.Red
        "Fair" -> C.Orange
        "Strong", "Excellent" -> C.Green
        else -> C.Muted
    }
    ScreenBg {
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = listPad()) {
            item { ScreenTitle("Stay", "Secure") }
            item {
                Column(Modifier.fillMaxWidth().padding(vertical = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Ring(frac, 170.dp, if (frac >= .8f) C.Green else if (frac >= .4f) C.Orange else C.Red, 14.dp) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${(frac * 100).toInt()}%", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                            Text("security score", color = C.Muted, fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("$done of $total habits on", color = C.Muted, fontSize = 14.sp)
                }
            }
            item { Kicker("Your habits", C.Blue); Spacer(Modifier.height(4.dp)) }
            items(Content.checklist, key = { it.id }) { item ->
                Column {
                    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = item.id in store.checks, onCheckedChange = { store.toggleCheck(item.id) },
                            colors = SwitchDefaults.colors(checkedTrackColor = C.Green, checkedThumbColor = Color.White)
                        )
                    }
                    Hairline()
                }
            }
            item {
                SectionTitle("Password strength")
                Text("Runs only on your device. Nothing is stored or sent. Avoid typing a password you really use.", color = C.Muted, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                TextField(
                    value = pw, onValueChange = { pw = it }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp), placeholder = { Text("Type a test password") },
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { show = !show }) {
                            Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = C.Muted)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = .07f),
                        unfocusedContainerColor = Color.White.copy(alpha = .07f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                if (pw.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (bits / 100f).coerceIn(0.05f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = ratingColor, trackColor = Color.White.copy(alpha = .08f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("$rating · about $bits bits of entropy (upper estimate)", color = ratingColor, fontSize = 13.sp)
                }
            }
        }
    }
}

// ───────────── PROFILE ─────────────
@Composable
private fun Stat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = C.Muted, fontSize = 12.sp)
    }
}

@Composable
fun ProfileScreen(store: Store, nav: NavHostController) {
    val ctx = LocalContext.current
    var confirmReset by remember { mutableStateOf(false) }
    val badges = listOf(
        Triple("First Steps", Icons.Filled.DirectionsWalk, store.lessons.isNotEmpty()),
        Triple("Scholar", Icons.Filled.School, store.lessons.size >= 8),
        Triple("Lab Rat", Icons.Filled.Science, store.labs.isNotEmpty()),
        Triple("Flag Hunter", Icons.Filled.Flag, store.labs.size >= 3),
        Triple("On Fire", Icons.Filled.LocalFireDepartment, store.streak >= 3),
        Triple("Level 5", Icons.Filled.EmojiEvents, store.level >= 5),
        Triple("Fortified", Icons.Filled.VerifiedUser, store.checks.size == Content.checklist.size)
    )
    ScreenBg {
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = listPad()) {
            item {
                Column(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(92.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(C.Red, C.Purple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(store.name.take(1).uppercase(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(store.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${store.rank} · Level ${store.level}", color = C.Gold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { store.xpIntoLevel / 200f },
                        modifier = Modifier.width(220.dp).height(5.dp).clip(CircleShape),
                        color = C.Gold, trackColor = Color.White.copy(alpha = .1f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("${200 - store.xpIntoLevel} XP to level ${store.level + 1}", color = C.Muted, fontSize = 12.sp)
                }
            }
            item {
                Row(Modifier.fillMaxWidth().padding(vertical = 26.dp)) {
                    Stat("${store.xp}", "XP", Modifier.weight(1f))
                    Stat("${store.streak}", "Day streak", Modifier.weight(1f))
                    Stat("${store.lessons.size + store.labs.size}", "Completed", Modifier.weight(1f))
                }
                Hairline()
            }
            item {
                SectionTitle("Achievements")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    items(badges, key = { it.first }) { (title, icon, earned) ->
                        Column(Modifier.width(76.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier.size(64.dp).clip(CircleShape)
                                    .background(if (earned) C.Gold.copy(alpha = .20f) else Color.White.copy(alpha = .06f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(if (earned) icon else Icons.Filled.Lock, null, tint = if (earned) C.Gold else C.Muted)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(title, color = if (earned) Color.White else C.Muted, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            item {
                SectionTitle("More")
                ListRow(Icons.Filled.Share, C.Blue, "Share progress", "Tell a friend where you stand", onClick = {
                    val i = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "I'm level ${store.level} (${store.rank}) on CyberLab with ${store.xp} XP and a ${store.streak}-day streak!"
                        )
                    }
                    ctx.startActivity(Intent.createChooser(i, "Share progress"))
                })
                ListRow(Icons.Filled.Book, C.Purple, "Glossary", "Security terms explained", onClick = { nav.navigate("glossary") })
                ListRow(Icons.AutoMirrored.Filled.Logout, C.Orange, "Sign out", null, onClick = {
                    store.signOut()
                    nav.navigate("welcome") { popUpTo("learn") { inclusive = true } }
                })
                ListRow(Icons.Filled.RestartAlt, C.Red, "Reset all progress", "Clears XP, lessons, labs and saved items", onClick = { confirmReset = true })
            }
        }
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = C.Surface2,
            title = { Text("Reset progress?", color = Color.White) },
            text = { Text("XP, streak, lessons, labs and saved items will be cleared.", color = C.Muted) },
            confirmButton = { TextButton(onClick = { store.reset(); confirmReset = false }) { Text("Reset", color = C.Red) } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel", color = C.Muted) } }
        )
    }
}
