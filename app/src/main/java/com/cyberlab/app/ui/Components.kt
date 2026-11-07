package com.cyberlab.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberlab.app.data.Level

fun listPad() = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp)

fun levelColors(l: Level): Pair<Color, Color> = when (l) {
    Level.BEGINNER -> Color(0xFF16A34A) to Color(0xFF0F766E)
    Level.INTERMEDIATE -> Color(0xFF2F6BFF) to Color(0xFF4F46E5)
    Level.ADVANCED -> Color(0xFFF97316) to Color(0xFFDC2626)
    Level.EXPERT -> Color(0xFFFF1E2D) to Color(0xFF6D28D9)
}

fun courseIcon(id: String): ImageVector = when (id) {
    "c1" -> Icons.Filled.Security
    "c2" -> Icons.Filled.Wifi
    "c3" -> Icons.Filled.Search
    "c4" -> Icons.Filled.Language
    "c5" -> Icons.Filled.BugReport
    "c6" -> Icons.Filled.Dns
    "c7" -> Icons.Filled.Groups
    else -> Icons.Filled.Memory
}

@Composable
fun ScreenBg(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .fillMaxSize()
            .background(C.Bg)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(C.Red.copy(alpha = .20f), Color.Transparent),
                        center = Offset(size.width, 0f), radius = size.width * .95f
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        listOf(C.Blue.copy(alpha = .16f), Color.Transparent),
                        center = Offset(0f, size.height * .35f), radius = size.width * .9f
                    )
                )
            },
        content = content
    )
}

@Composable
fun Hairline(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .07f)))
}

@Composable
fun ScreenTitle(label: String, title: String, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(label.uppercase(), color = C.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(title, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
        }
        trailing()
    }
}

@Composable
fun SectionTitle(text: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) {
            Text(action, color = C.Red, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onAction() })
        }
    }
}

@Composable
fun ListRow(
    icon: ImageVector, tint: Color, title: String, subtitle: String? = null,
    onClick: (() -> Unit)? = null, trailing: @Composable (() -> Unit)? = null
) {
    Column {
        Row(
            Modifier.fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(tint.copy(alpha = .16f)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(subtitle, color = C.Muted, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            if (trailing != null) trailing()
        }
        Hairline(Modifier.padding(start = 58.dp))
    }
}

@Composable
fun PosterTile(
    title: String, sub: String, c1: Color, c2: Color, icon: ImageVector, progress: Float, onClick: () -> Unit
) {
    Box(
        Modifier.width(210.dp).height(150.dp).clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(c1, c2)))
            .clickable { onClick() }
    ) {
        Icon(
            icon, null, tint = Color.White.copy(alpha = .18f),
            modifier = Modifier.size(110.dp).align(Alignment.TopEnd).offset(x = 20.dp, y = (-14).dp)
        )
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text(sub.uppercase(), color = Color.White.copy(alpha = .75f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 21.sp, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = .25f))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f, 1f)).background(Color.White))
            }
        }
    }
}

@Composable
fun Pill(label: String, selected: Boolean, color: Color = C.Red, onClick: () -> Unit) {
    Box(
        Modifier.clip(CircleShape)
            .background(if (selected) color else Color.White.copy(alpha = .07f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(label, color = if (selected) Color.White else C.Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Kicker(text: String, color: Color = C.Red) {
    Text(text.uppercase(), color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
}

@Composable
fun Brand(size: androidx.compose.ui.unit.TextUnit = 26.sp) {
    Row {
        Text("CYBER", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = size)
        Text("LAB", color = C.Red, fontWeight = FontWeight.ExtraBold, fontSize = size)
    }
}

@Composable
fun RedButton(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, onClick: () -> Unit) {
    Row(
        modifier.fillMaxWidth().height(56.dp).clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(Color(0xFFB4081A), C.Red, Color(0xFFFF4D5A))))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = Color.White)
            Spacer(Modifier.width(10.dp))
        }
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

@Composable
fun TopBar(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
        Text(
            title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
fun SearchField(value: String, hint: String, onChange: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(top = 14.dp).clip(CircleShape)
            .background(Color.White.copy(alpha = .07f)).padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Search, null, tint = C.Muted)
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = value, onValueChange = onChange, singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
            cursorBrush = SolidColor(C.Red), modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(hint, color = C.Muted, fontSize = 15.sp)
                    inner()
                }
            }
        )
    }
}

@Composable
fun Ring(progress: Float, size: Dp, color: Color, stroke: Dp = 8.dp, content: @Composable () -> Unit = {}) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), animationSpec = tween(900), label = "ring")
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(stroke / 2)) {
            val st = Stroke(stroke.toPx(), cap = StrokeCap.Round)
            drawArc(Color.White.copy(alpha = .10f), -90f, 360f, false, style = st)
            drawArc(color, -90f, 360f * p, false, style = st)
        }
        content()
    }
}

@Composable
fun StreakBadge(n: Int) {
    Row(
        Modifier.clip(CircleShape).background(C.Orange.copy(alpha = .15f)).padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.LocalFireDepartment, null, tint = C.Orange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("$n", color = C.Orange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
