package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UniversityLightColorScheme = lightColorScheme(
    primary = UniversityNavy,
    onPrimary = Color.White,
    primaryContainer = UniversityNavyLight,
    onPrimaryContainer = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = SoftBlueBg,
    onSecondaryContainer = UniversityNavyDark,
    tertiary = AcademicGoldDark,
    onTertiary = Color.White,
    tertiaryContainer = AcademicGoldLight,
    onTertiaryContainer = Color(0xFF451A03),
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftBlueBg,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UniversityLightColorScheme,
        typography = Typography,
        content = content
    )
}
