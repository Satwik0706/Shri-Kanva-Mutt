package com.satwik.example.mutt_app.data

import android.util.Log
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.satwik.example.mutt_app.BuildConfig

data class Announcement(
    var id: String = "", 
    var content: String = "", 
    var title: String = "",
    var imageUrl: String = "",
    var language: String = "English",
    var timestamp: Long = System.currentTimeMillis()
)

data class Event(
    var id: String = "", 
    var title: String = "", 
    var description: String = "", 
    var imageUrl: String = "",
    var startDate: Long = 0, 
    var endDate: Long = 0,
    var expiryDate: Long = 0, // For backward compatibility, will match endDate
    var location: String = "",
    var registrationStatus: String = "Open",
    var registrationUrl: String = "",
    var galleryImageUrls: List<String> = emptyList()
)

data class SevaFormField(
    var label: String = "",
    var type: String = "Text", // Text, Dropdown, Number
    var options: String = "", // Comma separated for dropdown
    var isRequired: Boolean = true
)

data class Seva(
    var id: String = "",
    var title: String = "",
    var tag: String = "SANKALPA",
    var description: String = "",
    var amount: Int = 0,
    var imageUrl: String = "",
    var paymentQrUrl: String = "",
    var upiId: String = "",
    var isFeatured: Boolean = false,
    var formFields: List<SevaFormField> = emptyList()
)

data class Shloka(
    var id: String = "",
    var content: String = "",
    var deityImageUrl: String = "",
    var title: String = "",
    var pdfUrl: String = "",
    var timestamp: Long = System.currentTimeMillis()
)

data class Branch(
    var id: String = "",
    var name: String = "",
    var address: String = "",
    var contact: String = "",
    var email: String = "",
    var mapUrl: String = ""
)

data class SocialLink(
    var id: String = "",
    var platform: String = "",
    var handle: String = "",
    var audience: String = "",
    var url: String = "",
    var imageUrl: String = ""
)

data class Panchang(
    var id: String = "",
    var date: Long = 0,
    var samvatsara: String = "",
    var ayana: String = "",
    var ritu: String = "",
    var masa: String = "",
    var paksha: String = "",
    var tithi: String = "",
    var tithiStart: String = "",
    var tithiEnd: String = "",
    var nextTithi: String = "",
    var nakshatra: String = "",
    var nakshatraStart: String = "",
    var nakshatraEnd: String = "",
    var nextNakshatra: String = "",
    var yoga: String = "",
    var karana: String = "",
    var souraMasa: String = "",
    var rahuKala: String = "",
    var yamagandaKala: String = "",
    var suryodaya: String = "",
    var suryasta: String = "",
    var specialNote: String = "",
    var isSpecial: Boolean = false
)

data class Registration(
    var id: String = "",
    var userId: String = "guest_user",
    var phoneNumber: String = "",
    var itemId: String = "", // Event or Seva ID
    var itemTitle: String = "",
    var status: String = "Pending", // Pending, Verified, Completed
    var timestamp: Long = System.currentTimeMillis(),
    var formResponses: Map<String, String> = emptyMap(),
    var utrNumber: String = "",
    var paymentScreenshotUrl: String = "",
    var paidAmount: String = "0" // Added for record keeping
)

data class SevaRecord(
    var id: String = "",
    var devoteeName: String = "",
    var sevaName: String = "",
    var amountPaid: String = "0",
    var utr: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var type: String = "Seva" // "Seva", "Member", "Volunteer"
)

data class Publication(
    var id: String = "",
    var type: String = "Book", // Book, Magazine, Audio, etc.
    var name: String = "",
    var description: String = "",
    var cost: String = "0",
    var imageUrl: String = ""
)

data class ContactConfig(
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var mapUrl: String = "",
    var volunteerFormUrl: String = "",
    var membershipFormUrl: String = "",
    var feedbackFormUrl: String = "",
    var facebookUrl: String = "",
    var instagramUrl: String = "",
    var youtubeUrl: String = "",
    var privacyPolicyUrl: String = ""
)

data class News(
    var id: String = "",
    var headline: String = "",
    var description: String = "",
    var link: String = "",
    var timestamp: Long = System.currentTimeMillis()
)

