package com.satwik.example.mutt_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satwik.example.mutt_app.data.*
import com.satwik.example.mutt_app.ui.theme.*

@Composable
fun HomeScreen() {
    val theme = LocalMuttTheme.current
    val config by MuttRepository.homeConfig.collectAsState()
    val announcements by MuttRepository.announcements.collectAsState()
    val shlokas by MuttRepository.shlokas.collectAsState()
    val socialLinks by MuttRepository.socialLinks.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Hero Banner (Opening UI)
        item { HeroBanner(config) }
        
        // 2. Shloka Board
        if (shlokas.isNotEmpty()) {
            item { ShlokaSection(shlokas) }
        }

        // 3. Latest Happenings (Announcements/Events)
        if (announcements.isNotEmpty()) {
            item { LatestHappeningsSection(announcements) }
        }
        
        // 4. Sevas/Registration Cards
        item { FeaturedSevasSection() }

        // 5. Social Connect
        if (socialLinks.isNotEmpty()) {
            item { SocialConnectSection(socialLinks) }
        }

        // 6. Footer
        item { AppFooter() }
    }
}

@Composable
fun HeroBanner(config: HomeConfig) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        // Placeholder for dynamic banner image
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Terracotta, Color(0xFFE65100)))))
        
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                config.bannerTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                config.bannerSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Terracotta),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Explore More", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShlokaSection(shlokas: List<Shloka>) {
    Column(modifier = Modifier.padding(16.dp)) {
        shlokas.forEach { shloka ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                        Icon(Icons.Default.Spa, contentDescription = null, tint = GoldAccent, modifier = Modifier.align(Alignment.Center).size(48.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        shloka.content,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LatestHappeningsSection(items: List<Announcement>) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "Stay connected with the latest happenings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                AnnouncementCard(item)
            }
        }
    }
}

@Composable
fun AnnouncementCard(item: Announcement) {
    ElevatedCard(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Color.LightGray)) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(item.timestamp)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE65100)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.content, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 2)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                ) {
                    Text("View Details", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun FeaturedSevasSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        SevaRegCard("Krishna Mantra Lekhana Yajna", "SANKALPA", "Write \"Shri Krishnaya Namaha\" and align your sankalpa...")
        Spacer(modifier = Modifier.height(16.dp))
        SevaRegCard("Veda Parayana Seva", "VEDIC PRAYER", "Support the sacred recitation of the Vedas at our Matha...")
    }
}

@Composable
fun SevaRegCard(title: String, tag: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.weight(1f).padding(20.dp)) {
                Surface(color = Color(0xFFFFF9C4), shape = RoundedCornerShape(4.dp)) {
                    Text(tag, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Terracotta), shape = RoundedCornerShape(12.dp)) {
                    Text("Register Now")
                }
            }
            Box(modifier = Modifier.width(140.dp).fillMaxHeight().background(Color.LightGray))
        }
    }
}

@Composable
fun SocialConnectSection(links: List<SocialLink>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Join our growing spiritual community on social media for daily updates, live sessions, and sacred teachings.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            links.take(4).forEach { link ->
                SocialCard(link, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SocialCard(link: SocialLink, modifier: Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF5F5F5))) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.Blue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(link.platform, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(link.handle, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(link.audience, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("AUDIENCE", fontSize = 8.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AppFooter() {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FooterColumn("ABOUT", listOf("About Us", "Mutt History"))
            FooterColumn("SERVICES", listOf("Sevas & Donations", "Events & Celebrations"))
            FooterColumn("POLICIES", listOf("Terms and Conditions", "Privacy Policy"))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("CONNECT WITH US", color = GoldAccent, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) { Icon(Icons.Default.Circle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp)) }
        }
    }
}

@Composable
fun FooterColumn(title: String, items: List<String>) {
    Column {
        Text(title, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { Text(it, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp)) }
    }
}

@Composable
fun BranchesScreen() {
    val branches by MuttRepository.branches.collectAsState()
    val theme = LocalMuttTheme.current
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp)) {
        Text("Our Locations", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(branches) { branch ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(branch.name, fontWeight = FontWeight.Bold, color = Terracotta)
                        Text(branch.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Terracotta)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(branch.contact, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Terracotta)) {
            Text("Get in Touch", modifier = Modifier.align(Alignment.Center), color = Color.White, style = MaterialTheme.typography.headlineSmall)
        }
        
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Form on the right, Info on left
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text("Our Locations", fontWeight = FontWeight.Bold)
                // Location items...
                ContactInfoItem(Icons.Default.Email, "support@mutt.org")
                ContactInfoItem(Icons.Default.Schedule, "Morning: 6 AM - 12 PM\nEvening: 4 PM - 8 PM")
            }
            
            Card(modifier = Modifier.weight(1.2f), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Send us a Message", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                        Text("Send Message")
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Terracotta, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 12.sp)
    }
}

@Composable fun AboutScreen() {}
@Composable fun EventsScreen() {
    // Implementing Event Screen with Notifications-like popups or status indicators
}
@Composable fun SevasScreen() {}
@Composable fun PanchangaScreen() {}
@Composable fun GuruParamparaScreen() {}
