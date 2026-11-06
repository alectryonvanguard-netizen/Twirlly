package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.*

@Composable
fun LabsScreen(store: Store, nav: NavHostController) {
    ScreenBg {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Practice", color = C.Muted, fontSize = 14.sp)
                Text("Labs", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
            item {
                GlowCard(accent = C.Green) {
                    Text(
                        "Every lab runs in a local simulated shell. Nothing touches real systems or networks. " +
                            "Explore files, find the flag and submit it with: submit flag{...}",
                        color = Color(0xFFD6D9E5), fontSize = 13.sp
                    )
                }
            }
            items(Content.labs, key = { it.id }) { lab ->
                val accent = Color(lab.level.color)
                val done = lab.id in store.labs
                GlowCard(accent = accent, onClick = { nav.navigate("lab/${lab.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Terminal, null, tint = accent, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Tag(lab.level.label, accent)
                                Spacer(Modifier.width(8.dp))
                                Text("${lab.xp} XP", color = C.Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(lab.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(lab.brief, color = C.Muted, fontSize = 12.sp)
                        }
                        if (done) Icon(Icons.Filled.CheckCircle, null, tint = C.Green)
                    }
                }
            }
        }
    }
}

@Composable
fun LabScreen(labId: String, store: Store, nav: NavHostController) {
    val lab = Content.labs.first { it.id == labId }
    val lines: SnapshotStateList<String> = remember {
        mutableStateListOf("CyberLab sandbox ready. Type help to begin.", "Mission: ${lab.brief}")
    }
    var input by remember { mutableStateOf("") }
    var hintIdx by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) listState.animateScrollToItem(lines.lastIndex)
    }

    fun run(raw: String) {
        val cmd = raw.trim()
        if (cmd.isEmpty()) return
        lines.add("analyst@lab:~\$ $cmd")
        val parts = cmd.split(Regex("\\s+"))
        when (parts[0].lowercase()) {
            "clear" -> lines.clear()
            "hint" -> {
                if (hintIdx < lab.hints.size) {
                    lines.add("[hint] " + lab.hints[hintIdx]); hintIdx++
                } else lines.add("[hint] No more hints.")
            }
            "submit" -> {
                val f = parts.getOrNull(1) ?: ""
                if (f == lab.flag) {
                    lines.add("CORRECT! Flag accepted.")
                    if (store.completeLab(lab.id, lab.xp)) lines.add("+${lab.xp} XP earned. Lab complete!")
                    else lines.add("Lab already completed earlier.")
                } else lines.add("Wrong flag. Keep digging.")
            }
            else -> lines.add(Terminal.exec(lab, cmd))
        }
        input = ""
    }

    ScreenBg {
        Column(Modifier.fillMaxSize().imePadding()) {
            TopBar(lab.title) { nav.popBackStack() }
            val shape = RoundedCornerShape(14.dp)
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                    .background(Color(0xFF02030A), shape).border(1.dp, C.Green.copy(alpha = .5f), shape),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(lines.toList()) { l ->
                    Text(
                        l, fontFamily = FontFamily.Monospace, fontSize = 13.sp,
                        color = if (l.startsWith("analyst@")) Color.White else C.Green
                    )
                }
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("help", "ls", "cat README.txt", "hint", "clear").forEach { c ->
                    Pill(c, false, C.Green) { run(c) }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    modifier = Modifier.weight(1f), singleLine = true,
                    placeholder = { Text("command", color = C.Muted) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { run(input) })
                )
                IconButton(onClick = { run(input) }) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = C.Red)
                }
            }
        }
    }
}
