package com.satwik.example.mutt_app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MUTT_APPTheme(
    config: MuttThemeConfig = ThemeConfig.current.value,
    content: @Composable () -> Unit
) {
    // Force Light Color Scheme for production consistency regardless of system theme
    val colorScheme = lightColorScheme(
        primary = config.primaryColor,
        secondary = config.secondaryColor,
        background = config.backgroundColor,
        surface = config.surfaceColor,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = config.deepBrown,
        onSurface = config.deepBrown
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        // Use light icons (isAppearanceLightStatusBars = false) on dark Terracotta background
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    CompositionLocalProvider(LocalMuttTheme provides config) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
