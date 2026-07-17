package com.princelaghari.ailatestfinder

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AiLatestFinderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob SDK in the background
        Thread {
            MobileAds.initialize(this) {}
        }.start()
    }
}
