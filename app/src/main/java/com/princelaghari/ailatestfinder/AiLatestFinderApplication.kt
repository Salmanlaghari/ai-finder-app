package com.princelaghari.ailatestfinder

import android.app.Application
import com.google.android.gms.ads.MobileAds
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class AiLatestFinderApplication : Application(), ImageLoaderFactory {
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

    /**
     * Centralized, high-performance Coil Image Loader configuration.
     * Establishes memory caching (25% available app memory heap) and persistent disk caching (100MB max)
     * with standard crossfades to ensure instant scrolling, smooth UI loading, and zero duplicated image downloads.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(coil.decode.SvgDecoder.Factory())
            }
            .okHttpClient {
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Utilize 25% of app's memory heap size
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100 Megabytes max persistent size
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