data class PhotoAlbum(
    var id: String = "",
    var title: String = "",
    var category: String = "General", // Festival, Pravachana, Daily Darshan
    var imageUrls: List<String> = emptyList(),
    var timestamp: Long = System.currentTimeMillis()
)

data class VideoItem(
    var id: String = "",
    var title: String = "",
    var url: String = "", // YouTube or direct link
    var category: String = "General",
    var timestamp: Long = System.currentTimeMillis()
)

data class Festival(
    var id: String = "",
    var name: String = "",
    var dateTime: String = "",
    var description: String = "",
    var link: String = "",
    var timestamp: Long = System.currentTimeMillis()
)

data class SpecialEvent(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var imageUrl: String = "",
    var videoUrl: String = "",
    var isHighlighted: Boolean = false,
    var timestamp: Long = System.currentTimeMillis()
)

data class HomeConfig(
    var bannerTitle: String = "Welcome to Sri Kanva Matha",
    var bannerSubtitle: String = "Digital Home of Sri Kanva Matha",
    var bannerImageUrl: String = "",
    var dailyWisdom: String = "\"Satyam Eva Jayate\" - Truth Alone Triumphs.",
    var liveDarshanUrl: String = "",
    var appLogoUrl: String = "",
    var splashLogoUrl: String = "",
    var panchangaSourceUrl: String = "https://kanvamath.org/daypanchang.php",
    var whatsappSharePrefix: String = "*Daily Panchanga - Sri Kanva Matha*\\n\\n"
)

data class AboutConfig(
    var title: String = "About Sri Kanva Matha",
    var content: String = "History and Vision content...",
    var imageUrl: String = ""
)

data class Guru(
    var id: String = "",
    var name: String = "",
    var title: String = "",
    var lifespan: String = "",
    var shortDescription: String = "",
    var biography: String = "",
    var timeline: String = "", // Aradhana dates, etc.
    var imageUrl: String = "",
    var order: Int = 0, // For sorting
    var videoUrl: String = "",
    var pdfUrl: String = ""
)

data class PeethadhipatiConfig(
    var name: String = "",
    var title: String = "",
    var imageUrl: String = "",
    var vision: String = "",
    var videoUrl: String = ""
)

data class ProgramConfig(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var mode: String = "External", // "External" or "Internal"
    var externalUrl: String = "",
    var formFields: List<SevaFormField> = emptyList()
)

data class AppNotification(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var type: String = "General" // "General", "Panchanga", "Event"
)

object MuttRepository {
    private const val TAG = "MuttRepository"
    val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isLanguageKannada = MutableStateFlow(false)
    val isLanguageKannada: StateFlow<Boolean> = _isLanguageKannada

    fun setLanguage(isKannada: Boolean) {
        _isLanguageKannada.value = isKannada
    }

    private val collectionStatuses = MutableStateFlow<Map<String, String>>(emptyMap())

    val connectionStatus: StateFlow<String> = collectionStatuses.map { statuses ->
        val issues = statuses.filterValues { it != "Connected" }
        if (issues.isEmpty()) "Connected"
        else issues.entries.first().value // Show the first issue found
    }.stateIn(scope = CoroutineScope(Dispatchers.Main + SupervisorJob()), started = SharingStarted.Eagerly, initialValue = "Initializing...")

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser

