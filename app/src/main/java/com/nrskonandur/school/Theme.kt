package com.nrskonandur.school

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green = Color(0xFF0F3D2E)
val Green2 = Color(0xFF1F6F50)
val Cream = Color(0xFFF7F2E8)
val Paper = Color(0xFFFFFDF8)
val Saffron = Color(0xFFF4A62A)
val Clay = Color(0xFFD9593D)
val Mint = Color(0xFFD8EADF)
val Ink = Color(0xFF17211C)
val Muted = Color(0xFF59655E)

@Composable
fun NRSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green, onPrimary = Color.White,
            secondary = Saffron, onSecondary = Green,
            tertiary = Clay,
            background = Cream, onBackground = Ink,
            surface = Paper, onSurface = Ink,
            secondaryContainer = Mint, onSecondaryContainer = Green
        ),
        content = content
    )
}
