# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Satwik_G.J\AppData\Local\Android\Sdk\tools\proguard\proguard-android-optimize.txt

# Models used for Firestore (to avoid obfuscating field names)
-keepclassmembers class com.satwik.example.mutt_app.data.** { *; }
-keep class com.satwik.example.mutt_app.data.** { *; }

# Prevent obfuscation of BuildConfig to keep flavor checks working
-keep class com.satwik.example.mutt_app.BuildConfig { *; }