    suspend fun signIn(email: String, pass: String) = try {
        auth.signInWithEmailAndPassword(email, pass).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    fun signOut() { auth.signOut() }

    // ... (rest of the fields)

    suspend fun uploadFile(uri: Uri, folder: String, extension: String = "jpg"): Result<String> = try {
        val fileName = "${System.currentTimeMillis()}.$extension"
        val ref = storage.reference.child("$folder/$fileName")
        ref.putFile(uri).await()
        val downloadUrl = ref.downloadUrl.await()
        Result.success(downloadUrl.toString())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun uploadImage(uri: Uri, folder: String): Result<String> = uploadFile(uri, folder, "jpg")

    suspend fun uploadCompressedImage(context: android.content.Context, uri: Uri, folder: String): Result<String> = try {
        val maxDimension = 1920
        
        // 1. Efficiently decode dimensions only
        val options = android.graphics.BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use { 
            android.graphics.BitmapFactory.decodeStream(it, null, options)
        }

        // 2. Calculate sample size for downsampling
        val width = options.outWidth
        val height = options.outHeight
        var sampleSize = 1
        if (width > maxDimension || height > maxDimension) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            while (halfWidth / sampleSize >= maxDimension || halfHeight / sampleSize >= maxDimension) {
                sampleSize *= 2
            }
        }

        // 3. Decode scaled bitmap
        val decodeOptions = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val originalBitmap = context.contentResolver.openInputStream(uri)?.use {
            android.graphics.BitmapFactory.decodeStream(it, null, decodeOptions)
        } ?: throw Exception("Failed to decode bitmap")

        // 4. Final precise scaling if still needed
        val finalScale = if (originalBitmap.width > maxDimension || originalBitmap.height > maxDimension) {
            val scaleW = maxDimension.toFloat() / originalBitmap.width
            val scaleH = maxDimension.toFloat() / originalBitmap.height
            kotlin.math.min(scaleW, scaleH)
        } else 1.0f
        
        val bitmap = if (finalScale < 1.0f) {
            android.graphics.Bitmap.createScaledBitmap(
                originalBitmap, 
                (originalBitmap.width * finalScale).toInt(), 
                (originalBitmap.height * finalScale).toInt(), 
                true
            )
        } else originalBitmap

        val baos = java.io.ByteArrayOutputStream()
        
        // 5. Use WebP for superior compression
        val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            android.graphics.Bitmap.CompressFormat.WEBP
        }
        
        bitmap.compress(format, 75, baos)
        val data = baos.toByteArray()
        
        val fileName = "${System.currentTimeMillis()}.webp"
        val ref = storage.reference.child("$folder/$fileName")
        ref.putBytes(data).await()
        val downloadUrl = ref.downloadUrl.await()
        
        if (originalBitmap != bitmap) originalBitmap.recycle()
        bitmap.recycle()
        
        Result.success(downloadUrl.toString())
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteImage(url: String): Result<Unit> = try {
        if (url.startsWith("https://firebasestorage.googleapis.com")) {
            val ref = storage.getReferenceFromUrl(url)
            ref.delete().await()
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    private val _homeConfig = MutableStateFlow(HomeConfig())
    val homeConfig: StateFlow<HomeConfig> = _homeConfig

    private val _aboutConfig = MutableStateFlow(AboutConfig())
    val aboutConfig: StateFlow<AboutConfig> = _aboutConfig

    private val _peethadhipatiConfig = MutableStateFlow(PeethadhipatiConfig())
    val peethadhipatiConfig: StateFlow<PeethadhipatiConfig> = _peethadhipatiConfig

    private val _contactConfig = MutableStateFlow(ContactConfig())
    val contactConfig: StateFlow<ContactConfig> = _contactConfig

    private val _volunteerPrograms = MutableStateFlow<List<ProgramConfig>>(emptyList())
    val volunteerPrograms: StateFlow<List<ProgramConfig>> = _volunteerPrograms

    private val _membershipConfig = MutableStateFlow(ProgramConfig(title = "Membership Program"))
    val membershipConfig: StateFlow<ProgramConfig> = _membershipConfig

    init {
        // Enable offline persistence for faster perceived speed
        try {
            val settings = com.google.firebase.firestore.firestoreSettings {
                isPersistenceEnabled = true
                cacheSizeBytes = com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
            }
            db.firestoreSettings = settings
        } catch (e: Exception) {
            Log.e(TAG, "Firestore settings already initialized or error: ${e.message}")
        }

        auth.addAuthStateListener {
            _currentUser.value = it.currentUser
        }
        db.collection("config").document("home").addSnapshotListener { snapshot, e ->
            if (e != null) {
                updateStatus("config_home", "Error: ${e.message}")
                return@addSnapshotListener
            }
            snapshot?.toObject(HomeConfig::class.java)?.let { 
                _homeConfig.value = it 
                updateStatus("config_home", "Connected")
            }
        }

        db.collection("config").document("about").addSnapshotListener { snapshot, e ->
            if (e != null) {
                updateStatus("config_about", "Error: ${e.message}")
                return@addSnapshotListener
            }
            snapshot?.toObject(AboutConfig::class.java)?.let {
                _aboutConfig.value = it
                updateStatus("config_about", "Connected")
            }
        }

        db.collection("config").document("peethadhipati").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            snapshot?.toObject(PeethadhipatiConfig::class.java)?.let {
                _peethadhipatiConfig.value = it
            }
        }

        db.collection("config").document("contact").addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener
            snapshot?.toObject(ContactConfig::class.java)?.let {
                _contactConfig.value = it
            }
        }

        db.collection("volunteer_programs").addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(ProgramConfig::class.java)?.apply { id = doc.id }
            } ?: emptyList()
            _volunteerPrograms.value = items
        }

