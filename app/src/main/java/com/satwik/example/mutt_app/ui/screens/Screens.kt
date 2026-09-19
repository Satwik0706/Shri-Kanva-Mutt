package com.satwik.example.mutt_app.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.satwik.example.mutt_app.ui.components.ShimmerAsyncImage
import com.satwik.example.mutt_app.ui.components.TranslatedText
import com.satwik.example.mutt_app.Screen
import com.satwik.example.mutt_app.data.*
import com.satwik.example.mutt_app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val config by MuttRepository.homeConfig.collectAsState()
    val announcements by MuttRepository.announcements.collectAsState(emptyList())
    val shlokas by MuttRepository.shlokas.collectAsState(emptyList())
    val events by MuttRepository.events.collectAsState(emptyList())
    val socialLinks by MuttRepository.socialLinks.collectAsState(emptyList())
    val news by MuttRepository.news.collectAsState(emptyList())
    val specialEvents by MuttRepository.specialEvents.collectAsState(emptyList())
    val festivals by MuttRepository.festivals.collectAsState(emptyList())
    
    var hasShownPopupInSession by rememberSaveable { mutableStateOf(false) }
    var showEventPopup by remember { mutableStateOf(false) }
    val latestEvent = events.maxByOrNull { it.startDate }

    LaunchedEffect(events) {
        if (events.isNotEmpty() && !hasShownPopupInSession) {
            showEventPopup = true
            hasShownPopupInSession = true
        }
    }

    if (showEventPopup && latestEvent != null) {
        EventPopup(event = latestEvent, onDismiss = { showEventPopup = false })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HeroBanner(config) }
        item { QuickLinksSection(navController) }

        if (news.isNotEmpty() || specialEvents.isNotEmpty()) {
            item { NewsSection(news, specialEvents) }
        }

        if (shlokas.isNotEmpty() || config.dailyWisdom.isNotBlank()) {
            item { 
                TranslatedText("Daily Wisdom & Shlokas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, color = Terracotta)
                DailyWisdomCard(config.dailyWisdom)
                TextButton(onClick = { navController.navigate(Screen.Shlokas.route) }, modifier = Modifier.padding(start = 16.dp)) {
                    TranslatedText("Explore More Shlokas", color = Terracotta, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Terracotta)
                }
            }
        }

        if (announcements.isNotEmpty()) {
            item(key = "announcements_section") { LatestHappeningsSection(announcements) }
        }

        if (events.isNotEmpty() || festivals.isNotEmpty()) {
            item(key = "festivals_section") { FestivalSection(navController, events, festivals) }
        }
        
        item(key = "sevas_section") { 
            TranslatedText("Featured Sevas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, color = Terracotta)
            FeaturedSevasSection(navController)
        }

        item(key = "footer") { AppFooter(navController) }
    }
}

@Composable
fun HeroBanner(config: HomeConfig) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .background(Brush.verticalGradient(listOf(Terracotta, RichBrown)))
    ) {
        if (config.bannerImageUrl.isNotEmpty()) {
            ShimmerAsyncImage(
                model = config.bannerImageUrl, 
                contentDescription = "Banner", 
                modifier = Modifier.fillMaxSize(), 
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(Color.Transparent, DeepBlack.copy(alpha = 0.85f)),
                startY = 300f
            )
        ))
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Bottom) {
            Surface(color = GoldAccent, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                Text("OFFICIAL APP", color = RichBrown, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
                Text(
                    text = config.bannerTitle, 
                    style = MaterialTheme.typography.headlineSmall, 
                    color = Color.White, 
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 30.sp
                )
                Text(
                    text = config.bannerSubtitle, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
        }
    }
}

@Composable
fun QuickLinksSection(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val config by MuttRepository.homeConfig.collectAsState()
    val contact by MuttRepository.contactConfig.collectAsState()

    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            QuickLinkItem("Donate", Icons.Default.VolunteerActivism, Color(0xFF4CAF50)) { navController.navigate(Screen.Sevas.route) }
            QuickLinkItem("Events", Icons.Default.Event, Color(0xFF2196F3)) { navController.navigate(Screen.Events.route) }
            QuickLinkItem("Gallery", Icons.Default.Collections, Color(0xFF9C27B0)) { navController.navigate(Screen.Photos.route) }
            QuickLinkItem("Live", Icons.Default.LiveTv, Color.Red) { if (config.liveDarshanUrl.isNotEmpty()) uriHandler.openUri(config.liveDarshanUrl) }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            QuickLinkItem("Shlokas", Icons.Default.MenuBook, Color(0xFFFF5722)) { navController.navigate(Screen.Shlokas.route) }
            QuickLinkItem("Panchanga", Icons.Default.CalendarMonth, Color(0xFF3F51B5)) { navController.navigate(Screen.Panchanga.route) }
            QuickLinkItem("Videos", Icons.Default.VideoLibrary, Color(0xFF2E7D32)) { navController.navigate(Screen.Videos.route) }
            QuickLinkItem("Contact", Icons.Default.ContactSupport, Color(0xFF795548)) { navController.navigate(Screen.Contact.route) }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(16.dp))
            QuickLinkItem("Books", Icons.Default.LibraryBooks, Color(0xFF607D8B)) { navController.navigate(Screen.Publications.route) }
        }
    }
}

@Composable
fun QuickLinkItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, animationSpec = tween(100))
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(64.dp), 
            shape = RoundedCornerShape(16.dp), 
            color = color.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        TranslatedText(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RichBrown, textAlign = TextAlign.Center)
    }
}

