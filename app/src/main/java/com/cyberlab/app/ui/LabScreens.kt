package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build as TerminalIcon
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.Content
import com.cyberlab.app.data.Store

@Composable
fun LabsScreen(store: Store, nav: NavHostController) {
    val done = store.labs.size
    val total = Content.labs.size
    ScreenBg {
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = listPad()) {
            item { ScreenTitle("Practice", "Labs") }
            item {
                Row(Modifier.padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ring(done / total.toFloat(), 84.dp, C.Green, 8.dp) {
                        Text("$done/$total", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(18.dp))
                    Column {
                        Text("Hands-on sandboxes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "A simulated shell. Nothing touches real systems. Find the flag, then: submit flag{...}",
                            color = C.Muted, fontSize = 13.sp, lineHeight = 18.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            items(Content.labs, key = { it.id }) { lab ->
                val tint = levelColors(lab.level).first
                ListRow(
                    TerminalIcon, tint, lab.title, "${lab.level.label} · ${lab.xp} XP\n${lab.brief}",
                    onClick = { nav.navigate("lab/${lab.id}") },
                    trailing = {
                        if (lab.id in store.labs) Icon(Icons.Filled.CheckCircle, null, tint = C.Green)
                        else Icon(Icons.Filled.ChevronRight, null, tint = C.Muted)
                    }
                )
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
            else -> lines.add(com.cyberlab.app.data.Terminal.exec(lab, cmd))
        }
        input = ""
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF03040A)).imePadding()) {
        TopBar(lab.title, onBack = { nav.popBackStack() }, trailing = {
            IconButton(onClick = {
                lines.clear(); lines.add("Session reset."); hintIdx = 0
            }) { Icon(Icons.Filled.Refresh, null, tint = C.Muted) }
        })
        LazyColumn(
            state = listState, modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(lines.toList()) { l ->
                Text(
                    l, fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 18.sp,
                    color = if (l.startsWith("analyst@")) Color.White else C.Green
                )
            }
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("help", "ls", "cat README.txt", "hint").forEach { c -> Pill(c, false, C.Green) { run(c) } }
        }
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 16.dp, end = 8.dp, bottom = 10.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = .07f)).padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("\$ ", color = C.Green, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
            BasicTextField(
                value = input, onValueChange = { input = it }, singleLine = true,
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 15.sp),
                cursorBrush = SolidColor(C.Green),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { run(input) }),
                modifier = Modifier.weight(1f).padding(vertical = 14.dp)
            )
            IconButton(onClick = { run(input) }) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = C.Red) }
        }
    }
}
