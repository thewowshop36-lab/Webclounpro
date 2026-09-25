package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DenvorkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = EmeraldLight,
    secondary = CyanAccent,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = CyanLight,
    tertiary = GoldWarning,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = GoldLight,
    background = DenvorkBackground,
    onBackground = TextPrimary,
    surface = DenvorkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DenvorkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DenvorkCardBorder,
    error = RoseDanger,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep consistent Denvork fintech branding
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DenvorkColorScheme,
        typography = Typography,
        content = content
    )
}