@Composable
fun NewsSection(news: List<News>, special: List<SpecialEvent>) {
    Column(modifier = Modifier.padding(16.dp)) {
        TranslatedText("Daily Updates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(12.dp))
        special.find { it.isHighlighted }?.let { event ->
            SpecialEventCard(event)
            Spacer(modifier = Modifier.height(16.dp))
        }
        news.take(3).forEach { item ->
            NewsCard(item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun NewsCard(item: News) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { if(item.link.isNotEmpty()) uriHandler.openUri(item.link) }, 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.headline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RichBrown)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = Color.Gray, lineHeight = 18.sp)
            Row(modifier = Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(item.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun SpecialEventCard(event: SpecialEvent) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Terracotta), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                TranslatedText("Special Announcement", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (event.imageUrl.isNotEmpty()) {
                ShimmerAsyncImage(model = event.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(event.title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(event.content, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun FestivalSection(navController: NavController, events: List<Event>, festivals: List<Festival>) {
    Column(modifier = Modifier.padding(16.dp)) {
        TranslatedText("Festivals & Events", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(festivals.take(3), key = { "fest_${it.id}" }) { festival ->
                FestivalPremiumCard(festival)
            }
            items(events.take(3), key = { "event_${it.id}" }) { event ->
                EventPremiumMiniCard(event) { navController.navigate(Screen.Events.route) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { navController.navigate(Screen.Events.route) }) {
            Text("View All Events", color = Terracotta, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Terracotta)
        }
    }
}

@Composable
fun FestivalPremiumCard(festival: Festival) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(festival.name, style = MaterialTheme.typography.titleLarge, color = GoldAccent, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            EventDetailRow(Icons.Default.CalendarToday, festival.dateTime)
            Text(
                festival.description, 
                style = MaterialTheme.typography.bodySmall, 
                color = Color.White, 
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun EventPremiumMiniCard(event: Event, onClick: () -> Unit) {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    Card(
        modifier = Modifier.width(280.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            EventDetailRow(Icons.Default.Event, sdf.format(java.util.Date(event.startDate)))
            if (event.location.isNotEmpty()) {
                EventDetailRow(Icons.Default.LocationOn, event.location)
            }
            if (event.description.isNotEmpty()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DailyWisdomCard(wisdom: String) {
    if (wisdom.isBlank()) return
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = Terracotta, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(wisdom, style = MaterialTheme.typography.bodyLarge, color = RichBrown, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable fun ShlokasScreen() {
    val items by MuttRepository.shlokas.collectAsState(emptyList())
    val uriHandler = LocalUriHandler.current
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        Text("Divine Shlokas", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No Shlokas available.", color = Color.Gray) } }
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(items) { shloka ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(shloka.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(shloka.content, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                            if (shloka.pdfUrl.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { uriHandler.openUri(shloka.pdfUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("View / Download PDF", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventPopup(event: Event, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = DeepBlack),
            border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Box(modifier = Modifier.background(Brush.verticalGradient(listOf(RichBrown, DeepBlack)))) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            "UPCOMING EVENT",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    if (event.imageUrl.isNotEmpty()) {
                        ShimmerAsyncImage(
                            model = event.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(sdf.format(java.util.Date(event.startDate)), style = MaterialTheme.typography.bodySmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }

                    if (event.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White, // Brighter white
                            lineHeight = 20.sp
                        )
                    }

                    if (event.galleryImageUrls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            items(event.galleryImageUrls) { url ->
                                ShimmerAsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (event.registrationUrl.isNotEmpty()) {
                        Button(
                            onClick = { uriHandler.openUri(event.registrationUrl); onDismiss() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Register Now", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Dismiss", color = Color.Gray)
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun LatestHappeningsSection(items: List<Announcement>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        TranslatedText("Latest Announcements", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items, key = { "ann_${it.id}" }) { item -> AnnouncementCard(item) }
        }
    }
}

@Composable
fun AnnouncementCard(item: Announcement) {
    Card(
        modifier = Modifier.width(260.dp), 
        shape = RoundedCornerShape(16.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(0.5.dp, CardBorder)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFFF5F5F5))) {
                if (item.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                else Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.content, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 2, color = RichBrown)
                Spacer(modifier = Modifier.height(8.dp))
                Text(java.text.SimpleDateFormat("dd MMM yyyy").format(java.util.Date(item.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun FeaturedSevasSection(navController: NavController) {
    val sevas by MuttRepository.sevas.collectAsState(emptyList())
    val registrations by MuttRepository.userRegistrations.collectAsState(emptyList())
    
    val featuredSevas = remember(sevas) { sevas.filter { it.isFeatured }.take(2).ifEmpty { sevas.take(2) } }

    Column {
        if (featuredSevas.isEmpty()) {
            Text("No Sevas available currently.", modifier = Modifier.padding(16.dp), color = Color.Gray)
        } else {
            featuredSevas.forEach { seva ->
                val regStatus = registrations.find { it.itemId == seva.id }?.status ?: "Open"
                SevaRegCard(seva, regStatus)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (sevas.size > 2) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Sevas.route) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Terracotta),
                    border = BorderStroke(1.dp, Terracotta)
                ) {
                    Text("Know More / View All Sevas")
                }
            }
        }
    }
}

@Composable
fun SevaRegCard(seva: Seva, status: String) {
    var showForm by remember { mutableStateOf(false) }
    var showPayment by remember { mutableStateOf(false) }
    var showUtrEntry by remember { mutableStateOf(false) }
    var showPostPayment by remember { mutableStateOf(false) }
    var savedFormResponses by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var savedPhone by remember { mutableStateOf("") }
    
    val contact by MuttRepository.contactConfig.collectAsState()
    val uriHandler = LocalUriHandler.current

    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Surface(color = RichBrown, shape = RoundedCornerShape(4.dp)) {
                    Text(seva.tag, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(seva.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RichBrown)
                if (seva.amount > 0) {
                    Text(
                        text = "Amount: ₹${seva.amount}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                if (seva.description.isNotEmpty()) {
                    Text(
                        text = seva.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
                
                Button(
                    onClick = { showForm = true }, 
                    modifier = Modifier.fillMaxWidth().graphicsLayer(scaleX = scale, scaleY = scale).height(44.dp), 
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = interactionSource
                ) { 
                    Box(
                        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Terracotta, RichBrown)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        TranslatedText("Register Now", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White) 
                    }
                }

                if (status == "Verified") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { uriHandler.openUri("tel:${contact.phone}") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call for Confirmation", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
            Box(modifier = Modifier.width(100.dp).fillMaxHeight().background(Color(0xFFF5F5F5))) { 
                if (seva.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = seva.imageUrl, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize(), contentDescription = null) 
            }
        }
    }

    if (showForm) {
        SevaFormDialog(
            seva = seva,
            onDismiss = { showForm = false },
            onFormSubmitted = { phone, responses ->
                savedPhone = phone
                savedFormResponses = responses
                showForm = false
                showPayment = true 
            }
        )
    }

    if (showPayment) {
        PaymentQrDialog(
            seva = seva,
            onDismiss = { 
                showPayment = false 
                showUtrEntry = true
            }
        )
    }
    
    if (showUtrEntry) {
        val scope = rememberCoroutineScope()
        UtrEntryDialog(
            onDismiss = { showUtrEntry = false },
            onSubmit = { utr ->
                scope.launch {
                    MuttRepository.registerWithForm(
                        itemId = seva.id, 
                        itemTitle = seva.title, 
                        phone = savedPhone, 
                        responses = savedFormResponses, 
                        utr = utr,
                        amount = seva.amount.toString()
                    )
                    showUtrEntry = false
                    showPostPayment = true
                }
            }
        )
    }
    
    if (showPostPayment) {
        PostPaymentDialog(onDismiss = { showPostPayment = false })
    }
}

@Composable
fun SevaFormDialog(seva: Seva, onDismiss: () -> Unit, onFormSubmitted: (String, Map<String, String>) -> Unit) {
    val scope = rememberCoroutineScope()
    val responses = remember { mutableStateMapOf<String, String>() }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEmailValid = { e: String -> android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() }
    val isPhoneValid = { p: String -> p.length >= 10 && p.all { it.isDigit() } }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Registration: ${seva.title}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                if (seva.description.isNotEmpty()) {
                    Text(
                        text = seva.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (errorMessage != null) {
                    Surface(color = Color.Red.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("WhatsApp/Phone Number *") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723), fontWeight = FontWeight.Medium),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phone.isNotEmpty() && !isPhoneValid(phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF3E2723),
                        unfocusedTextColor = Color(0xFF3E2723)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email Address *") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723), fontWeight = FontWeight.Medium),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = email.isNotEmpty() && !isEmailValid(email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF3E2723),
                        unfocusedTextColor = Color(0xFF3E2723)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                seva.formFields.forEach { field ->
                    Text(text = "${field.label} *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    when (field.type) {
                        "Dropdown" -> {
                            val options = field.options.split(",").map { it.trim() }
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text(responses[field.label] ?: "Select Option", color = Color(0xFF3E2723))
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    options.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option, color = Color(0xFF3E2723)) }, 
                                            onClick = { 
                                                responses[field.label] = option
                                                expanded = false 
                                                errorMessage = null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            OutlinedTextField(
                                value = responses[field.label] ?: "",
                                onValueChange = { responses[field.label] = it; errorMessage = null },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter ${field.label}") },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723), fontWeight = FontWeight.Medium),
                                keyboardOptions = if (field.type == "Number") KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF3E2723),
                                    unfocusedTextColor = Color(0xFF3E2723)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            // Validation
                            if (phone.isBlank() || email.isBlank() || seva.formFields.any { responses[it.label]?.isBlank() ?: true }) {
                                errorMessage = "Please fill in all required fields."
                                return@Button
                            }
                            if (!isPhoneValid(phone)) {
                                errorMessage = "Please enter a valid 10-digit phone number."
                                return@Button
                            }
                            if (!isEmailValid(email)) {
                                errorMessage = "Please enter a valid email address."
                                return@Button
                            }

                            val allResponses = responses.toMutableMap()
                            allResponses["Email"] = email
                            onFormSubmitted(phone, allResponses.toMap())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) {
                        Text("Next: Payment", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentQrDialog(seva: Seva, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Complete Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please scan the QR code to pay ₹${seva.amount}", textAlign = TextAlign.Center, color = Color(0xFF3E2723))
                Spacer(modifier = Modifier.height(16.dp))
                
                if (seva.paymentQrUrl.isNotEmpty()) {
                    ShimmerAsyncImage(
                        model = seva.paymentQrUrl,
                        contentDescription = "Payment QR",
                        modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp)).clickable { /* Enlarging can be added here too if needed */ },
                        contentScale = ContentScale.Fit
                    )
                    if (seva.upiId.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SelectionContainer {
                            Text("UPI ID: ${seva.upiId}", fontWeight = FontWeight.Bold, color = Terracotta, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(150.dp), tint = Color.Gray)
                    Text("QR Code not available. Contact office.", color = Color.Gray, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("After payment, please visit the office for final confirmation.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                    Text("Done", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun UtrEntryDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var utr by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(90) }
    
    LaunchedEffect(Unit) {
        while(timeLeft > 0) {
            kotlinx.coroutines.delay(1000)
            timeLeft--
        }
        onDismiss()
    }

    Dialog(onDismissRequest = {}) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Enter Transaction ID (UTR)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Time Remaining: ${timeLeft}s", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = utr,
                    onValueChange = { if(it.length <= 12) utr = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("12-Digit UTR Number") },
                    placeholder = { Text("Ex: 123456789012") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723))
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if(utr.length >= 10) onSubmit(utr) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = utr.length >= 10,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("Submit Registration", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PostPaymentDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = Color(0xFF1B5E20), shape = CircleShape) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp).size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Registration Successful", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Thank you for your Seva. Please visit the Matha office for final confirmation and collection of prasada.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))) {
                    Text("Understood", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SocialConnectSection(links: List<SocialLink>) {
    val contact by MuttRepository.contactConfig.collectAsState()
    val uriHandler = LocalUriHandler.current
    
    // Combine hardcoded config and database links
    val socialItems = remember(contact, links) {
        val list = mutableListOf<Pair<String, String>>()
        if (contact.facebookUrl.isNotEmpty()) list.add("facebook" to contact.facebookUrl)
        if (contact.instagramUrl.isNotEmpty()) list.add("instagram" to contact.instagramUrl)
        if (contact.youtubeUrl.isNotEmpty()) list.add("youtube" to contact.youtubeUrl)
        
        links.forEach { link ->
            if (link.url.isNotEmpty()) list.add(link.platform.lowercase() to link.url)
        }
        list.distinctBy { it.second }
    }

    if (socialItems.isEmpty()) return

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Connect With Us", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.Center) {
            socialItems.forEach { (platform, url) ->
                Surface(
                    modifier = Modifier.padding(horizontal = 8.dp).size(60.dp).clickable { uriHandler.openUri(url) },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when(platform) {
                                "facebook" -> Icons.Default.Facebook
                                "instagram" -> Icons.Default.CameraAlt
                                "youtube" -> Icons.Default.VideoLibrary
                                "twitter", "x" -> Icons.Default.Share
                                else -> Icons.Default.Language
                            },
                            contentDescription = platform,
                            tint = Terracotta,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppFooter(navController: NavController) {
    val socialLinks by MuttRepository.socialLinks.collectAsState(emptyList())
    val uriHandler = LocalUriHandler.current
    val contact by MuttRepository.contactConfig.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().background(DeepBlack).padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FooterColumn(
                title = "ABOUT", 
                items = listOf(
                    "History & Vision" to { navController.navigate(Screen.About.route) },
                    "Guru Parampara" to { navController.navigate(Screen.GuruParampara.route) },
                    "Branches" to { navController.navigate(Screen.Branches.route) }
                ),
                modifier = Modifier.weight(1f)
            )
            FooterColumn(
                title = "SERVICES", 
                items = listOf(
                    "Sevas & Donations" to { navController.navigate(Screen.Sevas.route) },
                    "Upcoming Events" to { navController.navigate(Screen.Events.route) },
                    "Publications" to { navController.navigate(Screen.Publications.route) }
                ),
                modifier = Modifier.weight(1.2f)
            )
            FooterColumn(
                title = "LEGAL", 
                items = socialLinks.take(2).map { link ->
                    link.platform to { if(link.url.isNotEmpty()) uriHandler.openUri(link.url) }
                } + listOf(
                    "Privacy Policy" to { if(contact.privacyPolicyUrl.isNotEmpty()) uriHandler.openUri(contact.privacyPolicyUrl) }
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SRI KANVA MATHA", color = GoldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, letterSpacing = 1.sp)
        }
    }
}

fun shareApp(context: android.content.Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Experience the spiritual journey with Shri Kanva Mutt App. Download now: https://play.google.com/store/apps/details?id=${context.packageName}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@Composable
fun FooterColumn(title: String, items: List<Pair<String, () -> Unit>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        TranslatedText(title, color = GoldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { (label, onClick) ->
            TranslatedText(
                text = label,
                color = Color.Gray,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clickable { onClick() }
            )
        }
    }
}

@Composable fun AboutScreen() {
    val config by MuttRepository.aboutConfig.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = config.title, style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (config.imageUrl.isNotEmpty()) { ShimmerAsyncImage(model = config.imageUrl, contentDescription = "About Us Image", modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit); Spacer(modifier = Modifier.height(16.dp)) }
        Text(text = config.content, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray, lineHeight = 24.sp)
    }
}

@Composable fun EventsScreen() {
    val events by MuttRepository.events.collectAsState(emptyList())
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A)).padding(16.dp)) {
        TranslatedText("Upcoming Events", style = MaterialTheme.typography.headlineMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxSize()) {
            if (events.isEmpty()) {
                item { 
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        TranslatedText("No upcoming events at the moment.", color = GoldAccent)
                    }
                }
            } else {
                items(events, key = { it.id }) { event ->
                    EventPremiumCard(event)
                }
            }
        }
    }
}

@Composable
fun EventPremiumCard(event: Event) {
    val uriHandler = LocalUriHandler.current
    val sdf = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                if (event.imageUrl.isNotEmpty()) {
                    ShimmerAsyncImage(
                        model = event.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                // Gradient overlay
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF262626)))
                ))
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.title, style = MaterialTheme.typography.headlineSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                EventDetailRow(Icons.Default.CalendarMonth, sdf.format(java.util.Date(event.startDate)))
                EventDetailRow(Icons.Default.Schedule, "Morning - Evening") // Placeholder for time
                if (event.location.isNotEmpty()) {
                    EventDetailRow(Icons.Default.LocationOn, event.location)
                }

                if (event.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
                
                if (event.galleryImageUrls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Event Gallery", style = MaterialTheme.typography.labelLarge, color = GoldAccent, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(event.galleryImageUrls) { imageUrl ->
                            ShimmerAsyncImage(
                                model = imageUrl,
                                contentDescription = "Gallery Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                if (event.registrationUrl.isNotEmpty()) {
                    Button(
                        onClick = { uriHandler.openUri(event.registrationUrl) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Terracotta, RichBrown)), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Register Now", color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable fun SevasScreen() {
    val sevas by MuttRepository.sevas.collectAsState(emptyList())
    val registrations by MuttRepository.userRegistrations.collectAsState(emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TranslatedText("Sevas & Donations", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold) }
        items(sevas, key = { it.id }) { seva ->
            val regStatus = registrations.find { it.itemId == seva.id }?.status ?: "Open"
            SevaRegCard(seva, regStatus)
        }
    }
}

@Composable fun PanchangaScreen() {
    val items by MuttRepository.panchanga.collectAsState(emptyList())
    val sortedItems = remember(items) { items.filter { it.date > 0 }.sortedByDescending { it.date } }
    
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment)))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            TranslatedText("Daily Panchanga", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.ExtraBold)
            TranslatedText("Vedic timings calculated with high precision", style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(20.dp))
            if (sortedItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No Panchanga data available.", color = Color.Gray) 
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
            items(sortedItems, key = { it.id }) { item -> PanchangCard(item) }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun PanchangCard(item: Panchang) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val homeConfig by MuttRepository.homeConfig.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isSpecial) Color(0xFFFFF9C4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if(item.isSpecial) Terracotta.copy(alpha = 0.5f) else CardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val dateText = try { java.text.SimpleDateFormat("dd MMMM yyyy, EEEE", java.util.Locale.getDefault()).format(java.util.Date(item.date)) } catch (e: Exception) { "Invalid Date" }
                Column(modifier = Modifier.weight(1f)) {
                    Text(dateText, fontWeight = FontWeight.ExtraBold, color = Terracotta, style = MaterialTheme.typography.titleMedium)
                    if (item.samvatsara.isNotEmpty()) {
                        Text("Shri ${item.samvatsara} Samvatsara", style = MaterialTheme.typography.labelMedium, color = RichBrown.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                    }
                }
                
                // WhatsApp Share Button
                val scope = rememberCoroutineScope()
                IconButton(onClick = {
                    scope.launch {
                        val isKan = MuttRepository.isLanguageKannada.value
                        val prefix = if (isKan) TranslationManager.translate(homeConfig.whatsappSharePrefix.replace("\\n", "\n")) else homeConfig.whatsappSharePrefix.replace("\\n", "\n")
                        
                        val message = buildString {
                            append(prefix)
                            append("\n📅 *")
                            append(if (isKan) "ದಿನಾಂಕ:" else "Date:")
                            append("* $dateText\n")
                            
                            if (item.samvatsara.isNotEmpty()) {
                                append("✨ *")
                                append(if (isKan) "ಸಂವತ್ಸರ:" else "Samvatsara:")
                                append("* ${item.samvatsara}\n")
                            }
                            
                            if (item.masa.isNotEmpty()) {
                                append("🌙 *")
                                append(if (isKan) "ಮಾಸ:" else "Masa:")
                                append("* ${item.masa} (${item.paksha})\n")
                            }
                            
                            append("🔱 *")
                            append(if (isKan) "ತಿಥಿ:" else "Tithi:")
                            append("* ${item.tithi}\n")
                            
                            append("⭐ *")
                            append(if (isKan) "ನಕ್ಷತ್ರ:" else "Nakshatra:")
                            append("* ${item.nakshatra}\n")
                            
                            append("☀️ *")
                            append(if (isKan) "ಸೌರ ಮಾಸ:" else "Soura Masa:")
                            append("* ${item.souraMasa}\n")
                            
                            append("🌅 *")
                            append(if (isKan) "ಸೂರ್ಯೋದಯ:" else "Sunrise:")
                            append("* ${item.suryodaya} | *")
                            append(if (isKan) "ಸೂರ್ಯಾಸ್ತ:" else "Sunset:")
                            append("* ${item.suryasta}\n")
                            
                            if (item.specialNote.isNotEmpty()) {
                                append("\n📝 *")
                                append(if (isKan) "ಟಿಪ್ಪಣಿ:" else "Note:")
                                append("* ${item.specialNote}\n")
                            }
                            
                            append("\n")
                            append(if (isKan) "ಹೆಚ್ಚಿನ ಮಾಹಿತಿಗಾಗಿ:" else "Detailed Panchanga:")
                            append(" https://kanvamath.org")
                        }
                        
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, message)
                            setPackage("com.whatsapp")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val genericIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, message)
                            }
                            context.startActivity(android.content.Intent.createChooser(genericIntent, "Share Panchanga"))
                        }
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Terracotta)
                }

                if (item.isSpecial) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Terracotta, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Special", style = MaterialTheme.typography.labelSmall, color = Terracotta, fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Terracotta.copy(alpha = 0.3f))
            
            if (item.specialNote.isNotEmpty()) { 
                Text(item.specialNote, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = RichBrown, modifier = Modifier.padding(bottom = 8.dp))
            }

            // High Precision Sections - Vertical list for better space management
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(RichBrown.copy(alpha = 0.05f)).padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        PanchangRow("Samvatsara", item.samvatsara)
                        PanchangRow("Ayana", item.ayana)
                        PanchangRow("Ritu", item.ritu)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        PanchangRow("Masa", item.masa)
                        PanchangRow("Paksha", item.paksha)
                        PanchangRow("Soura Masa", item.souraMasa)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.2.dp, color = Color.Gray.copy(alpha = 0.3f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) { PanchangRow("Sunrise", item.suryodaya) }
                    Column(modifier = Modifier.weight(1f)) { PanchangRow("Sunset", item.suryasta) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            PanchangDetailRow(Icons.Default.Brightness3, "Tithi", if (item.tithiEnd.isNotEmpty()) "${item.tithi} (ends ${item.tithiEnd})" else item.tithi)
            
            if (item.nextTithi.isNotEmpty() && item.tithiEnd.isNotEmpty()) {
                PanchangDetailRow(Icons.Default.ArrowForward, "Next Tithi", item.nextTithi)
            }
            
            PanchangDetailRow(Icons.Default.Star, "Nakshatra", if (item.nakshatraEnd.isNotEmpty()) "${item.nakshatra} (ends ${item.nakshatraEnd})" else item.nakshatra)
            
            if (item.nextNakshatra.isNotEmpty() && item.nakshatraEnd.isNotEmpty()) {
                PanchangDetailRow(Icons.Default.ArrowForward, "Next Nakshatra", item.nextNakshatra)
            }
            
            PanchangDetailRow(Icons.Default.Favorite, "Yoga", item.yoga)
            PanchangDetailRow(Icons.Default.Security, "Karana", item.karana)

            if (item.rahuKala.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Terracotta.copy(alpha = 0.08f)).padding(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Rahu Kala", fontSize = 11.sp, color = Terracotta, fontWeight = FontWeight.ExtraBold)
                        Text(item.rahuKala, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3E2723))
                    }
                    Box(modifier = Modifier.height(30.dp).width(1.dp).background(Terracotta.copy(alpha = 0.2f)))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Yamaganda", fontSize = 11.sp, color = Terracotta, fontWeight = FontWeight.ExtraBold)
                        Text(item.yamagandaKala, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3E2723))
                    }
                }
            }
        }
    }
}

@Composable fun PanchangRow(label: String, value: String) {
    if (value.isEmpty() || value.length > 100) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
        TranslatedText(label, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            value, 
            fontWeight = FontWeight.Bold, 
            fontSize = 10.sp, 
            color = RichBrown, 
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2.5f) 
        )
    }
}

@Composable fun PanchangDetailRow(icon: ImageVector, label: String, value: String) {
    if (value.isEmpty()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Terracotta.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        TranslatedText(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1.2f))
        Text(
            value, 
            fontWeight = FontWeight.Bold, 
            fontSize = 13.sp, 
            color = RichBrown, 
            textAlign = TextAlign.End,
            modifier = Modifier.weight(3.5f) 
        )
    }
}

@Composable fun BranchesScreen() {
    val branches by MuttRepository.branches.collectAsState(emptyList())
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        TranslatedText("Our Branches", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(branches, key = { it.name }) { branch ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        TranslatedText(branch.name, fontWeight = FontWeight.Bold, color = Terracotta)
                        TranslatedText(branch.address, fontSize = 10.sp, color = Color.Gray, maxLines = 2)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = Terracotta); Spacer(modifier = Modifier.width(4.dp)); Text(branch.contact, fontSize = 10.sp, color = Color(0xFF3E2723)) }
                    }
                }
            }
        }
    }
}

@Composable fun ContactScreen() {
    val contact by MuttRepository.contactConfig.collectAsState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Terracotta)) { TranslatedText("Get in Touch", modifier = Modifier.align(Alignment.Center), color = Color.White, style = MaterialTheme.typography.headlineSmall) }
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ContactInfoItem(Icons.Default.Email, "Email", contact.email) { uriHandler.openUri("mailto:${contact.email}") }
            ContactInfoItem(Icons.Default.Phone, "Phone", contact.phone) { uriHandler.openUri("tel:${contact.phone}") }
            ContactInfoItem(Icons.Default.LocationOn, "Address", contact.address) { if(contact.mapUrl.isNotEmpty()) uriHandler.openUri(contact.mapUrl) }
            
            Button(
                onClick = { shareApp(context) }, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(8.dp)
            ) { 
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                TranslatedText("Share this App", color = Color.White) 
            }

            if (contact.mapUrl.isNotEmpty()) {
                Button(onClick = { uriHandler.openUri(contact.mapUrl) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Terracotta), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.Map, contentDescription = null, tint = Color.White); Spacer(modifier = Modifier.width(8.dp)); TranslatedText("Open in Google Maps", color = Color.White) }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            TranslatedText("Send us a Message", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RichBrown)
            OutlinedTextField(value = "", onValueChange = {}, label = { TranslatedText("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { TranslatedText("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { TranslatedText("Send Message", color = Color.White) }
        }
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Terracotta)
            Spacer(modifier = Modifier.width(16.dp))
            Column { Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray); Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) }
        }
    }
}

@Composable fun PublicationsScreen() {
    val items by MuttRepository.publications.collectAsState(emptyList())
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        TranslatedText("Our Publications", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { TranslatedText("Coming Soon...", color = Color.Gray) } }
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(items, key = { it.id }) { pub ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.width(100.dp).fillMaxHeight().background(Color(0xFFF5F5F5))) { if (pub.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = pub.imageUrl, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize()) }
                            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                                Surface(color = Terracotta, shape = RoundedCornerShape(4.dp)) { TranslatedText(pub.type, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White) }
                                Spacer(modifier = Modifier.height(8.dp))
                                TranslatedText(pub.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RichBrown)
                                TranslatedText(pub.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = Color.DarkGray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Cost: ₹${pub.cost}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Terracotta)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable fun GuruParamparaScreen() {
    val gurus by MuttRepository.gurus.collectAsState(emptyList())
    val peethadhipati by MuttRepository.peethadhipatiConfig.collectAsState()
    var selectedGuru by remember { mutableStateOf<Guru?>(null) }
    val sortedGurus = gurus.sortedBy { it.order }
    LazyColumn(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item { TranslatedText("Present Peethadhipati", style = MaterialTheme.typography.titleLarge, color = Terracotta, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp)); PresentPeethadhipatiCard(peethadhipati) }
        item { TranslatedText("Guru Parampara (Lineage)", style = MaterialTheme.typography.titleLarge, color = Terracotta, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp)); LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(vertical = 8.dp)) { items(sortedGurus, key = { it.order }) { guru -> GuruCircularItem(guru) { selectedGuru = guru } } } }
        item { TranslatedText("Detailed Lineage", style = MaterialTheme.typography.titleMedium, color = Terracotta, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) }
        items(sortedGurus, key = { it.order }) { guru -> GuruListCard(guru) { selectedGuru = guru } }
    }
    if (selectedGuru != null) GuruDetailDialog(guru = selectedGuru!!, onDismiss = { selectedGuru = null })
}

@Composable
fun PresentPeethadhipatiCard(config: PeethadhipatiConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(28.dp), 
        colors = CardDefaults.cardColors(containerColor = Terracotta),
        border = BorderStroke(1.5.dp, GoldAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.background(Brush.verticalGradient(listOf(Terracotta, RichBrown))).padding(28.dp), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(160.dp), shape = CircleShape, border = BorderStroke(4.dp, GoldAccent), color = Color.White) { 
                if (config.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = config.imageUrl, contentDescription = config.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit) 
                else Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(32.dp), tint = Color.Gray) 
            }
            Spacer(modifier = Modifier.height(20.dp))
            TranslatedText(config.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            TranslatedText(config.title, style = MaterialTheme.typography.titleMedium, color = GoldAccent, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(20.dp))
            TranslatedText(config.vision, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, textAlign = TextAlign.Center, lineHeight = 26.sp, fontWeight = FontWeight.Medium)
            if (config.videoUrl.isNotEmpty()) { 
                val uriHandler = LocalUriHandler.current
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { uriHandler.openUri(config.videoUrl) }, 
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Terracotta),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) { 
                    Icon(Icons.Default.PlayCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    TranslatedText("Watch Video", fontWeight = FontWeight.Bold) 
                } 
            }
        }
    }
}

@Composable
fun GuruCircularItem(guru: Guru, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp).clickable { onClick() }) {
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, border = BorderStroke(2.dp, Terracotta), color = Color.White) { if (guru.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = guru.imageUrl, contentDescription = guru.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit) else Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(20.dp), tint = Color.Gray) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(guru.name, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
    }
}

@Composable
fun GuruListCard(guru: Guru, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = Color(0xFFFFE0B2)) { Box(contentAlignment = Alignment.Center) { Text(guru.order.toString(), style = MaterialTheme.typography.titleLarge, color = Terracotta, fontWeight = FontWeight.Bold) } }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text(guru.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Terracotta); Text(guru.lifespan, style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun GuruDetailDialog(guru: Guru, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) { if (guru.imageUrl.isNotEmpty()) ShimmerAsyncImage(model = guru.imageUrl, contentDescription = guru.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit) else Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)); IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) } }
                Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                    TranslatedText(guru.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Terracotta); TranslatedText(guru.title, style = MaterialTheme.typography.titleMedium, color = Color.Gray); Text(guru.lifespan, style = MaterialTheme.typography.labelLarge, color = Terracotta, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(16.dp)); TranslatedText("Biography", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RichBrown); TranslatedText(guru.biography, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp, color = Color.DarkGray); if (guru.timeline.isNotEmpty()) { Spacer(modifier = Modifier.height(16.dp)); TranslatedText("Timeline & Aradhana", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RichBrown); TranslatedText(guru.timeline, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray) }
                    if (guru.videoUrl.isNotEmpty() || guru.pdfUrl.isNotEmpty()) { Spacer(modifier = Modifier.height(24.dp)); TranslatedText("Additional Resources", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RichBrown); Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { val uriHandler = LocalUriHandler.current; if (guru.videoUrl.isNotEmpty()) FilterChip(selected = false, onClick = { uriHandler.openUri(guru.videoUrl) }, label = { TranslatedText("Watch Video") }, leadingIcon = { Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp)) }); if (guru.pdfUrl.isNotEmpty()) FilterChip(selected = false, onClick = { uriHandler.openUri(guru.pdfUrl) }, label = { TranslatedText("View PDF") }, leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp)) }) } }
                }
            }
        }
    }
}

