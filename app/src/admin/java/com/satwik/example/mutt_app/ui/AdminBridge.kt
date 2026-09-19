package com.satwik.example.mutt_app.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.satwik.example.mutt_app.Screen
import com.satwik.example.mutt_app.ui.screens.AdminPanelScreen

fun NavGraphBuilder.adminRoutes(navController: NavHostController) {
    composable(Screen.Admin.route) {
        AdminPanelScreen(navController)
    }
}

val adminMenuItems: List<Screen> = listOf(Screen.Admin)
