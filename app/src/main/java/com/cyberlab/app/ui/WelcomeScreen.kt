package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberlab.app.data.Store

@Composable
private fun Feature(icon: ImageVector, color: Color, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(58.dp).clip(CircleShape).background(color.copy(alpha = .20f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = color, modifier = Modifier.size(28.dp)) }
        Spacer(Modifier.height(8.dp))
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AltButton(label: String, lead: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier.height(52.dp).clip(CircleShape).background(Color.White.copy(alpha = .08f)).clickable { onClick() },
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
            Text(lead, color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WelcomeScreen(store: Store, onDone: () -> Unit) {
    var askName by remember { mutableStateOf(false) }
    var provider by remember { mutableStateOf("") }

    ScreenBg {
        LogoMark
                (Modifier.size(48.dp))
            modifier = Modifier.size(360.dp).align(Alignment.TopEnd).offset(x = 90.dp, y = 60.dp)
        )
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoMark(Modifier.size(48.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Brand(24.sp)
                    Text("Learn · Practice · Stay Secure", color = C.Muted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(64.dp))
            Text("Welcome to", color = Color.White.copy(alpha = .85f), fontSize = 26.sp, fontWeight = FontWeight.Medium)
            Row {
                Text("CYBER", color = Color.White, fontSize = 58.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic, letterSpacing = (-2).sp)
                Text("LAB", color = C.Red, fontSize = 58.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic, letterSpacing = (-2).sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Your ultimate cybersecurity learning platform. Master skills, run labs, earn XP.",
                color = C.Muted, fontSize = 16.sp, lineHeight = 23.sp
            )
            Spacer(Modifier.height(36.dp))
            Row(Modifier.fillMaxWidth()) {
                Feature(Icons.Filled.School, C.Purple, "Learn", Modifier.weight(1f))
                Feature(Icons.Filled.Science, C.Green, "Practice", Modifier.weight(1f))
                Feature(Icons.Filled.Build, C.Orange, "Tools", Modifier.weight(1f))
                Feature(Icons.Filled.VerifiedUser, C.Blue, "Secure", Modifier.weight(1f))
            }
            Spacer(Modifier.height(40.dp))
            RedButton("Get Started", icon = Icons.Filled.RocketLaunch) { provider = ""; askName = true }
            Spacer(Modifier.height(22.dp))
            Text("or continue with", color = C.Muted, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AltButton("Google", "G", Modifier.weight(1f)) { provider = "Google"; askName = true }
                AltButton("GitHub", "Git", Modifier.weight(1f)) { provider = "GitHub"; askName = true }
                AltButton("Guest", "?", Modifier.weight(1f)) { store.signIn("Guest"); onDone() }
            }
            Spacer(Modifier.height(32.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, tint = C.Red, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("CYBERLAB · SECURE · EMPOWER", color = C.Muted, fontSize = 11.sp, letterSpacing = 2.sp)
            }
        }
    }

    if (askName) {
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { askName = false },
            containerColor = C.Surface2,
            title = { Text(if (provider.isEmpty()) "Create your profile" else "Continue with $provider", color = Color.White) },
            text = {
                Column {
                    Text("Pick a callsign. Progress is stored on this device.", color = C.Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    TextField(
                        value = input, onValueChange = { input = it }, singleLine = true,
                        placeholder = { Text("Callsign") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = .08f),
                            unfocusedContainerColor = Color.White.copy(alpha = .08f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { store.signIn(input.ifBlank { "Hacker" }); askName = false; onDone() }) {
                    Text("Start", color = C.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { askName = false }) { Text("Cancel", color = C.Muted) } }
        )
    }
}