@Composable
fun FeaturedShlokasSection(navController: NavController) {
    val items by MuttRepository.shlokas.collectAsState(emptyList())
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        items.take(3).forEach { shloka ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { navController.navigate(Screen.Shlokas.route) }, colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(shloka.title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(shloka.content, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        TextButton(onClick = { navController.navigate(Screen.Shlokas.route) }) {
            Text("View All Shlokas", color = Terracotta)
        }
    }
}

@Composable fun PhotoGalleryScreen(navController: NavController) {
    val albums by MuttRepository.photoAlbums.collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        TranslatedText("Photo Gallery", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(albums, key = { it.id }) { album ->
                Card(modifier = Modifier.clickable { navController.navigate(Screen.AlbumDetail.route.replace("{albumId}", album.id)) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFF5F5F5))) { if (album.imageUrls.isNotEmpty()) ShimmerAsyncImage(model = album.imageUrls.first(), contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize(), contentDescription = null) }
                        Column(modifier = Modifier.padding(8.dp)) { TranslatedText(album.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1, color = RichBrown); TranslatedText(album.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                    }
                }
            }
        }
    }
}

@Composable fun AlbumDetailScreen(navController: NavController, albumId: String) {
    val albums by MuttRepository.photoAlbums.collectAsState(emptyList())
    val album = albums.find { it.id == albumId }
    var zoomImageUrl by remember { mutableStateOf<String?>(null) }

    // Handle system back button to close zoom dialog first
    BackHandler(enabled = zoomImageUrl != null) {
        zoomImageUrl = null
    }

    if (album == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
            CircularProgressIndicator(color = Terracotta) 
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment)))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RichBrown) }
            TranslatedText(album.title, style = MaterialTheme.typography.titleLarge, color = RichBrown, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), 
            horizontalArrangement = Arrangement.spacedBy(8.dp), 
            verticalArrangement = Arrangement.spacedBy(8.dp), 
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(album.imageUrls) { url ->
                ShimmerAsyncImage(
                    model = url, 
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).clickable { zoomImageUrl = url }, 
                    contentDescription = null
                )
            }
        }
    }

    if (zoomImageUrl != null) {
        val initialPage = album.imageUrls.indexOf(zoomImageUrl)
        Dialog(onDismissRequest = { zoomImageUrl = null }) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Background overlay
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)).clickable { zoomImageUrl = null })
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { album.imageUrls.size }, initialPage = initialPage)
                    
                    androidx.compose.foundation.pager.HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ShimmerAsyncImage(
                                model = album.imageUrls[page],
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center) {
                        repeat(album.imageUrls.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray
                            Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
                        }
                    }
                }

                IconButton(onClick = { zoomImageUrl = null }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(0.5f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}


@Composable fun VideoGalleryScreen() {
    val videos by MuttRepository.videos.collectAsState(emptyList())
    val uriHandler = LocalUriHandler.current
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        TranslatedText("Video Gallery", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(videos, key = { it.id }) { video ->
                Card(modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri(video.url) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color.Red.copy(alpha = 0.1f)) { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Red, modifier = Modifier.padding(12.dp)) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column { TranslatedText(video.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RichBrown); TranslatedText(video.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                    }
                }
            }
        }
    }
}

