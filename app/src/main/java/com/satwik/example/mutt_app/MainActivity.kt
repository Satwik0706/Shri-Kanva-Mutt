package com.satwik.example.mutt_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.satwik.example.mutt_app.ui.screens.*
import com.satwik.example.mutt_app.ui.theme.MUTT_APPTheme
import com.satwik.example.mutt_app.ui.theme.Terracotta
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MUTT_APPTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object About : Screen("about", "About Us", Icons.Default.Info)
    object GuruParampara : Screen("guru_parampara", "Guru Parampara", Icons.Default.HistoryEdu)
    object Events : Screen("events", "Events", Icons.Default.Event)
    object Sevas : Screen("sevas", "Sevas & Donations", Icons.Default.VolunteerActivism)
    object Panchanga : Screen("panchanga", "Daily Panchanga", Icons.Default.CalendarMonth)
    object Branches : Screen("branches", "Branches", Icons.Default.AccountTree)
    object Contact : Screen("contact", "Contact Us", Icons.Default.ContactSupport)
    object Admin : Screen("admin", "Admin Panel", Icons.Default.AdminPanelSettings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val menuItems = listOf(
        Screen.Home,
        Screen.About,
        Screen.GuruParampara,
        Screen.Events,
        Screen.Sevas,
        Screen.Panchanga,
        Screen.Branches,
        Screen.Contact,
        Screen.Admin
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Sri Kanva Matha",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Terracotta,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "SRI KANVA MATHA", 
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Terracotta,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.About.route) { AboutScreen() }
                composable(Screen.GuruParampara.route) { GuruParamparaScreen() }
                composable(Screen.Events.route) { EventsScreen() }
                composable(Screen.Sevas.route) { SevasScreen() }
                composable(Screen.Panchanga.route) { PanchangaScreen() }
                composable(Screen.Branches.route) { BranchesScreen() }
                composable(Screen.Contact.route) { ContactScreen() }
                composable(Screen.Admin.route) { AdminPanelScreen() }
            }
        }
    }
}
