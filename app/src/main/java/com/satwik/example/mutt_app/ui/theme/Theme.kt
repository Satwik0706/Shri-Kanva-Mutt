package com.satwik.example.mutt_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColorScheme = lightColorScheme(
        primary = config.primaryColor,
        secondary = config.secondaryColor,
        background = config.backgroundColor,
        surface = config.surfaceColor,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = config.deepBrown,
        onSurface = config.deepBrown
    )

    val darkColorScheme = darkColorScheme(
        primary = config.primaryColor,
        secondary = config.secondaryColor,
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    CompositionLocalProvider(LocalMuttTheme provides config) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
