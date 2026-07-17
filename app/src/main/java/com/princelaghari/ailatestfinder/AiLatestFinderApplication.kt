package com.princelaghari.ailatestfinder

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class AiLatestFinderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Asynchronously initialize Google AdMob SDK on a background thread (Dispatchers.IO)
        // to prevent any block on the main UI thread during instant application startup.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MobileAds.initialize(this@AiLatestFinderApplication) {}
            } catch (e: Exception) {
                // Safe ignore if compilation sandbox environment lacks Google Play Services
            }
        }
    }
}
