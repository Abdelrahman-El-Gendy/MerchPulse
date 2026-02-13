package com.merchpulse.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA), // Blue 400
    onPrimary = Color.Black,
    background = Color(0xFF0F172A), // Slate 900
    onBackground = Color(0xFFF8FAFC), // Slate 50
    surface = Color(0xFF1E293B), // Slate 800
    onSurface = Color(0xFFF1F5F9), // Slate 100
    outline = Color(0xFF334155), // Slate 700
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8) // Slate 400
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB), // Blue 600
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF0F172A), // Slate 900
    surface = Color(0xFFF1F5F9), // Slate 100
    onSurface = Color(0xFF111827), // Slate 900
    outline = Color(0xFFCBD5E1), // Slate 300
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF64748B) // Slate 500
)

@Composable
fun MerchPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
