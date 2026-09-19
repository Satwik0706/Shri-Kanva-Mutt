package com.satwik.example.mutt_app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.util.*
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.satwik.example.mutt_app.data.*
import com.satwik.example.mutt_app.ui.theme.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    val user by MuttRepository.currentUser.collectAsState()
    var currentView by remember { mutableStateOf("Dashboard") }

    BackHandler(enabled = currentView != "Dashboard") {
        currentView = "Dashboard"
    }

    if (user == null) {
        AdminLoginScreen()
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mutt Administration", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (currentView != "Dashboard") {
                            IconButton(onClick = { currentView = "Dashboard" }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                        } else {
                            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.Close, contentDescription = "Exit Admin") }
                        }
                    },
                    actions = {
                        IconButton(onClick = { MuttRepository.signOut() }) { Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red) }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentView) {
                    "Dashboard" -> AdminDashboard(onNavigate = { currentView = it })
                    "Announcements" -> AnnouncementManager(onFinished = { currentView = "Dashboard" })
                    "Events" -> EventManager(onFinished = { currentView = "Dashboard" })
                    "Sevas" -> SevaManager(onFinished = { currentView = "Dashboard" })
                    "Shlokas" -> ShlokaManager(onFinished = { currentView = "Dashboard" })
                    "Social Links" -> SocialLinkManager(onFinished = { currentView = "Dashboard" })
                    "Home Screen" -> HomeScreenEditor(onFinished = { currentView = "Dashboard" })
                    "About Us" -> AboutUsEditor(onFinished = { currentView = "Dashboard" })
                    "Branches" -> BranchManager(onFinished = { currentView = "Dashboard" })
                    "Registrations" -> RegistrationManager(onFinished = { currentView = "Dashboard" })
                    "Panchanga" -> PanchangManager(onFinished = { currentView = "Dashboard" })
                    "Volunteer Program" -> VolunteerProgramManager(onFinished = { currentView = "Dashboard" })
                    "Membership Program" -> ProgramEditor(title = "Membership Management", configState = MuttRepository.membershipConfig, onSave = { MuttRepository.updateMembershipConfig(it) }, onFinished = { currentView = "Dashboard" })
                    "Publications" -> PublicationManager(onFinished = { currentView = "Dashboard" })
                    "Verified Seva Records" -> SevaRecordManager(title = "Verified Seva Logs", type = "Seva", onFinished = { currentView = "Dashboard" })
                    "Verified Members" -> SevaRecordManager(title = "Verified Member Logs", type = "Member", onFinished = { currentView = "Dashboard" })
                    "Verified Volunteers" -> SevaRecordManager(title = "Verified Volunteer Logs", type = "Volunteer", onFinished = { currentView = "Dashboard" })
                    "Contact Settings" -> ContactEditor(onFinished = { currentView = "Dashboard" })
                    "Theme" -> ThemeEditor(onFinished = { currentView = "Dashboard" })
                    "Photo Albums" -> PhotoManager(onFinished = { currentView = "Dashboard" })
                    "Videos" -> VideoManager(onFinished = { currentView = "Dashboard" })
                    "Guru Parampara" -> GuruManager(onFinished = { currentView = "Dashboard" })
                    "Peethadhipati" -> PeethadhipatiEditor(onFinished = { currentView = "Dashboard" })
                    "News" -> NewsManager(onFinished = { currentView = "Dashboard" })
                    "Festivals" -> FestivalManager(onFinished = { currentView = "Dashboard" })
                    "Special Events" -> SpecialEventManager(onFinished = { currentView = "Dashboard" })
                    "App Popups" -> NotificationManager(onFinished = { currentView = "Dashboard" })
                }
            }
        }
    }
}

@Composable
fun AdminLoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Admin Login", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Terracotta)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { isLoading = true; scope.launch { MuttRepository.signIn(email, password); isLoading = false } },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White) else Text("Login")
        }
    }
}

