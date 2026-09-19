package com.satwik.example.mutt_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil.compose.AsyncImage
import com.satwik.example.mutt_app.ui.components.ShimmerAsyncImage
import com.satwik.example.mutt_app.ui.components.TermsAndConditionsPopup
import com.satwik.example.mutt_app.ui.components.TranslatedText
import com.satwik.example.mutt_app.data.TranslationManager
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.satwik.example.mutt_app.data.MuttRepository
import com.satwik.example.mutt_app.ui.screens.*
import com.satwik.example.mutt_app.ui.theme.MUTT_APPTheme
import com.satwik.example.mutt_app.ui.theme.Terracotta
import com.satwik.example.mutt_app.ui.theme.GoldAccent
import com.satwik.example.mutt_app.ui.adminRoutes
import com.satwik.example.mutt_app.ui.adminMenuItems
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        askNotificationPermission()

        // Optimize Image Loading with larger cache and better networking
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .allowHardware(true)
            .respectCacheHeaders(false)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(250L * 1024 * 1024) // 250 MB cache
                    .build()
            }
            .okHttpClient {
                okhttp3.OkHttpClient.Builder()
                    .cache(okhttp3.Cache(cacheDir.resolve("http_cache"), 250L * 1024 * 1024))
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }
            .build()

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(coil.compose.LocalImageLoader provides imageLoader) {
                MUTT_APPTheme {
                    MainScreen()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Matha Updates"
            val descriptionText = "Notifications for Festivals, Panchanga and Events"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MATHA_NOTIFY", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.Rocket)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object About : Screen("about", "About Us", Icons.Default.Info)
    object GuruParampara : Screen("guru_parampara", "Guru Parampara", Icons.Default.HistoryEdu)
    object Events : Screen("events", "Events", Icons.Default.Event)
    object Sevas : Screen("sevas", "Sevas & Donations", Icons.Default.VolunteerActivism)
    object Panchanga : Screen("panchanga", "Daily Panchanga", Icons.Default.CalendarMonth)
    object Branches : Screen("branches", "Branches", Icons.Default.AccountTree)
    object Contact : Screen("contact", "Contact Us", Icons.Default.ContactSupport)
    object Photos : Screen("photos", "Photo Gallery", Icons.Default.Collections)
    object Shlokas : Screen("shlokas", "Divine Shlokas", Icons.Default.MenuBook)
    object Videos : Screen("videos", "Video Gallery", Icons.Default.VideoLibrary)
    object Publications : Screen("publications", "Publications", Icons.Default.MenuBook)
    object Membership : Screen("membership", "Member Registration", Icons.Default.Badge)
    object Volunteer : Screen("volunteer", "Volunteer Registration", Icons.Default.Groups)
    object Feedback : Screen("feedback", "Feedback", Icons.Default.RateReview)
    object Admin : Screen("admin", "Admin Panel", Icons.Default.AdminPanelSettings)
    object AlbumDetail : Screen("album_detail/{albumId}", "Album", Icons.Default.Collections)
    object Notifications : Screen("notifications", "Notifications", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val status by MuttRepository.connectionStatus.collectAsState()
    val homeConfig by MuttRepository.homeConfig.collectAsState()
    val notifications by MuttRepository.appNotifications.collectAsState(emptyList())
    val isKannada by MuttRepository.isLanguageKannada.collectAsState()
    val isModelDownloaded by TranslationManager.isModelDownloaded.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        TranslationManager.downloadModel()
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    // Terms and Conditions logic
    val prefs = remember { context.getSharedPreferences("mutt_app_prefs", Context.MODE_PRIVATE) }
    var termsAccepted by remember { mutableStateOf(prefs.getBoolean("terms_accepted", false)) }

    if (!termsAccepted) {
        TermsAndConditionsPopup(
            onAgree = {
                prefs.edit().putBoolean("terms_accepted", true).apply()
                termsAccepted = true
            }
        )
    }

    // Pre-fetch banner image for faster loading
    LaunchedEffect(homeConfig.bannerImageUrl) {
        if (homeConfig.bannerImageUrl.isNotEmpty()) {
            val request = ImageRequest.Builder(context)
                .data(homeConfig.bannerImageUrl)
                .precision(coil.size.Precision.INEXACT)
                .build()
            context.imageLoader.enqueue(request)
        }
    }
    var showPopupNotification by remember { mutableStateOf<com.satwik.example.mutt_app.data.AppNotification?>(null) }
    var lastSeenNotificationId by rememberSaveable { mutableStateOf(prefs.getString("last_notify_id", "")) }

    LaunchedEffect(notifications) {
        val latest = notifications.firstOrNull()
        if (latest != null && latest.id != lastSeenNotificationId) {
            val now = System.currentTimeMillis()
            // Only show if it's very recent (less than 2 mins old) to avoid historical popups
            if (now - latest.timestamp < 120000) {
                showPopupNotification = latest
                
                // Show System Notification
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                val builder = NotificationCompat.Builder(context, "MATHA_NOTIFY")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(latest.title)
                    .setContentText(latest.message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                try {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
                    }
                } catch (e: Exception) { /* Handle permission missing */ }

                lastSeenNotificationId = latest.id
                prefs.edit().putString("last_notify_id", latest.id).apply()
            }
        }
    }

    showPopupNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = { showPopupNotification = null },
            title = { Text(notification.title, fontWeight = FontWeight.Bold, color = Terracotta) },
            text = { Text(notification.message) },
            confirmButton = {
                Button(
                    onClick = { 
                        val type = notification.type
                        showPopupNotification = null 
                        when (type) {
                            "Panchanga" -> navController.navigate(Screen.Panchanga.route)
                            "Event" -> navController.navigate(Screen.Events.route)
                            else -> navController.navigate(Screen.Notifications.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) { Text("View Details", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showPopupNotification = null }) { Text("Close") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isHome = currentRoute == Screen.Home.route || currentRoute == null
    val isSplash = currentRoute == Screen.Splash.route
    val isAdminRoute = currentRoute == Screen.Admin.route
    
    // BACKSTACK SAFEGUARD: Prevent app from terminating if not on Home
    BackHandler(enabled = !isHome && !isSplash) {
        if (!navController.popBackStack()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    val menuItems = listOf(Screen.Home) + adminMenuItems + listOf(
        Screen.About,
        Screen.GuruParampara,
        Screen.Events,
        Screen.Sevas,
        Screen.Panchanga,
        Screen.Membership,
        Screen.Volunteer,
        Screen.Shlokas,
        Screen.Feedback,
        Screen.Branches,
        Screen.Photos,
        Screen.Videos,
        Screen.Publications,
        Screen.Contact
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Terracotta,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    menuItems.forEach { item ->
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        val contact by MuttRepository.contactConfig.collectAsState()
                        
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { TranslatedText(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item == Screen.Membership) {
                                    if (contact.membershipFormUrl.isNotEmpty()) {
                                        uriHandler.openUri(contact.membershipFormUrl)
                                    } else {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                } else if (item == Screen.Volunteer) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else if (item == Screen.Feedback) {
                                    if (contact.feedbackFormUrl.isNotEmpty()) {
                                        uriHandler.openUri(contact.feedbackFormUrl)
                                    }
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (!isAdminRoute && !isSplash) {
                    CenterAlignedTopAppBar(
                        title = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (homeConfig.appLogoUrl.isNotEmpty()) {
                                    ShimmerAsyncImage(
                                        model = homeConfig.appLogoUrl,
                                        contentDescription = "Logo",
                                        modifier = Modifier
                                            .size(38.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    stringResource(R.string.app_name).uppercase(), 
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        },
                        navigationIcon = {
                            if (isHome) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                            } else {
                                IconButton(onClick = { 
                                    if (!navController.navigateUp()) {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                                }
                            }
                        },
                        actions = {
                            // Language Toggle
                            IconButton(onClick = { MuttRepository.setLanguage(!isKannada) }) {
                                Surface(
                                    color = if (isKannada) GoldAccent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.2.dp, if(isKannada) GoldAccent else Color.White.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Translate, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(16.dp),
                                            tint = if (isKannada) GoldAccent else Color.White
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            if (isKannada) "KA" else "E",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isKannada) GoldAccent else Color.White,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }

                            if (status != "Connected") {
                                Surface(
                                    color = Color.Yellow.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        status.take(15) + if(status.length > 15) "..." else "", 
                                        color = Color.Yellow, 
                                        style = MaterialTheme.typography.labelSmall, 
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            if (isKannada && !isModelDownloaded) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp).size(16.dp),
                                    color = GoldAccent,
                                    strokeWidth = 2.dp
                                )
                            }

                            IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
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
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(if (isSplash) PaddingValues(0.dp) else innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier.padding(if (isSplash) PaddingValues(0.dp) else innerPadding),
                    enterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { -300 }, animationSpec = tween(400)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(400)) },
                    popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(400)) }
                ) {
                    composable(Screen.Splash.route) { SplashScreen(navController) }
                    composable(Screen.Home.route) { HomeScreen(navController) }
                    composable(Screen.About.route) { AboutScreen() }
                    composable(Screen.GuruParampara.route) { GuruParamparaScreen() }
                    composable(Screen.Events.route) { EventsScreen() }
                    composable(Screen.Sevas.route) { SevasScreen() }
                    composable(Screen.Panchanga.route) { PanchangaScreen() }
                    composable(Screen.Branches.route) { BranchesScreen() }
                    composable(Screen.Contact.route) { ContactScreen() }
                    composable(Screen.Photos.route) { PhotoGalleryScreen(navController) }
                    composable(Screen.AlbumDetail.route) { backStackEntry ->
                        val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
                        AlbumDetailScreen(navController, albumId)
                    }
                    composable(Screen.Shlokas.route) { ShlokasScreen() }
                    composable(Screen.Videos.route) { VideoGalleryScreen() }
                    composable(Screen.Publications.route) { PublicationsScreen() }
                    composable(Screen.Membership.route) { MembershipScreen() }
                    composable(Screen.Volunteer.route) { VolunteerScreen() }
                    composable(Screen.Notifications.route) { NotificationsScreen() }
                    adminRoutes(navController)
                }
            }
        }
    }
}
