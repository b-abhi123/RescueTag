package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SosNeonGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B3D25),
    onPrimaryContainer = SosNeonGreen,
    secondary = SosWarningAmber,
    onSecondary = Color.Black,
    tertiary = SosMedicalBlue,
    onTertiary = Color.White,
    error = SosEmergencyRed,
    onError = Color.White,
    background = SosDarkCanvas,
    onBackground = TextPrimaryDark,
    surface = SosCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF23252C),
    onSurfaceVariant = TextSecondaryDark,
    outline = SosCardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = LightNeonGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = LightWarningAmber,
    onSecondary = Color.White,
    tertiary = LightMedicalBlue,
    onTertiary = Color.White,
    error = LightEmergencyRed,
    onError = Color.White,
    background = LightCanvas,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = LightSurfaceBorder
)

@Composable
fun RescueTagTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
