package com.satwik.example.mutt_app.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MuttThemeConfig(
    val primaryColor: Color = Terracotta,
    val secondaryColor: Color = Sandstone,
    val backgroundColor: Color = Parchment,
    val surfaceColor: Color = Color.White,
    val deepBrown: Color = DeepSlate,
    val cornerRadius: Dp = 12.dp,
    val paddingScale: Dp = 16.dp
)

object ThemeConfig {
    var current = mutableStateOf(MuttThemeConfig())
    
    fun update(config: MuttThemeConfig) {
        current.value = config
    }
}

val LocalMuttTheme = staticCompositionLocalOf { MuttThemeConfig() }
