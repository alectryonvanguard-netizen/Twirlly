package com.cyberlab.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object C {
    val Bg = Color(0xFF05060A)
    val Surface = Color(0xFF0E1018)
    val Surface2 = Color(0xFF161926)
    val Red = Color(0xFFFF1E2D)
    val DarkRed = Color(0xFF9B0A14)
    val Purple = Color(0xFF7C3AED)
    val Green = Color(0xFF22C55E)
    val Orange = Color(0xFFF97316)
    val Blue = Color(0xFF2F6BFF)
    val Gold = Color(0xFFFFB800)
    val Muted = Color(0xFF9AA0B4)
}

@Composable
fun CyberTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = C.Red, onPrimary = Color.White,
            background = C.Bg, onBackground = Color.White,
            surface = C.Surface, onSurface = Color.White,
            surfaceVariant = C.Surface2, onSurfaceVariant = C.Muted,
            secondary = C.Blue, tertiary = C.Gold, outline = Color(0xFF3A3F55)
        ),
        content = content
    )
}
