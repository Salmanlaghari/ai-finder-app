# Jetpack Compose ProGuard rules
-keepclassmembers class * extends androidx.compose.runtime.snapshots.SnapshotState { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.ui.** { *; }

# Hilt and Dagger rules
-keep class dagger.hilt.** { *; }
-keep class com.princelaghari.ailatestfinder.di.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.ViewModel

# Firebase Firestore and common rules
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.princelaghari.ailatestfinder.domain.model.** { *; }
-keepclassmembers class com.princelaghari.ailatestfinder.domain.model.** {
    <fields>;
    <init>(...);
    *** get*();
    *** set*(...);
}

# Google Mobile Ads (AdMob) rules
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }

# General optimizations - KEEP Signature, InnerClasses, Annotations
-keepattributes Signature, InnerClasses, EnclosingMethod, AnnotationDefault, *Annotation*
-dontwarn okio.**
-dontwarn javax.annotation.**

# Safe Optimization and Shrinking Rules (NO repackaging or aggressive renaming)
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
-keep class * implements androidx.room.RoomDatabase
-keep class com.princelaghari.ailatestfinder.data.local.** { *; }
-keep class com.princelaghari.ailatestfinder.data.local.entity.** { *; }
-keep class com.princelaghari.ailatestfinder.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class **_Impl { *; }
-dontwarn androidx.room.paging.**

# Coil Image Loader Rules
-keep class coil.** { *; }
-dontwarn coil.**

# Kotlin Coroutines Rules
-keep class kotlinx.coroutines.** { *; }

# Keep Android Application and Activity classes referenced in Manifest
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver

# Keep R classes to prevent resource reflection crashes
-keep class **.R { *; }
-keep class **.R$* { *; }
