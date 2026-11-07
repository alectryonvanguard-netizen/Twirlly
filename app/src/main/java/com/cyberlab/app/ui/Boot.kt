package com.cyberlab.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** The CyberLab logo: a red shield with a keyhole and blue circuit lines. */
@Composable
fun LogoMark(modifier: Modifier = Modifier, reveal: Float = 1f) {
    Canvas(modifier) {
        val s = size.minDimension / 108f
        val shield = Path().apply {
            moveTo(54f * s, 22f * s)
            lineTo(80f * s, 31f * s)
            lineTo(80f * s, 54f * s)
            cubicTo(80f * s, 70f * s, 69f * s, 81f * s, 54f * s, 88f * s)
            cubicTo(39f * s, 81f * s, 28f * s, 70f * s, 28f * s, 54f * s)
            lineTo(28f * s, 31f * s)
            close()
        }
        drawPath(
            shield,
            Brush.linearGradient(
                listOf(Color(0xFFFF4D5A), Color(0xFF9B0A14)),
                start = Offset(28f * s, 22f * s), end = Offset(80f * s, 88f * s)
            ),
            alpha = reveal
        )
        drawPath(shield, Color.White.copy(alpha = reveal), style = Stroke(width = 2f * s))
        val blue = Color(0xFF2F6BFF)
        listOf(
            Offset(28f, 44f) to Offset(14f, 44f), Offset(80f, 44f) to Offset(94f, 44f),
            Offset(28f, 60f) to Offset(20f, 60f), Offset(80f, 60f) to Offset(88f, 60f)
        ).forEach { (a, b) ->
            drawLine(blue, Offset(a.x * s, a.y * s), Offset(b.x * s, b.y * s), strokeWidth = 2.5f * s, cap = StrokeCap.Round, alpha = reveal)
        }
        drawCircle(Color.White, radius = 6f * s, center = Offset(54f * s, 50f * s), alpha = reveal)
        val key = Path().apply {
            moveTo(51f * s, 54f * s); lineTo(57f * s, 54f * s)
            lineTo(60f * s, 68f * s); lineTo(48f * s, 68f * s); close()
        }
        drawPath(key, Color.White, alpha = reveal)
    }
}

@Composable
fun BootScreen(onFinished: () -> Unit) {
    val reveal = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    var step by remember { mutableIntStateOf(0) }
    val msgs = listOf("Initializing secure core", "Loading courses and labs", "Hardening sandbox", "Ready")

    LaunchedEffect(Unit) {
        launch { reveal.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
        launch { delay(500); textAlpha.animateTo(1f, tween(600)) }
        for (i in msgs.indices) {
            step = i
            delay(520)
        }
        delay(200)
        onFinished()
    }

    ScreenBg {
        Column(
            Modifier.fillMaxSize().navigationBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier.size(220.dp).drawBehind {
                    drawCircle(
                        Brush.radialGradient(listOf(C.Red.copy(alpha = .35f * reveal.value), Color.Transparent)),
                        radius = size.minDimension / 2f
                    )
                },
                contentAlignment = Alignment.Center
            ) {
                LogoMark(
                    Modifier.size(170.dp).graphicsLayer {
                        val sc = 0.7f + 0.3f * reveal.value
                        scaleX = sc; scaleY = sc
                    },
                    reveal = reveal.value
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.graphicsLayer { alpha = textAlpha.value }) {
                Text("CYBER", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
                Text("LAB", color = C.Red, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
            }
            Text(
                "LEARN · PRACTICE · STAY SECURE", color = C.Muted, fontSize = 11.sp, letterSpacing = 2.sp,
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            )
            Spacer(Modifier.height(48.dp))
            Text(
                "> " + msgs[step] + if (step < msgs.lastIndex) "..." else "",
                color = C.Green, fontFamily = FontFamily.Monospace, fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (step + 1) / msgs.size.toFloat() },
                modifier = Modifier.width(180.dp).height(3.dp).clip(CircleShape),
                color = C.Red, trackColor = Color.White.copy(alpha = .1f)
            )
        }
    }
}
