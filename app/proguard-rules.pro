# Jetpack Compose ProGuard rules
-keepclassmembers class * extends androidx.compose.runtime.snapshots.SnapshotState { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.ui.** { *; }

# Hilt and Dagger rules
-keep class dagger.hilt.** { *; }
-keep class com.princelaghari.ailatestfinder.di.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Firebase Firestore rules
-keep class com.google.firebase.firestore.** { *; }
-keep class com.princelaghari.ailatestfinder.domain.model.** { *; }

# Google Mobile Ads (AdMob) rules
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }

# General optimizations
-keepattributes Signature, InnerClasses, EnclosingMethod, AnnotationDefault, *Annotation*
-dontwarn okio.**
-dontwarn javax.annotation.**

# Aggressive Optimization and Shrinking Rules
-repackageclasses ''
-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Strip Logging / Debug Output in Production
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# Room Database Rules
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Dao
-keep class com.princelaghari.ailatestfinder.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**

# Coil Image Loader Rules
-keep class coil.** { *; }
-dontwarn coil.**

# Kotlin Coroutines Rules
-keep class kotlinx.coroutines.** { *; }
