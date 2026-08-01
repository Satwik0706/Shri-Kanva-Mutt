package com.satwik.example.mutt_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

data class Announcement(
    val id: Int, 
    var content: String, 
    var title: String = "",
    var imageUrl: String = "",
    var language: String = "English",
    var timestamp: Long = System.currentTimeMillis()
)

data class Event(
    val id: Int, 
    var title: String, 
    var description: String, 
    var imageUrl: String = "",
    var date: Long, 
    var expiryDate: Long,
    var location: String = "",
    var registrationStatus: String = "Open" // Open, Registered, Pending
)

data class Photo(
    val id: Int,
    var url: String,
    var caption: String,
    var category: String = "General"
)

data class Panchang(
    val id: Int,
    var date: String,
    var tithi: String,
    var nakshatra: String,
    var yoga: String,
    var karana: String
)

data class Shloka(
    val id: Int,
    var content: String,
    var deityImageUrl: String = "",
    var title: String = ""
)

data class AppFeature(
    val id: Int,
    var name: String,
    var description: String,
    var isEnabled: Boolean = true
)

data class Seva(
    val id: Int,
    var name: String,
    var description: String = "",
    var amount: Int,
    var imageUrl: String = ""
)

data class Branch(
    val id: Int,
    var name: String,
    var address: String,
    var contact: String,
    var email: String = "",
    var mapUrl: String = ""
)

data class Guru(
    val id: Int,
    var name: String,
    var period: String,
    var description: String,
    var photoUrl: String = ""
)

data class SocialLink(
    val id: Int,
    val platform: String,
    val handle: String,
    val audience: String,
    val url: String
)

data class Donation(
    val id: Int,
    val donorName: String,
    val amount: Int,
    val date: Long,
    val receiptId: String
)

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val date: Long
)

data class HomeConfig(
    val bannerTitle: String = "Welcome to Sri Kanva Matha",
    val bannerSubtitle: String = "Digital Home of Sri Kanva Matha",
    val bannerImageUrl: String = "",
    val dailyWisdom: String = "\"Satyam Eva Jayate\" - Truth Alone Triumphs."
)

object MuttRepository {
    private val _homeConfig = MutableStateFlow(HomeConfig())
    val homeConfig: StateFlow<HomeConfig> = _homeConfig

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos

    private val _panchang = MutableStateFlow<List<Panchang>>(emptyList())
    val panchang: StateFlow<List<Panchang>> = _panchang

    private val _shlokas = MutableStateFlow<List<Shloka>>(emptyList())
    val shlokas: StateFlow<List<Shloka>> = _shlokas

    private val _features = MutableStateFlow<List<AppFeature>>(emptyList())
    val features: StateFlow<List<AppFeature>> = _features

    private val _sevas = MutableStateFlow<List<Seva>>(emptyList())
    val sevas: StateFlow<List<Seva>> = _sevas

    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches

    private val _gurus = MutableStateFlow<List<Guru>>(emptyList())
    val gurus: StateFlow<List<Guru>> = _gurus

    private val _socialLinks = MutableStateFlow<List<SocialLink>>(emptyList())
    val socialLinks: StateFlow<List<SocialLink>> = _socialLinks

    private val _donations = MutableStateFlow<List<Donation>>(emptyList())
    val donations: StateFlow<List<Donation>> = _donations

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun updateHomeConfig(config: HomeConfig) { _homeConfig.value = config }

    fun <T> saveItem(flow: MutableStateFlow<List<T>>, item: T, getId: (T) -> Int, setId: (T, Int) -> T) {
        val current = flow.value.toMutableList()
        val id = getId(item)
        val index = current.indexOfFirst { getId(it) == id }
        if (index != -1 && id != 0) {
            current[index] = item
        } else {
            val newId = (current.maxOfOrNull { getId(it) } ?: 0) + 1
            current.add(setId(item, newId))
        }
        flow.value = current
    }

    fun <T> deleteItem(flow: MutableStateFlow<List<T>>, id: Int, getId: (T) -> Int) {
        flow.value = flow.value.filter { getId(it) != id }
    }

    fun saveAnnouncement(a: Announcement) = saveItem(_announcements, a, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteAnnouncement(id: Int) = deleteItem(_announcements, id, { it.id })

    fun saveEvent(e: Event) = saveItem(_events, e, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteEvent(id: Int) = deleteItem(_events, id, { it.id })

    fun saveSeva(s: Seva) = saveItem(_sevas, s, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteSeva(id: Int) = deleteItem(_sevas, id, { it.id })

    fun saveShloka(s: Shloka) = saveItem(_shlokas, s, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteShloka(id: Int) = deleteItem(_shlokas, id, { it.id })

    fun saveBranch(b: Branch) = saveItem(_branches, b, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteBranch(id: Int) = deleteItem(_branches, id, { it.id })

    fun saveSocialLink(s: SocialLink) = saveItem(_socialLinks, s, { it.id }, { item, id -> item.copy(id = id) })
    fun deleteSocialLink(id: Int) = deleteItem(_socialLinks, id, { it.id })

    fun sendNotification(n: Notification) = saveItem(_notifications, n, { it.id }, { item, id -> item.copy(id = id) })
}
