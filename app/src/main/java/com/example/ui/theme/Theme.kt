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

private val DarkColorScheme = darkColorScheme(
    primary = AthleticBlueLight,
    onPrimary = Slate900,
    primaryContainer = AthleticBlueDark,
    onPrimaryContainer = Color.White,
    secondary = WarnOrangeLight,
    onSecondary = Slate900,
    secondaryContainer = WarnOrangeDark,
    onSecondaryContainer = Color.White,
    tertiary = RunGreenLight,
    onTertiary = Slate900,
    background = Slate900,
    onBackground = Slate50,
    surface = Slate800,
    onSurface = Slate50,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate200,
    outline = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary = AthleticBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = AthleticBlueDark,
    secondary = WarnOrange,
    onSecondary = Color.White,
    secondaryContainer = WarnOrangeBg,
    onSecondaryContainer = WarnOrangeDark,
    tertiary = RunGreen,
    onTertiary = Color.White,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200
)

@Composable
fun YoYoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize high-contrast athletic theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
