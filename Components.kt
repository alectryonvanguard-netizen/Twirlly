package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberlab.app.data.Store

@Composable
fun ScreenBg(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(C.Bg)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(C.Red.copy(alpha = .22f), Color.Transparent),
                        center = Offset(size.width, 0f), radius = size.width * .9f
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        listOf(C.Blue.copy(alpha = .20f), Color.Transparent),
                        center = Offset(0f, size.height * .12f), radius = size.width * .9f
                    )
                )
            },
        content = content
    )
}

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    accent: Color = C.Red,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(accent.copy(alpha = .16f), C.Surface)))
            .border(1.dp, accent.copy(alpha = .55f), shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun RedButton(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, onClick: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(C.DarkRed, C.Red, C.DarkRed)))
            .border(1.dp, Color(0xFFFF6A73), shape)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            androidx.compose.material3.Icon(icon, null, tint = Color.White)
            Spacer(Modifier.width(10.dp))
        }
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun Pill(label: String, selected: Boolean, color: Color = C.Red, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) color.copy(alpha = .25f) else C.Surface2)
            .border(1.dp, if (selected) color else Color(0xFF2A2E42), shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (selected) Color.White else C.Muted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun Tag(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = .2f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) { Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
}

@Composable
fun Brand(size: TextUnit = 26.sp) {
    Row {
        Text("CYBER", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = size)
        Text("LAB", color = C.Red, fontWeight = FontWeight.ExtraBold, fontSize = size)
    }
}

@Composable
fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        Text(
            title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun XpCard(store: Store) {
    GlowCard(accent = C.Gold) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Icon(
                Icons.Filled.EmojiEvents, null, tint = C.Gold, modifier = Modifier.size(52.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Level ${store.level} · ${store.rank}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { store.xpIntoLevel / 200f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = C.Gold, trackColor = C.Surface2
                )
                Spacer(Modifier.height(6.dp))
                Text("${store.xp} XP · ${200 - store.xpIntoLevel} XP to next level", color = C.Muted, fontSize = 12.sp)
            }
        }
    }
}
