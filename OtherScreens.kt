package com.cyberlab.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.*
import kotlin.math.log2

// ───────────── TOOLS ─────────────
@Composable
fun ToolsScreen() {
    val cats = listOf("All") + Content.tools.map { it.category }.distinct()
    var cat by remember { mutableStateOf("All") }
    var open by remember { mutableStateOf("") }
    val list = Content.tools.filter { cat == "All" || it.category == cat }
    ScreenBg {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Use", color = C.Muted, fontSize = 14.sp)
                Text("Tools", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
            item {
                GlowCard(accent = C.Orange) {
                    Text(
                        "Use security tools only on systems you own or have written permission to test.",
                        color = Color(0xFFD6D9E5), fontSize = 13.sp
                    )
                }
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cats.forEach { c -> Pill(c, cat == c, C.Orange) { cat = c } }
                }
            }
            items(list, key = { it.name }) { t ->
                val expanded = open == t.name
                GlowCard(accent = C.Orange, onClick = { open = if (expanded) "" else t.name }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Tag(t.category, C.Orange)
                            Spacer(Modifier.height(4.dp))
                            Text(t.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(t.what, color = C.Muted, fontSize = 13.sp)
                        }
                        Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null, tint = C.Muted)
                    }
                    if (expanded) {
                        Spacer(Modifier.height(10.dp))
                        Text("Typical use", color = C.Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(t.use, color = Color(0xFFD6D9E5), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Defender view", color = C.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(t.defend, color = Color(0xFFD6D9E5), fontSize = 14.sp)
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
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Stay", color = C.Muted, fontSize = 14.sp)
                Text("Secure", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
            item {
                GlowCard(accent = C.Blue) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize().padding(8.dp)) {
                                val st = Stroke(10.dp.toPx(), cap = StrokeCap.Round)
                                drawArc(Color(0xFF2A2E42), -90f, 360f, false, style = st)
                                drawArc(C.Green, -90f, 360f * frac, false, style = st)
                            }
                            Text("${(frac * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Security score", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("$done of $total habits on", color = C.Muted, fontSize = 13.sp)
                        }
                    }
                }
            }
            items(Content.checklist, key = { it.id }) { item ->
                GlowCard(accent = if (item.id in store.checks) C.Green else Color(0xFF3A3F55), onClick = { store.toggleCheck(item.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = item.id in store.checks, onCheckedChange = { store.toggleCheck(item.id) },
                            colors = CheckboxDefaults.colors(checkedColor = C.Green)
                        )
                        Text(item.title, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
            item {
                GlowCard(accent = C.Purple) {
                    Text("Password strength checker", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Runs fully on your device. Nothing is stored or sent. Avoid typing a password you actually use.", color = C.Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pw, onValueChange = { pw = it }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Test password") },
                        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { show = !show }) {
                                Icon(if (show) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = C.Muted)
                            }
                        }
                    )
                    if (pw.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (bits / 100f).coerceIn(0.05f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = ratingColor, trackColor = C.Surface2
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("$rating · about $bits bits of entropy (upper estimate)", color = ratingColor, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ───────────── PROFILE ─────────────
@Composable
fun ProfileScreen(store: Store, nav: NavHostController) {
    var confirmReset by remember { mutableStateOf(false) }
    val badges = listOf(
        Triple("First Steps", "Finish 1 lesson", store.lessons.size >= 1),
        Triple("Scholar", "Finish 8 lessons", store.lessons.size >= 8),
        Triple("Lab Rat", "Solve 1 lab", store.labs.size >= 1),
        Triple("Flag Hunter", "Solve 3 labs", store.labs.size >= 3),
        Triple("Level 5", "Reach level 5", store.level >= 5),
        Triple("Fortified", "Finish the checklist", store.checks.size == Content.checklist.size)
    )
    ScreenBg {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(64.dp).clip(CircleShape).background(C.Red.copy(alpha = .3f))
                            .border(2.dp, C.Red, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(store.name.take(1).uppercase(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(store.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(store.rank, color = C.Gold, fontSize = 14.sp)
                    }
                }
            }
            item { XpCard(store) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlowCard(Modifier.weight(1f), accent = C.Purple) {
                        Text("${store.lessons.size}/${Content.courses.sumOf { it.lessons.size }}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Lessons", color = C.Muted, fontSize = 12.sp)
                    }
                    GlowCard(Modifier.weight(1f), accent = C.Green) {
                        Text("${store.labs.size}/${Content.labs.size}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Labs", color = C.Muted, fontSize = 12.sp)
                    }
                }
            }
            item { Text("Achievements", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            items(badges.chunked(2)) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (title, sub, earned) ->
                        GlowCard(Modifier.weight(1f), accent = if (earned) C.Gold else Color(0xFF3A3F55)) {
                            Icon(
                                if (earned) Icons.Filled.EmojiEvents else Icons.Filled.Lock, null,
                                tint = if (earned) C.Gold else C.Muted
                            )
                            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(sub, color = C.Muted, fontSize = 11.sp)
                        }
                    }
                }
            }
            item {
                RedButton("Sign out", icon = Icons.AutoMirrored.Filled.Logout) {
                    store.signOut()
                    nav.navigate("welcome") { popUpTo("learn") { inclusive = true } }
                }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.RestartAlt, null, tint = C.Muted)
                    Spacer(Modifier.width(6.dp))
                    Text("Reset all progress", color = C.Muted)
                }
            }
        }
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = C.Surface,
            title = { Text("Reset progress?", color = Color.White) },
            text = { Text("XP, lessons, labs and checklist will be cleared.", color = C.Muted) },
            confirmButton = { TextButton(onClick = { store.reset(); confirmReset = false }) { Text("Reset", color = C.Red) } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancel", color = C.Muted) } }
        )
    }
}