        db.collection("config").document("membership").addSnapshotListener { snapshot, _ ->
            snapshot?.toObject(ProgramConfig::class.java)?.let { _membershipConfig.value = it }
        }
    }

    private fun updateStatus(collection: String, status: String) {
        val current = collectionStatuses.value.toMutableMap()
        current[collection] = status
        collectionStatuses.value = current
    }

    private fun <T : Any> observeCollection(collectionName: String, clazz: Class<T>) = callbackFlow<List<T>> {
        val listener = db.collection(collectionName).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed for $collectionName: ${error.message}")
                updateStatus(collectionName, "Sync Issue: $collectionName")
                trySend(emptyList())
            } else {
                val items = mutableListOf<T>()
                snapshot?.documents?.forEach { doc ->
                    try {
                        doc.toObject(clazz)?.let { item ->
                            try {
                                val idField = item.javaClass.getDeclaredField("id")
                                idField.isAccessible = true
                                idField.set(item, doc.id)
                            } catch (e: Exception) { /* No id field */ }
                            items.add(item)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deserializing document in $collectionName: ${e.message}")
                    }
                }
                updateStatus(collectionName, "Connected")
                trySend(items)
            }
        }
        awaitClose { listener.remove() }
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun <T : Any> observeCollectionShared(collectionName: String, clazz: Class<T>) = 
        observeCollection(collectionName, clazz).stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val announcements = observeCollectionShared("announcements", Announcement::class.java)
    val events = observeCollection("events", Event::class.java).map { list ->
        if (BuildConfig.FLAVOR == "admin") {
            list
        } else {
            val now = System.currentTimeMillis()
            list.filter { it.endDate == 0L || it.endDate > now }
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val shlokas = observeCollectionShared("shlokas", Shloka::class.java)
    val branches = observeCollectionShared("branches", Branch::class.java)
    val socialLinks = observeCollectionShared("social_links", SocialLink::class.java)
    val sevas = observeCollectionShared("sevas", Seva::class.java)
    val userRegistrations = observeCollectionShared("registrations", Registration::class.java)
    val panchanga = observeCollectionShared("panchanga", Panchang::class.java)
    val gurus = observeCollectionShared("gurus", Guru::class.java)
    val news = observeCollectionShared("news", News::class.java)
    val photoAlbums = observeCollectionShared("photo_albums", PhotoAlbum::class.java)
    val videos = observeCollectionShared("videos", VideoItem::class.java)
    val festivals = observeCollectionShared("festivals", Festival::class.java)
    val specialEvents = observeCollectionShared("special_events", SpecialEvent::class.java)
    val publications = observeCollectionShared("publications", Publication::class.java)
    val sevaRecords = if (BuildConfig.FLAVOR == "admin") observeCollectionShared("seva_records", SevaRecord::class.java) else MutableStateFlow(emptyList())
    val appNotifications = observeCollection("notifications", AppNotification::class.java)
        .map { it.sortedByDescending { n -> n.timestamp } }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun saveNotification(n: AppNotification): Result<Unit> = try {
        val docRef = if (n.id.isEmpty()) db.collection("notifications").document() else db.collection("notifications").document(n.id)
        docRef.set(n.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun pushNotification(n: AppNotification): Result<Unit> = saveNotification(n)

    suspend fun deleteNotification(id: String) = try {
        db.collection("notifications").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveSevaRecord(record: SevaRecord): Result<Unit> = try {
        db.collection("seva_records").add(record).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteSevaRecord(id: String) = try {
        db.collection("seva_records").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateHomeConfig(config: HomeConfig): Result<Unit> = try {
        db.collection("config").document("home").set(config).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateAboutConfig(config: AboutConfig): Result<Unit> = try {
        db.collection("config").document("about").set(config).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updatePeethadhipatiConfig(config: PeethadhipatiConfig): Result<Unit> = try {
        db.collection("config").document("peethadhipati").set(config).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateContactConfig(config: ContactConfig): Result<Unit> = try {
        db.collection("config").document("contact").set(config).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveVolunteerProgram(config: ProgramConfig): Result<Unit> = try {
        val docRef = if (config.id.isEmpty()) db.collection("volunteer_programs").document() else db.collection("volunteer_programs").document(config.id)
        docRef.set(config.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteVolunteerProgram(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("volunteer_programs").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateMembershipConfig(config: ProgramConfig): Result<Unit> = try {
        db.collection("config").document("membership").set(config).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun savePublication(p: Publication): Result<Unit> = try {
        val docRef = if (p.id.isEmpty()) db.collection("publications").document() else db.collection("publications").document(p.id)
        docRef.set(p.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deletePublication(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("publications").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveGuru(g: Guru): Result<Unit> = try {
        val docRef = if (g.id.isEmpty()) db.collection("gurus").document() else db.collection("gurus").document(g.id)
        docRef.set(g.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteGuru(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("gurus").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveNews(n: News): Result<Unit> = try {
        val docRef = if (n.id.isEmpty()) db.collection("news").document() else db.collection("news").document(n.id)
        docRef.set(n.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteNews(id: String) = try { db.collection("news").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun savePhotoAlbum(p: PhotoAlbum): Result<Unit> = try {
        val docRef = if (p.id.isEmpty()) db.collection("photo_albums").document() else db.collection("photo_albums").document(p.id)
        docRef.set(p.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deletePhotoAlbum(id: String, urls: List<String> = emptyList()) = try {
        urls.forEach { deleteImage(it) }
        db.collection("photo_albums").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveVideo(v: VideoItem): Result<Unit> = try {
        val docRef = if (v.id.isEmpty()) db.collection("videos").document() else db.collection("videos").document(v.id)
        docRef.set(v.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteVideo(id: String) = try { db.collection("videos").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun saveFestival(f: Festival): Result<Unit> = try {
        val docRef = if (f.id.isEmpty()) db.collection("festivals").document() else db.collection("festivals").document(f.id)
        docRef.set(f.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteFestival(id: String) = try { db.collection("festivals").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun saveSpecialEvent(s: SpecialEvent): Result<Unit> = try {
        val docRef = if (s.id.isEmpty()) db.collection("special_events").document() else db.collection("special_events").document(s.id)
        docRef.set(s.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteSpecialEvent(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("special_events").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveAnnouncement(a: Announcement): Result<Unit> = try {
        val docRef = if (a.id.isEmpty()) db.collection("announcements").document() else db.collection("announcements").document(a.id)
        docRef.set(a.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteAnnouncement(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("announcements").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveEvent(e: Event): Result<Unit> = try {
        val docRef = if (e.id.isEmpty()) db.collection("events").document() else db.collection("events").document(e.id)
        docRef.set(e.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteEvent(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("events").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun cleanupExpiredEvents() = try {
        val now = System.currentTimeMillis()
        val snapshot = db.collection("events").get().await()
        snapshot.documents.forEach { doc ->
            val endDate = doc.getLong("endDate") ?: 0L
            if (endDate != 0L && endDate < now) {
                doc.reference.delete().await()
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveSeva(s: Seva): Result<Unit> = try {
        val docRef = if (s.id.isEmpty()) db.collection("sevas").document() else db.collection("sevas").document(s.id)
        docRef.set(s.copy(id = docRef.id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteSeva(id: String, imageUrl: String = "", qrUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        if (qrUrl.isNotEmpty()) deleteImage(qrUrl)
        db.collection("sevas").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun saveShloka(s: Shloka) = try {
        val docRef = if (s.id.isEmpty()) db.collection("shlokas").document() else db.collection("shlokas").document(s.id)
        docRef.set(s.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteShloka(id: String) = try { db.collection("shlokas").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun saveBranch(b: Branch) = try {
        val docRef = if (b.id.isEmpty()) db.collection("branches").document() else db.collection("branches").document(b.id)
        docRef.set(b.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteBranch(id: String) = try { db.collection("branches").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun saveSocialLink(s: SocialLink) = try {
        val docRef = if (s.id.isEmpty()) db.collection("social_links").document() else db.collection("social_links").document(s.id)
        docRef.set(s.copy(id = docRef.id)).await(); Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteSocialLink(id: String, imageUrl: String = "") = try {
        if (imageUrl.isNotEmpty()) deleteImage(imageUrl)
        db.collection("social_links").document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun savePanchang(p: Panchang) = try {
        val docRef = if (p.id.isEmpty()) db.collection("panchanga").document() else db.collection("panchanga").document(p.id)
        docRef.set(p.copy(id = docRef.id)).await()
        cleanupOldPanchanga()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    fun calculateLocalPanchanga(timestamp: Long): Panchang {
        val res = PanchangaEngine.calculate(timestamp)
        return Panchang(
            date = timestamp,
            samvatsara = res.samvatsara,
            ayana = res.ayana,
            ritu = res.ritu,
            masa = res.masa,
            paksha = res.paksha,
            tithi = res.tithi,
            tithiStart = res.tithiStart,
            tithiEnd = res.tithiEnd,
            nextTithi = res.nextTithi,
            nakshatra = res.nakshatra,
            nakshatraStart = res.nakshatraStart,
            nakshatraEnd = res.nakshatraEnd,
            nextNakshatra = res.nextNakshatra,
            yoga = res.yoga,
            karana = res.karana,
            souraMasa = res.souraMasa,
            rahuKala = res.rahuKala,
            yamagandaKala = res.yamaganda,
            suryodaya = res.suryodaya,
            suryasta = res.suryasta,
            specialNote = res.note
        )
    }

    suspend fun cleanupOldPanchanga() = try {
        val fourDaysAgo = System.currentTimeMillis() - (4 * 24 * 60 * 60 * 1000L)
        val snapshot = db.collection("panchanga").whereLessThan("date", fourDaysAgo).get().await()
        snapshot.documents.forEach { it.reference.delete().await() }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deletePanchang(id: String) = try { db.collection("panchanga").document(id).delete().await(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun registerForItem(itemId: String, itemTitle: String): Result<Unit> = try {
        val docRef = db.collection("registrations").document()
        val reg = Registration(id = docRef.id, itemId = itemId, itemTitle = itemTitle)
        docRef.set(reg).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun registerWithForm(itemId: String, itemTitle: String, phone: String, responses: Map<String, String>, utr: String = "", amount: String = "0"): Result<Unit> = try {
        val docRef = db.collection("registrations").document()
        val reg = Registration(
            id = docRef.id, 
            itemId = itemId, 
            itemTitle = itemTitle, 
            phoneNumber = phone, 
            formResponses = responses, 
            status = "Pending Verification", 
            utrNumber = utr,
            paidAmount = amount
        )
        docRef.set(reg).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateRegistrationStatus(id: String, newStatus: String): Result<Unit> = try {
        if (newStatus == "Verified" || newStatus == "Rejected" || newStatus == "Completed") {
            // Delete immediately after processing as per user request to "refresh"
            db.collection("registrations").document(id).delete().await()
        } else {
            db.collection("registrations").document(id).update("status", newStatus).await()
        }
        cleanupOldRegistrations()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun cleanupOldRegistrations() = try {
        val now = System.currentTimeMillis()
        val threeHoursAgo = now - (3 * 60 * 60 * 1000)
        val snapshot = db.collection("registrations").get().await()
        snapshot.documents.forEach { doc ->
            val timestamp = doc.getLong("timestamp") ?: 0L
            // Delete EVERYTHING older than 3 hours to refresh the logs
            if (timestamp < threeHoursAgo) {
                doc.reference.delete().await()
            }
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
