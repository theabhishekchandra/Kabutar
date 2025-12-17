# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================== Hilt ====================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}

# ==================== Gson ====================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data model classes
-keep class com.abhishek.gomailai.core.model.** { *; }
-keep class com.abhishek.gomailai.core.local.entities.** { *; }

# ==================== JavaMail ====================
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn java.awt.**
-dontwarn javax.security.**
-dontwarn javax.activation.**

# ==================== Google Generative AI ====================
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# ==================== Kotlin Coroutines ====================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ==================== AndroidX ====================
-keep class androidx.lifecycle.** { *; }
-keep class androidx.work.** { *; }
-keep class androidx.navigation.** { *; }

# ==================== Encrypted SharedPreferences ====================
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# ==================== Data Binding ====================
-keep class androidx.databinding.** { *; }
-keepclassmembers class * extends androidx.databinding.ViewDataBinding {
    public static * inflate(...);
    public static * bind(...);
}

# ==================== ViewBinding ====================
-keep class * implements androidx.viewbinding.ViewBinding {
    public static * inflate(...);
    public static * bind(...);
}

# ==================== Prevent stripping of app classes ====================
-keep class com.abhishek.gomailai.** { *; }
