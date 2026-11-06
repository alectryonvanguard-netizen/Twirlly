package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
private fun Feature(icon: ImageVector, color: Color, l1: String, l2: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(56.dp).clip(CircleShape).background(color.copy(alpha = .35f))
                .border(1.5.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = Color.White) }
        Spacer(Modifier.height(8.dp))
        Text(l1, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(l2, color = C.Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AltButton(label: String, lead: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier.height(50.dp).clip(shape).background(C.Surface2)
            .border(1.dp, Color(0xFF3A3F55), shape).clickable { onClick() },
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
            Text(lead, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .statusBarsPadding().navigationBarsPadding().padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Security, null, tint = C.Red, modifier = Modifier.size(46.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Brand(28.sp)
                    Text("Learn · Practice · Stay Secure", color = C.Muted, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(36.dp))
            Box(Modifier.fillMaxWidth()) {
                Icon(
                    Icons.Filled.Security, null, tint = C.Red.copy(alpha = .18f),
                    modifier = Modifier.size(190.dp).align(Alignment.CenterEnd)
                )
                Column {
                    Text("Welcome to", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Row {
                        Text("CYBER", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)
                        Text("LAB", color = C.Red, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic)
                    }
                    Row(Modifier.padding(top = 12.dp).height(IntrinsicSize.Min)) {
                        Box(Modifier.width(3.dp).fillMaxHeight().background(C.Red))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Your Ultimate Cybersecurity\nLearning Platform", color = Color.White.copy(alpha = .9f),
                            fontSize = 20.sp, fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Learn skills, explore tools, and stay one step ahead in the digital world.",
                color = C.Muted, fontStyle = FontStyle.Italic, fontSize = 15.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(22.dp))
            GlowCard(accent = C.Purple) {
                Row(Modifier.fillMaxWidth()) {
                    Feature(Icons.Filled.School, C.Purple, "Learn", "Courses", Modifier.weight(1f))
                    Feature(Icons.Filled.Science, C.Green, "Practice", "Labs", Modifier.weight(1f))
                    Feature(Icons.Filled.Build, C.Orange, "Use", "Tools", Modifier.weight(1f))
                    Feature(Icons.Filled.VerifiedUser, C.Blue, "Stay", "Secure", Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(18.dp))
            GlowCard(accent = C.Gold, onClick = { provider = ""; askName = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.EmojiEvents, null, tint = C.Gold, modifier = Modifier.size(72.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Start Your Journey", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Build your skills, earn XP,\nand unlock achievements.", color = C.Muted, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, null, tint = C.Gold, modifier = Modifier.size(18.dp))
                            Text(" ${store.xp} XP", color = C.Gold, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(14.dp))
                            Icon(Icons.Filled.BarChart, null, tint = C.Green, modifier = Modifier.size(18.dp))
                            Text(" Level ${store.level}", color = C.Green, fontWeight = FontWeight.Bold)
                        }
                    }
                    Icon(Icons.Filled.KeyboardDoubleArrowRight, null, tint = C.Gold, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(22.dp))
            RedButton("Get Started", icon = Icons.Filled.RocketLaunch) { provider = ""; askName = true }
            Spacer(Modifier.height(14.dp))
            Text("or continue with", color = C.Muted, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AltButton("Google", "G", Modifier.weight(1f)) { provider = "Google"; askName = true }
                AltButton("GitHub", "Git", Modifier.weight(1f)) { provider = "GitHub"; askName = true }
                AltButton("Guest", "?", Modifier.weight(1f)) { store.signIn("Guest"); onDone() }
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, null, tint = C.Red, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("CYBERLAB · SECURE · EMPOWER", color = C.Muted, fontSize = 12.sp, letterSpacing = 2.sp)
            }
        }
    }

    if (askName) {
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { askName = false },
            containerColor = C.Surface,
            title = {
                Text(if (provider.isEmpty()) "Create your profile" else "Continue with $provider", color = Color.White)
            },
            text = {
                Column {
                    Text("Pick a callsign. Progress is stored on this device.", color = C.Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input, onValueChange = { input = it }, singleLine = true,
                        label = { Text("Callsign") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    store.signIn(input.ifBlank { "Hacker" }); askName = false; onDone()
                }) { Text("Start", color = C.Red) }
            },
            dismissButton = { TextButton(onClick = { askName = false }) { Text("Cancel", color = C.Muted) } }
        )
    }
}
