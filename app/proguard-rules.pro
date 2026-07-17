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