@Composable
fun MembershipScreen() {
    val config by MuttRepository.membershipConfig.collectAsState()
    ProgramDetails(config = config)
}

@Composable
fun VolunteerScreen() {
    val programs by MuttRepository.volunteerPrograms.collectAsState(emptyList())
    if (programs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))), contentAlignment = Alignment.Center) {
            Text("No volunteer programs currently active.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(programs) { program ->
                ProgramDetails(config = program)
            }
        }
    }
}

@Composable
fun ProgramDetails(config: ProgramConfig) {
    var showForm by remember { mutableStateOf(false) }
    var showPostSubmission by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column {
            if (config.imageUrl.isNotEmpty()) {
                ShimmerAsyncImage(model = config.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Fit)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(config.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(8.dp))
                Text(config.description, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        if (config.mode == "External" && config.externalUrl.isNotEmpty()) {
                            uriHandler.openUri(config.externalUrl)
                        } else {
                            showForm = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text(if (config.mode == "External") "Join Externally" else "Register Now", color = Color.White)
                }
            }
        }
    }

    if (showForm) {
        val scope = rememberCoroutineScope()
        ProgramFormDialog(
            program = config,
            onDismiss = { showForm = false },
            onFormSubmitted = { phone, responses ->
                scope.launch {
                    MuttRepository.registerWithForm(
                        itemId = config.id,
                        itemTitle = config.title,
                        phone = phone,
                        responses = responses,
                        amount = "0"
                    )
                    showForm = false
                    showPostSubmission = true
                }
            }
        )
    }

    if (showPostSubmission) {
        PostPaymentDialog(onDismiss = { showPostSubmission = false })
    }
}

