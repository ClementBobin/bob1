package com.mirage.bob1.ui.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.kindling.core.theme.KindlingShapes
import dev.kindling.core.theme.KindlingTheme

// Tokens taken from the design system (src/index.css of the Figma export).
private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFFE86602), onPrimary = Color(0xFFFAF7F5),
    secondary = Color(0xFF056037), onSecondary = Color(0xFFFAF7F5),
    secondaryContainer = Color(0xFFCCE5D6), onSecondaryContainer = Color(0xFF0A4527),
    tertiary = Color(0xFF2F6FED), onTertiary = Color.White,
    background = Color(0xFFFBFAF9), onBackground = Color(0xFF211B17),
    surface = Color(0xFFFFFFFF), onSurface = Color(0xFF211B17),
    surfaceVariant = Color(0xFFF0EAE3), onSurfaceVariant = Color(0xFF6B645C),
    outline = Color(0xFFDCD6D0), outlineVariant = Color(0xFFEAE4DD),
    error = Color(0xFFDF202E), onError = Color.White,
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A33), onPrimary = Color(0xFF080503),
    secondary = Color(0xFF9DDDB8), onSecondary = Color(0xFF080503),
    secondaryContainer = Color(0xFF0A2A1A), onSecondaryContainer = Color(0xFF9DDDB8),
    tertiary = Color(0xFF7FA6FF), onTertiary = Color(0xFF080503),
    background = Color(0xFF080503), onBackground = Color(0xFFFAF7F5),
    surface = Color(0xFF110B08), onSurface = Color(0xFFFAF7F5),
    surfaceVariant = Color(0xFF211711), onSurfaceVariant = Color(0xFFAAA099),
    outline = Color(0xFF3A322C), outlineVariant = Color(0xFF2A221D),
    error = Color(0xFFF94144), onError = Color(0xFF080503),
)

/** Wraps Kindling's theme so every Kindling component picks up the BOB1 palette. */
@Composable
fun Bob1Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    KindlingTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = KindlingShapes(base = 14.dp),
        content = content,
    )
}