@Composable
fun AdminDashboard(onNavigate: (String) -> Unit) {
    val sections = listOf(
        "Institutional Content" to listOf(
            "Home Screen" to Icons.Default.Home,
            "About Us" to Icons.Default.Info,
            "Peethadhipati" to Icons.Default.Person,
            "Guru Parampara" to Icons.Default.HistoryEdu,
            "Shlokas" to Icons.Default.MenuBook,
            "Panchanga" to Icons.Default.CalendarMonth,
            "Branches" to Icons.Default.AccountTree
        ),
        "Media & Gallery" to listOf(
            "Announcements" to Icons.Default.Campaign,
            "News" to Icons.Default.Newspaper,
            "Festivals" to Icons.Default.Festival,
            "Special Events" to Icons.Default.Star,
            "Events" to Icons.Default.Event,
            "App Popups" to Icons.Default.AdsClick,
            "Photo Albums" to Icons.Default.Collections,
            "Videos" to Icons.Default.VideoLibrary,
            "Publications" to Icons.Default.LibraryBooks
        ),
        "Registrations & Logistics" to listOf(
            "Sevas" to Icons.Default.VolunteerActivism,
            "Volunteer Program" to Icons.Default.Groups,
            "Membership Program" to Icons.Default.Badge,
            "Registrations" to Icons.Default.HowToReg,
            "Contact Settings" to Icons.Default.ContactSupport
        ),
        "Permanent Records" to listOf(
            "Verified Seva Records" to Icons.Default.AssignmentTurnedIn,
            "Verified Members" to Icons.Default.CardMembership,
            "Verified Volunteers" to Icons.Default.GroupWork
        ),
        "System" to listOf(
            "Theme" to Icons.Default.Palette
        )
    )

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sections.forEach { (sectionTitle, items) ->
            item {
                Text(
                    text = sectionTitle.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Terracotta.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { (title, icon) ->
                                Card(
                                    onClick = { onNavigate(title) },
                                    modifier = Modifier.weight(1f).height(90.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = Terracotta, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            title, 
                                            style = MaterialTheme.typography.labelSmall, 
                                            fontWeight = FontWeight.Bold, 
                                            color = Color(0xFF3E2723),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FileUploadButton(
    label: String,
    folder: String,
    extension: String,
    mimeType: String,
    onFileUploaded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch {
                val result = if (mimeType.startsWith("image")) {
                    MuttRepository.uploadCompressedImage(context, it, folder)
                } else {
                    MuttRepository.uploadFile(it, folder, extension)
                }
                if (result.isSuccess) {
                    onFileUploaded(result.getOrThrow())
                    Toast.makeText(context, "File Uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
                isUploading = false
            }
        }
    }

    OutlinedButton(
        onClick = { launcher.launch(mimeType) },
        modifier = modifier.fillMaxWidth(),
        enabled = !isUploading,
        border = BorderStroke(1.dp, Terracotta)
    ) {
        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Terracotta)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Uploading...", color = Terracotta)
        } else {
            Icon(if (mimeType.startsWith("image")) Icons.Default.CloudUpload else Icons.Default.PictureAsPdf, contentDescription = null, tint = Terracotta)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Terracotta)
        }
    }
}

@Composable
fun ImageUploadButton(label: String, onImageUploaded: (String) -> Unit, modifier: Modifier = Modifier) {
    FileUploadButton(
        label = label,
        folder = "uploads",
        extension = "jpg",
        mimeType = "image/*",
        onFileUploaded = onImageUploaded,
        modifier = modifier
    )
}

@Composable
fun AnnouncementManager(onFinished: () -> Unit) {
    val items by MuttRepository.announcements.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Announcements", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Image" else "Replace Image", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) Text("Image attached", color = Color(0xFF1B5E20), fontSize = 10.sp)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { if (content.isNotBlank()) { isSaving = true; scope.launch { MuttRepository.saveAnnouncement(Announcement(content = content, imageUrl = imageUrl)); content = ""; imageUrl = ""; isSaving = false } } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Post", color = Color.White) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(headlineContent = { Text(item.content, color = Color(0xFF3E2723)) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteAnnouncement(item.id, item.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@Composable
fun EventManager(onFinished: () -> Unit) {
    val items by MuttRepository.events.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    BackHandler(enabled = showEditor) {
        showEditor = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Events Management", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
            Button(onClick = { selectedEvent = null; showEditor = true }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("Add Event", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { event ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    ListItem(
                        headlineContent = { Text(event.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(event.location, color = Color.Gray) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { selectedEvent = event; showEditor = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                                IconButton(onClick = { scope.launch { MuttRepository.deleteEvent(event.id, event.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White)
                    )
                }
            }
        }
    }

    if (showEditor) {
        EventEditorDialog(
            event = selectedEvent,
            onDismiss = { showEditor = false },
            onSave = { updatedEvent ->
                scope.launch {
                    MuttRepository.saveEvent(updatedEvent)
                    showEditor = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorDialog(event: Event?, onDismiss: () -> Unit, onSave: (Event) -> Unit) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var desc by remember { mutableStateOf(event?.description ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var imageUrl by remember { mutableStateOf(event?.imageUrl ?: "") }
    var galleryUrls by remember { mutableStateOf(event?.galleryImageUrls ?: emptyList<String>()) }
    var regUrl by remember { mutableStateOf(event?.registrationUrl ?: "") }
    var shouldPushNotify by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val newEvent = Event(
                    id = event?.id ?: "",
                    title = title,
                    description = desc,
                    location = location,
                    imageUrl = imageUrl,
                    galleryImageUrls = galleryUrls,
                    registrationUrl = regUrl,
                    startDate = event?.startDate ?: System.currentTimeMillis(),
                    endDate = event?.endDate ?: (System.currentTimeMillis() + 86400000)
                )
                onSave(newEvent)
                if (shouldPushNotify) {
                    scope.launch {
                        MuttRepository.pushNotification(AppNotification(
                            title = "New Event: $title",
                            message = desc.take(100),
                            type = "Event"
                        ))
                    }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) } },
        title = { Text(if (event == null) "Add Event" else "Edit Event", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = regUrl, onValueChange = { regUrl = it }, label = { Text("Registration Link") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Banner" else "Replace Banner", onImageUploaded = { imageUrl = it })
                if (imageUrl.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Banner attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Event Gallery (Multiple Images)", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                ImageUploadButton(label = "Add Image to Gallery", onImageUploaded = { galleryUrls = galleryUrls + it })
                
                galleryUrls.forEach { url ->
                    ListItem(
                        headlineContent = { Text(url, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        trailingContent = {
                            IconButton(onClick = { scope.launch { MuttRepository.deleteImage(url); galleryUrls = galleryUrls - url } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color(0xFFF5F5F5))
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = shouldPushNotify, onCheckedChange = { shouldPushNotify = it })
                    Text("Push as Popup Notification", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun SevaManager(onFinished: () -> Unit) {
    val items by MuttRepository.sevas.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var selectedSeva by remember { mutableStateOf<Seva?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    BackHandler(enabled = showEditor) {
        showEditor = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Sevas Management", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
            Button(onClick = { selectedSeva = null; showEditor = true }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("Add Seva", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { seva ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    ListItem(
                        headlineContent = { Text(seva.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("₹${seva.amount}", color = Color(0xFF1B5E20)) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { selectedSeva = seva; showEditor = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                                IconButton(onClick = { scope.launch { MuttRepository.deleteSeva(seva.id, seva.imageUrl, seva.paymentQrUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White)
                    )
                }
            }
        }
    }

    if (showEditor) {
        SevaEditorDialog(
            seva = selectedSeva,
            onDismiss = { showEditor = false },
            onSave = { updatedSeva ->
                scope.launch {
                    MuttRepository.saveSeva(updatedSeva)
                    showEditor = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevaEditorDialog(seva: Seva?, onDismiss: () -> Unit, onSave: (Seva) -> Unit) {
    var title by remember { mutableStateOf(seva?.title ?: "") }
    var tag by remember { mutableStateOf(seva?.tag ?: "SANKALPA") }
    var desc by remember { mutableStateOf(seva?.description ?: "") }
    var upiId by remember { mutableStateOf(seva?.upiId ?: "") }
    var amount by remember { mutableStateOf(seva?.amount?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(seva?.imageUrl ?: "") }
    var qrUrl by remember { mutableStateOf(seva?.paymentQrUrl ?: "") }
    var isFeatured by remember { mutableStateOf(seva?.isFeatured ?: false) }
    var formFields by remember { mutableStateOf(seva?.formFields ?: emptyList()) }
    
    var showFormEditor by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (seva == null) "Add Seva" else "Edit Seva", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag (e.g. SANKALPA)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = upiId, onValueChange = { upiId = it }, label = { Text("UPI ID") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(
                    value = amount, 
                    onValueChange = { if (it.all { char -> char.isDigit() }) amount = it }, 
                    label = { Text("Amount (₹)") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723))
                )
                
                ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Seva Image" else "Replace Seva Image", onImageUploaded = { imageUrl = it })
                if (imageUrl.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Image attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
                    }
                }

                ImageUploadButton(label = if (qrUrl.isEmpty()) "Upload Payment QR" else "Replace Payment QR", onImageUploaded = { qrUrl = it })
                if (qrUrl.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("QR attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { scope.launch { MuttRepository.deleteImage(qrUrl); qrUrl = "" } }) { Text("Clear", color = Color.Red) }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Show as Featured", color = Color(0xFF3E2723))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showFormEditor = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                    Icon(Icons.Default.DynamicForm, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Custom Form (${formFields.size} fields)", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Seva(
                        id = seva?.id ?: "",
                        title = title,
                        tag = tag,
                        description = desc,
                        amount = amount.toIntOrNull() ?: 0,
                        imageUrl = imageUrl,
                        paymentQrUrl = qrUrl,
                        upiId = upiId,
                        isFeatured = isFeatured,
                        formFields = formFields
                    )
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) } }
    )

    if (showFormEditor) {
        SevaFormEditorDialog(
            fields = formFields,
            onDismiss = { showFormEditor = false },
            onSave = { updatedFields ->
                formFields = updatedFields
                showFormEditor = false
            }
        )
    }
}

@Composable
fun SevaFormEditorDialog(fields: List<SevaFormField>, onDismiss: () -> Unit, onSave: (List<SevaFormField>) -> Unit) {
    var editFields by remember { mutableStateOf(fields) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Text("Form Field Builder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(16.dp))
                
                editFields.forEachIndexed { index, field ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = field.label, onValueChange = { editFields = editFields.toMutableList().apply { this[index] = field.copy(label = it) } }, label = { Text("Field Label") }, modifier = Modifier.weight(1f))
                                IconButton(onClick = { editFields = editFields.toMutableList().apply { removeAt(index) } }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Type: ", style = MaterialTheme.typography.bodySmall)
                                AssistChip(onClick = { editFields = editFields.toMutableList().apply { this[index] = field.copy(type = "Text") } }, label = { Text("Text") }, border = if(field.type == "Text") BorderStroke(1.dp, Terracotta) else null)
                                Spacer(modifier = Modifier.width(4.dp))
                                AssistChip(onClick = { editFields = editFields.toMutableList().apply { this[index] = field.copy(type = "Number") } }, label = { Text("Number") }, border = if(field.type == "Number") BorderStroke(1.dp, Terracotta) else null)
                            }
                        }
                    }
                }
                
                Button(onClick = { editFields = editFields + SevaFormField(label = "New Field", type = "Text") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Text("Add Field", color = Color.White)
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { onSave(editFields) }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save Form", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun RegistrationManager(onFinished: () -> Unit) {
    val items by MuttRepository.userRegistrations.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedReg by remember { mutableStateOf<Registration?>(null) }

    BackHandler(enabled = selectedReg != null) {
        selectedReg = null
    }

    LaunchedEffect(Unit) {
        while(true) {
            MuttRepository.cleanupOldRegistrations()
            kotlinx.coroutines.delay(60000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Devotee Registrations", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No active registrations.", color = Color.Gray) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items.sortedByDescending { it.timestamp }) { reg ->
                ElevatedCard(modifier = Modifier.fillMaxWidth().clickable { selectedReg = reg }, colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(reg.itemTitle, fontWeight = FontWeight.Bold, color = Terracotta)
                            Text(reg.status, style = MaterialTheme.typography.labelSmall, color = if(reg.status.contains("Pending")) Color.Red else Color(0xFF1B5E20))
                        }
                        Text("Phone: ${reg.phoneNumber}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(java.text.SimpleDateFormat("dd MMM, hh:mm a").format(java.util.Date(reg.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }

    selectedReg?.let { registration ->
        Dialog(onDismissRequest = { selectedReg = null }) {
            Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text("Registration Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Terracotta)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Item: ${registration.itemTitle}", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    Text("Phone: ${registration.phoneNumber}", color = Color.Gray)
                    Text("UTR: ${registration.utrNumber}", color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text("Form Responses:", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    registration.formResponses.forEach { (label, value) ->
                        Text("$label: $value", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { 
                            scope.launch { 
                                val devoteeName = registration.formResponses.entries.find { 
                                    it.key.contains("Name", ignoreCase = true) 
                                }?.value ?: "Devotee"
                                
                                MuttRepository.saveSevaRecord(SevaRecord(
                                    devoteeName = devoteeName,
                                    sevaName = registration.itemTitle,
                                    amountPaid = registration.paidAmount,
                                    utr = registration.utrNumber,
                                    type = when {
                                        registration.itemTitle.contains("Volunteer", ignoreCase = true) -> "Volunteer"
                                        registration.itemTitle.contains("Member", ignoreCase = true) -> "Member"
                                        else -> "Seva"
                                    }
                                ))
                                MuttRepository.updateRegistrationStatus(registration.id, "Verified")
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("sms:${registration.phoneNumber}")
                                    putExtra("sms_body", "Namaste, your Seva (${registration.itemTitle}) registration has been accepted and verified. - Sri Kanva Matha")
                                }
                                context.startActivity(intent)
                                selectedReg = null 
                            } 
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                            Text("Verify & SMS", color = Color.White)
                        }
                        Button(onClick = { scope.launch { MuttRepository.updateRegistrationStatus(registration.id, "Rejected"); selectedReg = null } }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("Reject", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { selectedReg = null }, modifier = Modifier.align(Alignment.End)) { Text("Close", color = Color.Gray) }
                }
            }
        }
    }
}

@Composable
fun ShlokaManager(onFinished: () -> Unit) {
    val items by MuttRepository.shlokas.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var pdfUrl by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Shlokas", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Preview Content / Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        
        Spacer(modifier = Modifier.height(12.dp))
        FileUploadButton(
            label = if (pdfUrl.isEmpty()) "Upload Shloka PDF" else "Replace PDF",
            folder = "shlokas",
            extension = "pdf",
            mimeType = "application/pdf",
            onFileUploaded = { pdfUrl = it }
        )
        if (pdfUrl.isNotEmpty()) {
            Text("PDF Attached", color = Color(0xFF1B5E20), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = {
                if (title.isNotBlank()) {
                    isSaving = true
                    scope.launch {
                        MuttRepository.saveShloka(Shloka(id = editingId, title = title, content = content, pdfUrl = pdfUrl))
                        title = ""; content = ""; pdfUrl = ""; editingId = ""; isSaving = false
                    }
                }
            }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) }, 
                supportingContent = { Text(item.content, maxLines = 1, color = Color.Gray) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { title = item.title; content = item.content; pdfUrl = item.pdfUrl; editingId = item.id }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                        IconButton(onClick = { scope.launch { MuttRepository.deleteShloka(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.White)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangManager(onFinished: () -> Unit) {
    val items by MuttRepository.panchanga.collectAsState(emptyList())
    val homeConfig by MuttRepository.homeConfig.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var samvatsara by remember { mutableStateOf("") }
    var ayana by remember { mutableStateOf("") }
    var ritu by remember { mutableStateOf("") }
    var masa by remember { mutableStateOf("") }
    var paksha by remember { mutableStateOf("") }
    var tithi by remember { mutableStateOf("") }
    var tithiEnd by remember { mutableStateOf("") }
    var nextTithi by remember { mutableStateOf("") }
    var nakshatra by remember { mutableStateOf("") }
    var nakshatraEnd by remember { mutableStateOf("") }
    var nextNakshatra by remember { mutableStateOf("") }
    var yoga by remember { mutableStateOf("") }
    var karana by remember { mutableStateOf("") }
    var souraMasa by remember { mutableStateOf("") }
    var rahuKala by remember { mutableStateOf("") }
    var yamaganda by remember { mutableStateOf("") }
    var suryodaya by remember { mutableStateOf("") }
    var suryasta by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isSpecial by remember { mutableStateOf(false) }
    var shouldPushNotify by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(datePickerState.selectedDateMillis, items) {
        val selected = datePickerState.selectedDateMillis ?: return@LaunchedEffect
        val existing = items.find { 
            try {
                val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = it.date }
                val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = selected }
                cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
            } catch (e: Exception) { false }
        }
        if (existing != null) {
            samvatsara = existing.samvatsara
            ayana = existing.ayana
            ritu = existing.ritu
            masa = existing.masa
            paksha = existing.paksha
            tithi = existing.tithi
            tithiEnd = existing.tithiEnd
            nextTithi = existing.nextTithi
            nakshatra = existing.nakshatra
            nakshatraEnd = existing.nakshatraEnd
            nextNakshatra = existing.nextNakshatra
            yoga = existing.yoga
            karana = existing.karana
            souraMasa = existing.souraMasa
            rahuKala = existing.rahuKala
            yamaganda = existing.yamagandaKala
            suryodaya = existing.suryodaya
            suryasta = existing.suryasta
            note = existing.specialNote
            isSpecial = existing.isSpecial
            editingId = existing.id
        } else {
            samvatsara = ""; ayana = ""; ritu = ""; masa = ""; paksha = ""; tithi = ""; tithiEnd = ""; nextTithi = ""; nakshatra = ""; nakshatraEnd = ""; nextNakshatra = ""; yoga = ""; karana = ""; souraMasa = ""; rahuKala = ""; yamaganda = ""; suryodaya = ""; suryasta = ""; note = ""; isSpecial = false; editingId = ""
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).imePadding()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Panchanga Calendar", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            
            // Auto-Calculate Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = {
                    scope.launch {
                        try {
                            val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            val p = MuttRepository.calculateLocalPanchanga(selectedDate)
                            samvatsara = p.samvatsara
                            ayana = p.ayana
                            ritu = p.ritu
                            masa = p.masa
                            paksha = p.paksha
                            tithi = p.tithi
                            tithiEnd = p.tithiEnd
                            nextTithi = p.nextTithi
                            nakshatra = p.nakshatra
                            nakshatraEnd = p.nakshatraEnd
                            nextNakshatra = p.nextNakshatra
                            yoga = p.yoga
                            karana = p.karana
                            souraMasa = p.souraMasa
                            rahuKala = p.rahuKala
                            yamaganda = p.yamagandaKala
                            suryodaya = p.suryodaya
                            suryasta = p.suryasta
                            note = p.specialNote
                            Toast.makeText(context, "Calculation complete. Please review and Save.", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Calculation Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp), tint = Terracotta)
                    Spacer(Modifier.width(4.dp))
                    Text("Auto-Calculate", fontSize = 12.sp, color = Terracotta)
                }
            }
        }
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            DatePicker(state = datePickerState, showModeToggle = false, title = null, headline = null, modifier = Modifier.fillMaxWidth())
            Column(modifier = Modifier.padding(16.dp)) {
                val dateFormatted = datePickerState.selectedDateMillis?.let { java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "Select a date"
                Text("Details for $dateFormatted", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = samvatsara, onValueChange = { samvatsara = it }, label = { Text("Samvatsara") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = ayana, onValueChange = { ayana = it }, label = { Text("Ayana") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = ritu, onValueChange = { ritu = it }, label = { Text("Ritu") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = masa, onValueChange = { masa = it }, label = { Text("Masa (Lunar)") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = paksha, onValueChange = { paksha = it }, label = { Text("Paksha") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = tithi, onValueChange = { tithi = it }, label = { Text("Tithi") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = tithiEnd, onValueChange = { tithiEnd = it }, label = { Text("Ends At") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                if (tithiEnd.isNotEmpty()) {
                    OutlinedTextField(value = nextTithi, onValueChange = { nextTithi = it }, label = { Text("Next Tithi") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = nakshatra, onValueChange = { nakshatra = it }, label = { Text("Nakshatra") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = nakshatraEnd, onValueChange = { nakshatraEnd = it }, label = { Text("Ends At") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                if (nakshatraEnd.isNotEmpty()) {
                    OutlinedTextField(value = nextNakshatra, onValueChange = { nextNakshatra = it }, label = { Text("Next Nakshatra") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = yoga, onValueChange = { yoga = it }, label = { Text("Yoga") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = karana, onValueChange = { karana = it }, label = { Text("Karana") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                OutlinedTextField(value = souraMasa, onValueChange = { souraMasa = it }, label = { Text("Soura Masa (Solar)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = suryodaya, onValueChange = { suryodaya = it }, label = { Text("Suryodaya") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = suryasta, onValueChange = { suryasta = it }, label = { Text("Suryasta") }, modifier = Modifier.weight(1f), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                }
                OutlinedTextField(value = rahuKala, onValueChange = { rahuKala = it }, label = { Text("Rahu Kala") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = yamaganda, onValueChange = { yamaganda = it }, label = { Text("Yamaganda Kala") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Special Note / Festivals") }, modifier = Modifier.fillMaxWidth(), minLines = 2, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Checkbox(checked = isSpecial, onCheckedChange = { isSpecial = it })
                    Text("Mark as Special Day", color = Color(0xFF3E2723))
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Checkbox(checked = shouldPushNotify, onCheckedChange = { shouldPushNotify = it })
                    Text("Push Notification to All Users", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (editingId.isNotEmpty()) {
                        TextButton(onClick = { scope.launch { MuttRepository.deletePanchang(editingId); Toast.makeText(context, "Entry Deleted", Toast.LENGTH_SHORT).show(); editingId = "" } }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Delete Entry") }
                    } else { Spacer(modifier = Modifier.width(1.dp)) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { 
                                val date = datePickerState.selectedDateMillis ?: return@Button
                                isSaving = true
                                scope.launch { 
                                    val panchang = Panchang(
                                        id = editingId, 
                                        date = date, 
                                        samvatsara = samvatsara,
                                        ayana = ayana,
                                        ritu = ritu,
                                        masa = masa,
                                        paksha = paksha,
                                        tithi = tithi, 
                                        tithiEnd = tithiEnd,
                                        nextTithi = nextTithi,
                                        nakshatra = nakshatra, 
                                        nakshatraEnd = nakshatraEnd,
                                        nextNakshatra = nextNakshatra,
                                        yoga = yoga, 
                                        karana = karana, 
                                        souraMasa = souraMasa,
                                        rahuKala = rahuKala,
                                        yamagandaKala = yamaganda,
                                        suryodaya = suryodaya,
                                        suryasta = suryasta,
                                        specialNote = note, 
                                        isSpecial = isSpecial
                                    )
                                    val result = MuttRepository.savePanchang(panchang)
                                    if (result.isSuccess) {
                                        if (shouldPushNotify) {
                                            val dateStr = java.text.SimpleDateFormat("dd MMM").format(java.util.Date(date))
                                            MuttRepository.pushNotification(AppNotification(
                                                title = "Panchanga Updated",
                                                message = "New details for $dateStr are now available.",
                                                type = "Panchanga"
                                            ))
                                        }
                                        Toast.makeText(context, "Panchanga Saved!", Toast.LENGTH_SHORT).show()
                                    }
                                    isSaving = false 
                                } 
                            }, 
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                        ) { Text("Save Details", color = Color.White) }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialLinkManager(onFinished: () -> Unit) {
    val items by MuttRepository.socialLinks.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var platform by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Social Links", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = platform, onValueChange = { platform = it }, label = { Text("Platform (e.g. Facebook)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Page URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Icon" else "Replace Icon", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) Text("Icon attached", color = Color(0xFF1B5E20), fontSize = 10.sp)
        OutlinedTextField(value = audience, onValueChange = { audience = it }, label = { Text("Audience Info") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { if (platform.isNotBlank()) { isSaving = true; scope.launch { MuttRepository.saveSocialLink(SocialLink(platform = platform, url = url, imageUrl = imageUrl, audience = audience)); platform = ""; url = ""; imageUrl = ""; audience = ""; isSaving = false } } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Add", color = Color.White) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(headlineContent = { Text("${item.platform}: ${item.audience}", color = Color(0xFF3E2723)) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteSocialLink(item.id, item.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@Composable
fun HomeScreenEditor(onFinished: () -> Unit) {
    val currentConfig by MuttRepository.homeConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf(currentConfig.bannerTitle) }
    var subtitle by remember { mutableStateOf(currentConfig.bannerSubtitle) }
    var imageUrl by remember { mutableStateOf(currentConfig.bannerImageUrl) }
    var wisdom by remember { mutableStateOf(currentConfig.dailyWisdom) }
    var liveUrl by remember { mutableStateOf(currentConfig.liveDarshanUrl) }
    var appLogoUrl by remember { mutableStateOf(currentConfig.appLogoUrl) }
    var splashLogoUrl by remember { mutableStateOf(currentConfig.splashLogoUrl) }
    var waPrefix by remember { mutableStateOf(currentConfig.whatsappSharePrefix) }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Edit Home Screen", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Banner Subtitle") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Banner Image" else "Replace Banner", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Banner attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }
        
        Text("Logos", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3E2723), modifier = Modifier.padding(vertical = 8.dp))
        
        // Home Screen Logo (Top Bar)
        ImageUploadButton(label = if (appLogoUrl.isEmpty()) "Upload Home Logo (Top Bar)" else "Replace Home Logo", onImageUploaded = { appLogoUrl = it })
        if (appLogoUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Home Logo attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(appLogoUrl); appLogoUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }
        
        // Splash Screen Logo
        ImageUploadButton(label = if (splashLogoUrl.isEmpty()) "Upload Splash Logo" else "Replace Splash Logo", onImageUploaded = { splashLogoUrl = it })
        if (splashLogoUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Splash Logo attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(splashLogoUrl); splashLogoUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Panchanga Automation", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3E2723))
        OutlinedTextField(value = waPrefix, onValueChange = { waPrefix = it }, label = { Text("WhatsApp Share Message Prefix") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = wisdom, onValueChange = { wisdom = it }, label = { Text("Daily Wisdom") }, modifier = Modifier.fillMaxWidth(), minLines = 2, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = liveUrl, onValueChange = { liveUrl = it }, label = { Text("Live Darshan URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { isSaving = true; scope.launch { MuttRepository.updateHomeConfig(HomeConfig(title, subtitle, imageUrl, wisdom, liveUrl, appLogoUrl, splashLogoUrl, currentConfig.panchangaSourceUrl, waPrefix)); isSaving = false; onFinished() } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        }
    }
}

@Composable
fun AboutUsEditor(onFinished: () -> Unit) {
    val currentConfig by MuttRepository.aboutConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf(currentConfig.title) }
    var content by remember { mutableStateOf(currentConfig.content) }
    var imageUrl by remember { mutableStateOf(currentConfig.imageUrl) }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Edit About Us", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 5, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload About Us Image" else "Replace Image", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Image attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { isSaving = true; scope.launch { MuttRepository.updateAboutConfig(AboutConfig(title, content, imageUrl)); isSaving = false; onFinished() } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        }
    }
}

@Composable
fun BranchManager(onFinished: () -> Unit) {
    val items by MuttRepository.branches.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Branches", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { if (name.isNotBlank()) { isSaving = true; scope.launch { MuttRepository.saveBranch(Branch(name = name, address = address, contact = contact)); name = ""; address = ""; contact = ""; isSaving = false } } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(headlineContent = { Text(item.name, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteBranch(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@Composable
fun PublicationManager(onFinished: () -> Unit) {
    val items by MuttRepository.publications.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var type by remember { mutableStateOf("Book") }; var name by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }; var cost by remember { mutableStateOf("") }; var imageUrl by remember { mutableStateOf("") }; var editingId by remember { mutableStateOf("") }; var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Publications", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Cost") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Book Cover" else "Replace Cover", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) Text("Cover attached", color = Color(0xFF1B5E20), fontSize = 10.sp)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { if (name.isNotBlank()) { isSaving = true; scope.launch { val result = MuttRepository.savePublication(Publication(id = editingId, type = type, name = name, description = desc, cost = cost, imageUrl = imageUrl)); if (result.isSuccess) { Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show(); name = ""; desc = ""; cost = ""; imageUrl = ""; editingId = ""; type = "Book" }; isSaving = false } } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text(if (editingId.isEmpty()) "Add" else "Update", color = Color.White) }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(headlineContent = { Text(item.name, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, trailingContent = { Row { IconButton(onClick = { name = item.name; desc = item.description; cost = item.cost; imageUrl = item.imageUrl; type = item.type; editingId = item.id }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }; IconButton(onClick = { scope.launch { MuttRepository.deletePublication(item.id, item.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@Composable
fun ContactEditor(onFinished: () -> Unit) {
    val currentConfig by MuttRepository.contactConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf(currentConfig.email) }; var phone by remember { mutableStateOf(currentConfig.phone) }; var address by remember { mutableStateOf(currentConfig.address) }; var mapUrl by remember { mutableStateOf(currentConfig.mapUrl) }; var volunteerUrl by remember { mutableStateOf(currentConfig.volunteerFormUrl) }; var membershipUrl by remember { mutableStateOf(currentConfig.membershipFormUrl) }; var feedbackUrl by remember { mutableStateOf(currentConfig.feedbackFormUrl) }; var facebookUrl by remember { mutableStateOf(currentConfig.facebookUrl) }; var instagramUrl by remember { mutableStateOf(currentConfig.instagramUrl) }; var youtubeUrl by remember { mutableStateOf(currentConfig.youtubeUrl) }; var privacyUrl by remember { mutableStateOf(currentConfig.privacyPolicyUrl) }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Edit Contact & Social Links", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Contact Email") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = mapUrl, onValueChange = { mapUrl = it }, label = { Text("Google Maps URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(16.dp)); Text("Social Media", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
        OutlinedTextField(value = facebookUrl, onValueChange = { facebookUrl = it }, label = { Text("Facebook") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = instagramUrl, onValueChange = { instagramUrl = it }, label = { Text("Instagram") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = youtubeUrl, onValueChange = { youtubeUrl = it }, label = { Text("YouTube") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(16.dp)); Text("External Forms", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
        OutlinedTextField(value = volunteerUrl, onValueChange = { volunteerUrl = it }, label = { Text("Volunteer URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = membershipUrl, onValueChange = { membershipUrl = it }, label = { Text("Membership URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = feedbackUrl, onValueChange = { feedbackUrl = it }, label = { Text("Feedback URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = privacyUrl, onValueChange = { privacyUrl = it }, label = { Text("Privacy Policy URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { isSaving = true; scope.launch { MuttRepository.updateContactConfig(ContactConfig(email, phone, address, mapUrl, volunteerUrl, membershipUrl, feedbackUrl, facebookUrl, instagramUrl, youtubeUrl, privacyUrl)); isSaving = false; onFinished() } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save Settings", color = Color.White) }
        }
    }
}

@Composable
fun ThemeEditor(onFinished: () -> Unit) {
    val currentConfig = ThemeConfig.current.value
    var primaryColor by remember { mutableStateOf(currentConfig.primaryColor) }
    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Theme Customization", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
            listOf(Terracotta, DarkOrange, Color.Red, Color.Blue).forEach { color ->
                Surface(modifier = Modifier.size(40.dp).clickable { primaryColor = color }, color = color, shape = CircleShape, border = if (primaryColor == color) BorderStroke(2.dp, Color.Black) else null) {}
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { ThemeConfig.update(currentConfig.copy(primaryColor = primaryColor)); onFinished() }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Apply", color = Color.White) }
    }
}

@Composable
fun SevaRecordManager(title: String, type: String, onFinished: () -> Unit) {
    val allItems by MuttRepository.sevaRecords.collectAsState(emptyList())
    val items = remember(allItems) { allItems.filter { it.type == type } }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (items.isEmpty()) Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No verified records yet.", color = Color.Gray) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items.sortedByDescending { it.timestamp }) { record ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.devoteeName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFF3E2723))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("₹${record.amountPaid}", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { scope.launch { MuttRepository.deleteSevaRecord(record.id) } }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Record", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        Text(record.sevaName, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("UTR: ${record.utr}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(java.text.SimpleDateFormat("dd MMM, hh:mm a").format(java.util.Date(record.timestamp)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoManager(onFinished: () -> Unit) {
    val items by MuttRepository.photoAlbums.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var editTitle by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("General") }
    var currentImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Photo Gallery Management", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Album Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
            OutlinedTextField(value = editCategory, onValueChange = { editCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Photos in Album: ${currentImageUrls.size}", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            ImageUploadButton(label = "Add Photo to Album", onImageUploaded = { currentImageUrls = currentImageUrls + it })
            currentImageUrls.forEach { url ->
                ListItem(headlineContent = { Text(url, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Gray) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteImage(url); currentImageUrls = currentImageUrls - url } }) { Icon(Icons.Default.Delete, contentDescription = "Delete Photo", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
            }
            Button(onClick = { if (editTitle.isNotBlank() && currentImageUrls.isNotEmpty()) { isSaving = true; scope.launch { MuttRepository.savePhotoAlbum(PhotoAlbum(title = editTitle, category = editCategory, imageUrls = currentImageUrls)); editTitle = ""; editCategory = "General"; currentImageUrls = emptyList(); isSaving = false } } }, modifier = Modifier.align(Alignment.End).padding(top = 8.dp), enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Create Album", color = Color.White) }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            items.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, 
                    supportingContent = { Text("${item.imageUrls.size} photos", color = Color.Gray) }, 
                    trailingContent = { 
                        Row {
                            IconButton(onClick = { scope.launch { MuttRepository.deletePhotoAlbum(item.id, item.imageUrls) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                        }
                    }, 
                    colors = ListItemDefaults.colors(containerColor = Color.White)
                )
            }
        }
    }
}

@Composable
fun VideoManager(onFinished: () -> Unit) {
    val items by MuttRepository.videos.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }; var url by remember { mutableStateOf("") }; var category by remember { mutableStateOf("General") }; var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Video Gallery Management", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Video Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Video URL (YouTube)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Button(onClick = { if (title.isNotBlank()) { isSaving = true; scope.launch { MuttRepository.saveVideo(VideoItem(title = title, url = url, category = category)); title = ""; url = ""; category = "General"; isSaving = false } } }, modifier = Modifier.align(Alignment.End).padding(top = 8.dp), enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Add Video", color = Color.White) }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(headlineContent = { Text(item.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteVideo(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeethadhipatiEditor(onFinished: () -> Unit) {
    val currentConfig by MuttRepository.peethadhipatiConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(currentConfig.name) }; var title by remember { mutableStateOf(currentConfig.title) }; var vision by remember { mutableStateOf(currentConfig.vision) }; var imageUrl by remember { mutableStateOf(currentConfig.imageUrl) }; var videoUrl by remember { mutableStateOf(currentConfig.videoUrl) }; var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Edit Present Peethadhipati", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = vision, onValueChange = { vision = it }, label = { Text("Vision / Message") }, modifier = Modifier.fillMaxWidth(), minLines = 4, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Photo" else "Replace Photo", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Photo attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }
        OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video Message URL (YouTube)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = { isSaving = true; scope.launch { MuttRepository.updatePeethadhipatiConfig(PeethadhipatiConfig(name, title, imageUrl, vision, videoUrl)); isSaving = false; onFinished() } }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        }
    }
}

@Composable
fun GuruManager(onFinished: () -> Unit) {
    val gurus by MuttRepository.gurus.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var selectedGuru by remember { mutableStateOf<Guru?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Guru Parampara", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
            Button(onClick = { selectedGuru = null; showEditor = true }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("Add Guru", color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(gurus.sortedBy { it.order }) { guru ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    ListItem(
                        headlineContent = { Text(guru.name, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Order: ${guru.order} | ${guru.lifespan}", color = Color.Gray) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { selectedGuru = guru; showEditor = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                                IconButton(onClick = { scope.launch { MuttRepository.deleteGuru(guru.id, guru.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White)
                    )
                }
            }
        }
    }

    if (showEditor) {
        GuruEditorDialog(
            guru = selectedGuru,
            onDismiss = { showEditor = false },
            onSave = { updatedGuru ->
                scope.launch {
                    MuttRepository.saveGuru(updatedGuru)
                    showEditor = false
                }
            }
        )
    }
}

@Composable
fun GuruEditorDialog(guru: Guru?, onDismiss: () -> Unit, onSave: (Guru) -> Unit) {
    var name by remember { mutableStateOf(guru?.name ?: "") }
    var title by remember { mutableStateOf(guru?.title ?: "") }
    var lifespan by remember { mutableStateOf(guru?.lifespan ?: "") }
    var shortDesc by remember { mutableStateOf(guru?.shortDescription ?: "") }
    var biography by remember { mutableStateOf(guru?.biography ?: "") }
    var timeline by remember { mutableStateOf(guru?.timeline ?: "") }
    var imageUrl by remember { mutableStateOf(guru?.imageUrl ?: "") }
    var order by remember { mutableStateOf(guru?.order?.toString() ?: "0") }
    var videoUrl by remember { mutableStateOf(guru?.videoUrl ?: "") }
    var pdfUrl by remember { mutableStateOf(guru?.pdfUrl ?: "") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (guru == null) "Add Guru" else "Edit Guru", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = lifespan, onValueChange = { lifespan = it }, label = { Text("Lifespan") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Sort Order") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = shortDesc, onValueChange = { shortDesc = it }, label = { Text("Short Description") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = biography, onValueChange = { biography = it }, label = { Text("Full Biography") }, minLines = 3, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = timeline, onValueChange = { timeline = it }, label = { Text("Timeline") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                
                ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Guru Photo" else "Replace Photo", onImageUploaded = { imageUrl = it })
                if (imageUrl.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Photo attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
                    }
                }

                OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video URL") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = pdfUrl, onValueChange = { pdfUrl = it }, label = { Text("PDF URL") }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Guru(
                        id = guru?.id ?: "",
                        name = name,
                        title = title,
                        lifespan = lifespan,
                        shortDescription = shortDesc,
                        biography = biography,
                        timeline = timeline,
                        imageUrl = imageUrl,
                        order = order.toIntOrNull() ?: 0,
                        videoUrl = videoUrl,
                        pdfUrl = pdfUrl
                    )
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) } }
    )
}

@Composable
fun NewsManager(onFinished: () -> Unit) {
    val items by MuttRepository.news.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var headline by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }; var link by remember { mutableStateOf("") }; var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage News", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = headline, onValueChange = { headline = it }, label = { Text("Headline") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Short Description") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("External Link") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Button(onClick = { if (headline.isNotBlank()) { isSaving = true; scope.launch { MuttRepository.saveNews(News(headline = headline, description = desc, link = link)); headline = ""; desc = ""; link = ""; isSaving = false } } }, modifier = Modifier.align(Alignment.End).padding(top = 8.dp), enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Add News", color = Color.White) }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.sortedByDescending { it.timestamp }.forEach { item ->
            ListItem(headlineContent = { Text(item.headline, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, trailingContent = { IconButton(onClick = { scope.launch { MuttRepository.deleteNews(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) } }, colors = ListItemDefaults.colors(containerColor = Color.White))
        }
    }
}

@Composable
fun FestivalManager(onFinished: () -> Unit) {
    val items by MuttRepository.festivals.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Manage Festivals", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Festival Name") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = { Text("Date & Time") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (editingId.isNotEmpty()) {
                TextButton(onClick = { name = ""; dateTime = ""; desc = ""; editingId = "" }) { Text("Cancel Edit") }
            }
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(
                onClick = { 
                    if (name.isNotBlank()) { 
                        isSaving = true
                        scope.launch { 
                            MuttRepository.saveFestival(Festival(id = editingId, name = name, dateTime = dateTime, description = desc))
                            name = ""; dateTime = ""; desc = ""; editingId = ""; isSaving = false 
                        } 
                    } 
                }, 
                enabled = !isSaving, 
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
            ) { Text(if (editingId.isEmpty()) "Add Festival" else "Update", color = Color.White) }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.name, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, 
                supportingContent = { Text(item.dateTime, color = Color.Gray) }, 
                trailingContent = { 
                    Row {
                        IconButton(onClick = { name = item.name; dateTime = item.dateTime; desc = item.description; editingId = item.id }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                        IconButton(onClick = { scope.launch { MuttRepository.deleteFestival(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                    }
                }, 
                colors = ListItemDefaults.colors(containerColor = Color.White)
            )
        }
    }
}

@Composable
fun SpecialEventManager(onFinished: () -> Unit) {
    val items by MuttRepository.specialEvents.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isHighlighted by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Special Announcements", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        Spacer(modifier = Modifier.height(8.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Image" else "Replace Image", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Image attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isHighlighted, onCheckedChange = { isHighlighted = it })
            Text("Highlight/Pin at Top", color = Color(0xFF3E2723))
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (editingId.isNotEmpty()) {
                TextButton(onClick = { title = ""; content = ""; imageUrl = ""; isHighlighted = false; editingId = "" }) { Text("Cancel Edit") }
            }
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(
                onClick = { 
                    if (title.isNotBlank()) { 
                        isSaving = true
                        scope.launch { 
                            MuttRepository.saveSpecialEvent(SpecialEvent(id = editingId, title = title, content = content, imageUrl = imageUrl, isHighlighted = isHighlighted))
                            title = ""; content = ""; imageUrl = ""; isHighlighted = false; editingId = ""; isSaving = false 
                        } 
                    } 
                }, 
                enabled = !isSaving, 
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
            ) { Text(if (editingId.isEmpty()) "Publish" else "Update", color = Color.White) }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) }, 
                trailingContent = { 
                    Row {
                        IconButton(onClick = { title = item.title; content = item.content; imageUrl = item.imageUrl; isHighlighted = item.isHighlighted; editingId = item.id }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                        IconButton(onClick = { scope.launch { MuttRepository.deleteSpecialEvent(item.id, item.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                    }
                }, 
                colors = ListItemDefaults.colors(containerColor = Color.White)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramEditor(
    title: String,
    configState: StateFlow<ProgramConfig>,
    onSave: suspend (ProgramConfig) -> Result<Unit>,
    onFinished: () -> Unit
) {
    val currentConfig by configState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var editTitle by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("External") }
    var externalUrl by remember { mutableStateOf("") }
    var formFields by remember { mutableStateOf<List<SevaFormField>>(emptyList()) }
    var showFormEditor by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Sync state when config loads
    LaunchedEffect(currentConfig) {
        editTitle = currentConfig.title
        desc = currentConfig.description
        imageUrl = currentConfig.imageUrl
        mode = currentConfig.mode
        externalUrl = currentConfig.externalUrl
        formFields = currentConfig.formFields
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Program Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        
        Spacer(modifier = Modifier.height(12.dp))
        ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Banner Photo" else "Replace Photo", onImageUploaded = { imageUrl = it })
        if (imageUrl.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Banner attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Registration Mode", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = mode == "External", onClick = { mode = "External" })
            Text("External Link (Google Form)", modifier = Modifier.clickable { mode = "External" }, color = Color(0xFF3E2723))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = mode == "Internal", onClick = { mode = "Internal" })
            Text("Internal Form (Built in App)", modifier = Modifier.clickable { mode = "Internal" }, color = Color(0xFF3E2723))
        }

        if (mode == "External") {
            OutlinedTextField(value = externalUrl, onValueChange = { externalUrl = it }, label = { Text("Google Form URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
        } else {
            Button(onClick = { showFormEditor = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.DynamicForm, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Build Custom Form (${formFields.size} fields)", color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
            Button(onClick = {
                isSaving = true
                scope.launch {
                    val result = onSave(ProgramConfig(
                        title = editTitle,
                        description = desc,
                        imageUrl = imageUrl,
                        mode = mode,
                        externalUrl = externalUrl,
                        formFields = formFields
                    ))
                    if (result.isSuccess) {
                        Toast.makeText(context, "Settings Saved!", Toast.LENGTH_SHORT).show()
                        onFinished()
                    }
                    isSaving = false
                }
            }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save All Settings", color = Color.White) }
        }
    }

    if (showFormEditor) {
        SevaFormEditorDialog(
            fields = formFields,
            onDismiss = { showFormEditor = false },
            onSave = { updatedFields ->
                formFields = updatedFields
                showFormEditor = false
            }
        )
    }
}

@Composable
fun VolunteerProgramManager(onFinished: () -> Unit) {
    val items by MuttRepository.volunteerPrograms.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<ProgramConfig?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFDF5E6)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("Volunteer Programs", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
            Button(onClick = { selectedItem = null; showEditor = true }, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Text("Add Program", color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(items) { program ->
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    ListItem(
                        headlineContent = { Text(program.title, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Mode: ${program.mode}", color = Color.Gray) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { selectedItem = program; showEditor = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                                IconButton(onClick = { scope.launch { MuttRepository.deleteVolunteerProgram(program.id, program.imageUrl) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White)
                    )
                }
            }
        }
    }

    if (showEditor) {
        Dialog(onDismissRequest = { showEditor = false }) {
            Card(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                var editTitle by remember { mutableStateOf(selectedItem?.title ?: "") }
                var desc by remember { mutableStateOf(selectedItem?.description ?: "") }
                var imageUrl by remember { mutableStateOf(selectedItem?.imageUrl ?: "") }
                var mode by remember { mutableStateOf(selectedItem?.mode ?: "External") }
                var externalUrl by remember { mutableStateOf(selectedItem?.externalUrl ?: "") }
                var formFields by remember { mutableStateOf(selectedItem?.formFields ?: emptyList()) }
                var showFormEditorInner by remember { mutableStateOf(false) }
                var isSaving by remember { mutableStateOf(false) }
                val context = LocalContext.current

                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(if (selectedItem == null) "Add Volunteer Program" else "Edit Program", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Program Title") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    ImageUploadButton(label = if (imageUrl.isEmpty()) "Upload Banner Photo" else "Replace Photo", onImageUploaded = { imageUrl = it })
                    if (imageUrl.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Banner attached", color = Color(0xFF1B5E20), fontSize = 10.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { scope.launch { MuttRepository.deleteImage(imageUrl); imageUrl = "" } }) { Text("Clear", color = Color.Red) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Registration Mode", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = mode == "External", onClick = { mode = "External" })
                        Text("External Link (Google Form)", modifier = Modifier.clickable { mode = "External" }, color = Color(0xFF3E2723))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = mode == "Internal", onClick = { mode = "Internal" })
                        Text("Internal Form (Built in App)", modifier = Modifier.clickable { mode = "Internal" }, color = Color(0xFF3E2723))
                    }

                    if (mode == "External") {
                        OutlinedTextField(value = externalUrl, onValueChange = { externalUrl = it }, label = { Text("Google Form URL") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                    } else {
                        Button(onClick = { showFormEditorInner = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                            Icon(Icons.Default.DynamicForm, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Build Custom Form (${formFields.size} fields)", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { showEditor = false }) { Text("Cancel") }
                        if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
                        Button(onClick = {
                            isSaving = true
                            scope.launch {
                                val result = MuttRepository.saveVolunteerProgram(ProgramConfig(
                                    id = selectedItem?.id ?: "",
                                    title = editTitle,
                                    description = desc,
                                    imageUrl = imageUrl,
                                    mode = mode,
                                    externalUrl = externalUrl,
                                    formFields = formFields
                                ))
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Settings Saved!", Toast.LENGTH_SHORT).show()
                                    showEditor = false
                                }
                                isSaving = false
                            }
                        }, enabled = !isSaving, colors = ButtonDefaults.buttonColors(containerColor = Terracotta)) { Text("Save", color = Color.White) }
                    }
                }

                if (showFormEditorInner) {
                    SevaFormEditorDialog(
                        fields = formFields,
                        onDismiss = { showFormEditorInner = false },
                        onSave = { updatedFields ->
                            formFields = updatedFields
                            showFormEditorInner = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationManager(onFinished: () -> Unit) {
    val items by MuttRepository.appNotifications.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("General") }
    var editingId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFF3E2723)) }
            Text("App Popup Notifications", style = MaterialTheme.typography.titleLarge, color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5E6))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(if (editingId.isEmpty()) "Push New Notification" else "Edit Notification", fontWeight = FontWeight.Bold, color = Terracotta)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (e.g. Chaturmasya)") }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 2, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF3E2723)))
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Action Type:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("General", "Panchanga", "Event").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Terracotta, selectedLabelColor = Color.White)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    if (editingId.isNotEmpty()) {
                        TextButton(onClick = { title = ""; message = ""; type = "General"; editingId = "" }) { Text("Cancel Edit") }
                    }
                    if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta)
                    Button(
                        onClick = { 
                            if (title.isNotBlank()) { 
                                isSaving = true
                                scope.launch { 
                                    MuttRepository.saveNotification(AppNotification(id = editingId, title = title, message = message, type = type))
                                    title = ""; message = ""; type = "General"; editingId = ""; isSaving = false 
                                } 
                            } 
                        }, 
                        enabled = !isSaving, 
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) { Text(if (editingId.isEmpty()) "Push Now" else "Update", color = Color.White) }
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        Text("Active Notifications (Most recent shown as popup)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
                supportingContent = { Text(item.message, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                overlineContent = { Text(item.type, color = Terracotta, fontSize = 10.sp) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { title = item.title; message = item.message; type = item.type; editingId = item.id }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Terracotta) }
                        IconButton(onClick = { scope.launch { MuttRepository.deleteNotification(item.id) } }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red) }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.White)
            )
        }
    }
}
