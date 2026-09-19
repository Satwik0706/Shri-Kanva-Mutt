package com.satwik.example.mutt_app.data

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object TranslationManager {
    private const val TAG = "TranslationManager"
    
    private val _isModelDownloaded = MutableStateFlow(false)
    val isModelDownloaded: StateFlow<Boolean> = _isModelDownloaded

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.KANNADA)
        .build()

    private var translatorClient: com.google.mlkit.nl.translate.Translator? = null

    @Synchronized
    private fun getClient(): com.google.mlkit.nl.translate.Translator {
        if (translatorClient == null) {
            translatorClient = Translation.getClient(options)
        }
        return translatorClient!!
    }

    suspend fun downloadModel() {
        try {
            val conditions = DownloadConditions.Builder().build()
            // We await the download in a background context to prevent any UI stutters
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                getClient().downloadModelIfNeeded(conditions).await()
            }
            _isModelDownloaded.value = true
            Log.d(TAG, "Kannada model downloaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading model: ${e.message}")
        }
    }

    private val localMap = mapOf(
        "Daily Wisdom & Shlokas" to "ನಿತ್ಯ ಜ್ಞಾನ ಮತ್ತು ಶ್ಲೋಕಗಳು",
        "Explore More Shlokas" to "ಹೆಚ್ಚಿನ ಶ್ಲೋಕಗಳನ್ನು ವೀಕ್ಷಿಸಿ",
        "Daily Updates" to "ದೈನಂದಿನ ಸುದ್ದಿ",
        "Festivals & Events" to "ಹಬ್ಬಗಳು ಮತ್ತು ಕಾರ್ಯಕ್ರಮಗಳು",
        "Featured Sevas" to "ಮುಖ್ಯ ಸೇವೆಗಳು",
        "Latest Announcements" to "ಇತ್ತೀಚಿನ ಪ್ರಕಟಣೆಗಳು",
        "Donate" to "ದಾನ",
        "Events" to "ಕಾರ್ಯಕ್ರಮಗಳು",
        "Gallery" to "ಚಿತ್ರಶಾಲೆ",
        "Live" to "ನೇರ ಪ್ರಸಾರ",
        "Shlokas" to "ಶ್ಲೋಕಗಳು",
        "Panchanga" to "ಪಂಚಾಂಗ",
        "Videos" to "ವೀಡಿಯೊಗಳು",
        "Contact" to "ಸಂಪರ್ಕ",
        "Books" to "ಪುಸ್ತಕಗಳು",
        "Register Now" to "ನೋಂದಾಯಿಸಿ",
        "View All Events" to "ಎಲ್ಲಾ ಕಾರ್ಯಕ್ರಮಗಳನ್ನು ನೋಡಿ",
        "Divine Shlokas" to "ದೈವಿಕ ಶ್ಲೋಕಗಳು",
        "About Us" to "ನಮ್ಮ ಬಗ್ಗೆ",
        "Connect With Us" to "ನಮ್ಮೊಂದಿಗೆ ಸಂಪರ್ಕ ಸಾಧಿಸಿ",
        "Upcoming Events" to "ಮುಂಬರುವ ಕಾರ್ಯಕ್ರಮಗಳು",
        "Sevas & Donations" to "ಸೇವೆಗಳು ಮತ್ತು ಕಾಣಿಕೆಗಳು",
        "Daily Panchanga" to "ದೈನಂದಿನ ಪಂಚಾಂಗ",
        "Our Branches" to "ನಮ್ಮ ಶಾಖೆಗಳು",
        "Our Publications" to "ನಮ್ಮ ಪ್ರಕಟಣೆಗಳು",
        "Guru Parampara (Lineage)" to "ಗುರು ಪರಂಪರೆ",
        "Detailed Lineage" to "ಸವಿಸ್ತಾರ ಪರಂಪರೆ",
        "Present Peethadhipati" to "ಪ್ರಸ್ತುತ ಪೀಠಾಧೀಶರು",
        "History & Vision" to "ಇತಿಹಾಸ ಮತ್ತು ದೃಷ್ಟಿಕೋನ",
        "Get in Touch" to "ಸಂಪರ್ಕಿಸಿ",
        "Send us a Message" to "ಸಂದೇಶ ಕಳುಹಿಸಿ",
        "Name" to "ಹೆಸರು",
        "Message" to "ಸಂದೇಶ",
        "Send Message" to "ಸಂದೇಶ ಕಳುಹಿಸಿ",
        "Photo Gallery" to "ಚಿತ್ರ ಗ್ಯಾಲರಿ",
        "Video Gallery" to "ವಿಡಿಯೋ ಗ್ಯಾಲರಿ",
        "Search" to "ಹುಡುಕಿ",
        "Back" to "ಹಿಂದಕ್ಕೆ",
        "Close" to "ಮುಚ್ಚಿ",
        "Submit" to "ಸಲ್ಲಿಸಿ",
        "Done" to "ಮುಗಿದಿದೆ",
        "Special Announcement" to "ವಿಶೇಷ ಪ್ರಕಟಣೆ",
        "Privacy Policy & Data Usage" to "ಗೌಪ್ಯತಾ ನೀತಿ ಮತ್ತು ಡೇಟಾ ಬಳಕೆ",
        "I Accept" to "ನಾನು ಒಪ್ಪುತ್ತೇನೆ",
        "View Full Policy" to "ಪೂರ್ಣ ನೀತಿಯನ್ನು ನೋಡಿ",
        "Vedic timings calculated with high precision" to "ಉನ್ನತ ನಿಖರತೆಯೊಂದಿಗೆ ವೈದಿಕ ಸಮಯದ ಲೆಕ್ಕಾಚಾರ",
        "Samvatsara" to "ಸಂವತ್ಸರ",
        "Ayana" to "ಅಯನ",
        "Ritu" to "ಋತು",
        "Masa" to "ಮಾಸ",
        "Paksha" to "ಪಕ್ಷ",
        "Soura Masa" to "ಸೌರ ಮಾಸ",
        "Sunrise" to "ಸೂರ್ಯೋದಯ",
        "Sunset" to "ಸೂರ್ಯಾಸ್ತ",
        "Tithi" to "ತಿಥಿ",
        "Nakshatra" to "ನಕ್ಷತ್ರ",
        "Yoga" to "ಯೋಗ",
        "Karana" to "ಕರಣ",
        "Rahu Kala" to "ರಾಹು ಕಾಲ",
        "Yamaganda" to "ಯಮಗಂಡ",
        "Special" to "ವಿಶೇಷ",
        "Share" to "ಹಂಚಿಕೊಳ್ಳಿ",
        "Next Tithi" to "ಮುಂದಿನ ತಿಥಿ",
        "Next Nakshatra" to "ಮುಂದಿನ ನಕ್ಷತ್ರ",
        "Biography" to "ಜೀವನ ಚರಿತ್ರೆ",
        "Timeline & Aradhana" to "ಕಾಲಗಣನೆ ಮತ್ತು ಆರಾಧನೆ",
        "Additional Resources" to "ಹೆಚ್ಚಿನ ಸಂಪನ್ಮೂಲಗಳು",
        "Watch Video" to "ವಿಡಿಯೋ ವೀಕ್ಷಿಸಿ",
        "View PDF" to "ಪಿಡಿಎಫ್ ವೀಕ್ಷಿಸಿ",
        "Event Gallery" to "ಕಾರ್ಯಕ್ರಮದ ಚಿತ್ರಗಳು",
        "No upcoming events at the moment." to "ಸದ್ಯಕ್ಕೆ ಯಾವುದೇ ಮುಂಬರುವ ಕಾರ್ಯಕ್ರಮಗಳಿಲ್ಲ.",
        "Coming Soon..." to "ಶೀಘ್ರದಲ್ಲೇ ಬರಲಿದೆ...",
        "Registration Successful" to "ನೋಂದಣಿ ಯಶಸ್ವಿಯಾಗಿದೆ",
        "Understood" to "ಅರ್ಥವಾಯಿತು",
        "Email Address *" to "ಇಮೇಲ್ ವಿಳಾಸ *",
        "WhatsApp/Phone Number *" to "ವಾಟ್ಸಾಪ್/ಫೋನ್ ಸಂಖ್ಯೆ *",
        "Complete Payment" to "ಪಾವತಿ ಪೂರ್ಣಗೊಳಿಸಿ",
        "UPI ID:" to "ಯುಪಿಐ ಐಡಿ:",
        "OFFICIAL APP" to "ಅಧಿಕೃತ ಆಪ್",
        "Shukla" to "ಶುಕ್ಲ",
        "Krishna" to "ಕೃಷ್ಣ",
        "Prathama" to "ಪ್ರಥಮ",
        "Dwitiya" to "ದ್ವಿತೀಯ",
        "Tritiya" to "ತೃತೀಯ",
        "Chaturthi" to "ಚತುರ್ಥಿ",
        "Panchami" to "ಪಂಚಮಿ",
        "Shashti" to "ಷಷ್ಠಿ",
        "Saptami" to "ಸಪ್ತಮಿ",
        "Ashtami" to "ಅಷ್ಟಮಿ",
        "Navami" to "ನವಮಿ",
        "Dashami" to "ದಶಮಿ",
        "Ekadashi" to "ಏಕಾದಶಿ",
        "Dwadashi" to "ದ್ವಾದಶಿ",
        "Trayodashi" to "ತ್ರಯೋದಶಿ",
        "Chaturdashi" to "ಚತುರ್ದಶಿ",
        "Purnima" to "ಪೂರ್ಣಿಮೆ",
        "Amavasya" to "ಅಮಾವಾಸ್ಯೆ",
        "Shukla Purnima" to "ಶುಕ್ಲ ಪೂರ್ಣಿಮೆ",
        "Krishna Amavasya" to "ಕೃಷ್ಣ ಅಮಾವಾಸ್ಯೆ",
        "Uttarayana" to "ಉತ್ತರಾಯಣ",
        "Dakshinayana" to "ದಕ್ಷಿಣಾಯಣ",
        "Vasanta" to "ವಸಂತ",
        "Grishma" to "ಗ್ರೀಷ್ಮ",
        "Varsha" to "ವರ್ಷ",
        "Sharad" to "ಶರತ್",
        "Hemanta" to "ಹೇಮಂತ",
        "Shishira" to "ಶಿಶಿರ",
        "Ashwini" to "ಅಶ್ವಿನಿ",
        "Bharani" to "ಭರಣಿ",
        "Krittika" to "ಕೃತ್ತಿಕಾ",
        "Rohini" to "ರೋಹಿಣಿ",
        "Mrigashirsha" to "ಮೃಗಶಿರ",
        "Ardra" to "ಆರಿದ್ರ",
        "Punarvasu" to "ಪುನರ್ವಸು",
        "Pushya" to "ಪುಷ್ಯ",
        "Ashlesha" to "ಆಶ್ಲೇಷ",
        "Magha" to "ಮಘಾ",
        "Purva Phalguni" to "ಪೂರ್ವ ಫಲ್ಗುಣಿ",
        "Uttara Phalguni" to "ಉತ್ತರ ಫಲ್ಗುಣಿ",
        "Hasta" to "ಹಸ್ತ",
        "Chitra" to "ಚಿತ್ರಾ",
        "Swati" to "ಸ್ವಾತಿ",
        "Vishakha" to "ವಿಶಾಖಾ",
        "Anuradha" to "ಅನುರಾಧಾ",
        "Jyeshtha" to "ಜ್ಯೇಷ್ಠಾ",
        "Mula" to "ಮೂಲಾ",
        "Purva Ashadha" to "ಪೂರ್ವಾಷಾಢ",
        "Uttara Ashadha" to "ಉತ್ತರಾಷಾಢ",
        "Shravana" to "ಶ್ರವಣ",
        "Dhanishta" to "ಧನಿಷ್ಠಾ",
        "Shatabhisha" to "ಶತಭಿಷಾ",
        "Purva Bhadrapada" to "ಪೂರ್ವಭಾದ್ರಪದ",
        "Uttara Bhadrapada" to "ಉತ್ತರಭಾದ್ರಪದ",
        "Revati" to "ರೇವತಿ"
    )

    suspend fun translate(text: String): String {
        if (text.isBlank()) return text
        
        val trimmedText = text.trim()
        Log.d(TAG, "Translating: '$trimmedText'")
        
        // 1. Check local manual map first for high quality
        localMap[trimmedText]?.let { 
            Log.d(TAG, "Map match found for: '$trimmedText' -> '$it'")
            return it 
        }
        
        // Use a simpler mapping for common variants
        localMap.entries.find { it.key.equals(trimmedText, ignoreCase = true) }?.value?.let { 
            Log.d(TAG, "Map match (ignoreCase) found for: '$trimmedText' -> '$it'")
            return it 
        }
        
        // 2. Fallback to ML Kit
        if (!_isModelDownloaded.value) {
            Log.d(TAG, "Model not downloaded yet, returning original")
            return text
        }

        return try {
            val result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                getClient().translate(trimmedText).await()
            }
            Log.d(TAG, "ML Kit result for '$trimmedText': '$result'")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Translation error for '$trimmedText': ${e.message}")
            text // Fallback to original
        }
    }

    fun close() {
        translatorClient?.close()
        translatorClient = null
    }
}
