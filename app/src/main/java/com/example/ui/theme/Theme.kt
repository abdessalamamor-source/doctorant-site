package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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

private val UniversityDarkColorScheme = darkColorScheme(
    primary = AccentBlueLight,
    onPrimary = UniversityNavyDark,
    primaryContainer = UniversityNavyLight,
    onPrimaryContainer = Color.White,
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = Color(0xFF0B1325),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131F37),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our university palette for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> UniversityDarkColorScheme
        else -> UniversityLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
