package com.satwik.example.mutt_app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.satwik.example.mutt_app.data.*
import com.satwik.example.mutt_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen() {
    var currentView by remember { mutableStateOf("Dashboard") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Console") },
                navigationIcon = {
                    if (currentView != "Dashboard") {
                        IconButton(onClick = { currentView = "Dashboard" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentView) {
                "Dashboard" -> AdminDashboard(onNavigate = { currentView = it })
                "Announcements" -> AnnouncementManager(onFinished = { currentView = "Dashboard" })
                "Events" -> EventManager(onFinished = { currentView = "Dashboard" })
                "Shlokas" -> ShlokaManager(onFinished = { currentView = "Dashboard" })
                "Social Links" -> SocialLinkManager(onFinished = { currentView = "Dashboard" })
                "Home Screen" -> HomeScreenEditor(onFinished = { currentView = "Dashboard" })
                "Branches" -> BranchManager(onFinished = { currentView = "Dashboard" })
                "Sevas" -> SevaManager(onFinished = { currentView = "Dashboard" })
                "Theme" -> ThemeEditor(onFinished = { currentView = "Dashboard" })
            }
        }
    }
}

@Composable
fun AdminDashboard(onNavigate: (String) -> Unit) {
    val menuItems = listOf(
        "Home Screen" to Icons.Default.Home,
        "Announcements" to Icons.Default.Announcement,
        "Events" to Icons.Default.Event,
        "Shlokas" to Icons.Default.MenuBook,
        "Social Links" to Icons.Default.Share,
        "Branches" to Icons.Default.AccountTree,
        "Sevas" to Icons.Default.VolunteerActivism,
        "Theme" to Icons.Default.Palette
    )
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Management Dashboard", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(menuItems) { (title, icon) ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigate(title) }
                ) {
                    ListItem(
                        headlineContent = { Text(title) },
                        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementManager(onFinished: () -> Unit) {
    val items by MuttRepository.announcements.collectAsState()
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUrl = it.toString() }
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Announcements", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL/Path") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.PhotoAlbum, contentDescription = null)
                Text("Pick")
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (content.isNotBlank()) {
                    MuttRepository.saveAnnouncement(Announcement(editingId ?: 0, content, imageUrl = imageUrl))
                    content = ""; imageUrl = ""; editingId = null
                }
            }) {
                Text(if (editingId == null) "Add" else "Update")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.content) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { content = item.content; imageUrl = item.imageUrl; editingId = item.id }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { MuttRepository.deleteAnnouncement(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ShlokaManager(onFinished: () -> Unit) {
    val items by MuttRepository.shlokas.collectAsState()
    var content by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Shlokas", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Shloka Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (content.isNotBlank()) {
                    MuttRepository.saveShloka(Shloka(editingId ?: 0, content))
                    content = ""; editingId = null
                }
            }) { Text("Save") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.content, maxLines = 1) },
                trailingContent = {
                    IconButton(onClick = { MuttRepository.deleteShloka(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    }
}

@Composable
fun SocialLinkManager(onFinished: () -> Unit) {
    val items by MuttRepository.socialLinks.collectAsState()
    var platform by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Social Links", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = platform, onValueChange = { platform = it }, label = { Text("Platform (e.g. Instagram)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text("Handle (e.g. @mutt_official)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = audience, onValueChange = { audience = it }, label = { Text("Audience Count (e.g. 29K+)") }, modifier = Modifier.fillMaxWidth())
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (platform.isNotBlank()) {
                    MuttRepository.saveSocialLink(SocialLink(0, platform, handle, audience, ""))
                    platform = ""; handle = ""; audience = ""
                }
            }) { Text("Add") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text("${item.platform}: ${item.audience}") },
                trailingContent = {
                    IconButton(onClick = { MuttRepository.deleteSocialLink(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    }
}

@Composable
fun EventManager(onFinished: () -> Unit) {
    val items by MuttRepository.events.collectAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUrl = it.toString() }
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Events", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL/Path") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.PhotoAlbum, contentDescription = null)
                Text("Pick")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (title.isNotBlank()) {
                    val expiry = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)
                    MuttRepository.saveEvent(Event(editingId ?: 0, title, desc, imageUrl, System.currentTimeMillis(), expiry))
                    title = ""; desc = ""; imageUrl = ""; editingId = null
                }
            }) { Text("Save") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                trailingContent = {
                    IconButton(onClick = { MuttRepository.deleteEvent(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreenEditor(onFinished: () -> Unit) {
    val currentConfig = MuttRepository.homeConfig.collectAsState().value
    var title by remember { mutableStateOf(currentConfig.bannerTitle) }
    var subtitle by remember { mutableStateOf(currentConfig.bannerSubtitle) }
    var imageUrl by remember { mutableStateOf(currentConfig.bannerImageUrl) }
    var wisdom by remember { mutableStateOf(currentConfig.dailyWisdom) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUrl = it.toString() }
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Edit Home Screen", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Banner Subtitle") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Banner Image URL") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.PhotoAlbum, contentDescription = null)
                Text("Pick")
            }
        }

        OutlinedTextField(value = wisdom, onValueChange = { wisdom = it }, label = { Text("Daily Wisdom/Quote") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Cancel") }
            Button(onClick = {
                MuttRepository.updateHomeConfig(HomeConfig(title, subtitle, imageUrl, wisdom))
                onFinished()
            }) { Text("Save Changes") }
        }
    }
}

@Composable
fun BranchManager(onFinished: () -> Unit) {
    val items by MuttRepository.branches.collectAsState()
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Branches", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Branch Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Number") }, modifier = Modifier.fillMaxWidth())
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (name.isNotBlank()) {
                    MuttRepository.saveBranch(Branch(editingId ?: 0, name, address, contact))
                    name = ""; address = ""; contact = ""; editingId = null
                }
            }) { Text("Save") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.name) },
                trailingContent = {
                    IconButton(onClick = { MuttRepository.deleteBranch(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    }
}

@Composable
fun SevaManager(onFinished: () -> Unit) {
    val items by MuttRepository.sevas.collectAsState()
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Manage Sevas", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Seva Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount (₹)") }, modifier = Modifier.fillMaxWidth())
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                if (name.isNotBlank()) {
                    MuttRepository.saveSeva(Seva(editingId ?: 0, name, "", amount.toIntOrNull() ?: 0))
                    name = ""; amount = ""; editingId = null
                }
            }) { Text("Save") }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.name) },
                trailingContent = {
                    IconButton(onClick = { MuttRepository.deleteSeva(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    }
}

@Composable
fun ThemeEditor(onFinished: () -> Unit) {
    val currentConfig = ThemeConfig.current.value
    var primaryColor by remember { mutableStateOf(currentConfig.primaryColor) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Theme Customization", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Primary Color")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Terracotta, DarkOrange, Color.Red, Color.Blue).forEach { color ->
                Surface(
                    modifier = Modifier.size(40.dp).clickable { primaryColor = color },
                    color = color,
                    shape = CircleShape,
                    border = if (primaryColor == color) BorderStroke(2.dp, Color.Black) else null
                ) {}
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onFinished) { Text("Back") }
            Button(onClick = {
                ThemeConfig.update(currentConfig.copy(primaryColor = primaryColor))
                onFinished()
            }) { Text("Apply Theme") }
        }
    }
}