@Composable
fun NotificationsScreen() {
    val items by MuttRepository.appNotifications.collectAsState(emptyList())
    
    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WarmWhite, Parchment))).padding(16.dp)) {
        TranslatedText("Notifications", style = MaterialTheme.typography.headlineMedium, color = Terracotta, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TranslatedText("No notifications yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(0.5.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when(item.type) {
                                        "Panchanga" -> Icons.Default.CalendarMonth
                                        "Event" -> Icons.Default.Event
                                        else -> Icons.Default.Campaign
                                    },
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                TranslatedText(item.title, fontWeight = FontWeight.Bold, color = RichBrown, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(item.message, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(java.util.Date(item.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgramFormDialog(program: ProgramConfig, onDismiss: () -> Unit, onFormSubmitted: (String, Map<String, String>) -> Unit) {
    val responses = remember { mutableStateMapOf<String, String>() }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEmailValid = { e: String -> android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() }
    val isPhoneValid = { p: String -> p.length >= 10 && p.all { it.isDigit() } }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Join: ${program.title}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                }

                OutlinedTextField(value = phone, onValueChange = { phone = it; errorMessage = null }, modifier = Modifier.fillMaxWidth(), label = { Text("Phone Number *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it; errorMessage = null }, modifier = Modifier.fillMaxWidth(), label = { Text("Email Address *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                Spacer(modifier = Modifier.height(16.dp))

                program.formFields.forEach { field ->
                    Text(text = "${field.label} *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    when (field.type) {
                        "Dropdown" -> {
                            val options = field.options.split(",").map { it.trim() }
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text(responses[field.label] ?: "Select Option", color = Color(0xFF3E2723))
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    options.forEach { option ->
                                        DropdownMenuItem(text = { Text(option) }, onClick = { responses[field.label] = option; expanded = false })
                                    }
                                }
                            }
                        }
                        else -> {
                            OutlinedTextField(
                                value = responses[field.label] ?: "",
                                onValueChange = { responses[field.label] = it; errorMessage = null },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter ${field.label}") },
                                keyboardOptions = if (field.type == "Number") KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (phone.isBlank() || email.isBlank() || program.formFields.any { responses[it.label]?.isBlank() ?: true }) {
                                errorMessage = "Please fill all required fields."
                                return@Button
                            }
                            if (!isPhoneValid(phone)) { errorMessage = "Invalid phone number."; return@Button }
                            if (!isEmailValid(email)) { errorMessage = "Invalid email address."; return@Button }

                            val allResponses = responses.toMutableMap()
                            allResponses["Email"] = email
                            onFormSubmitted(phone, allResponses.toMap())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) {
                        Text("Submit", color = Color.White)
                    }
                }
            }
        }
    }
}
