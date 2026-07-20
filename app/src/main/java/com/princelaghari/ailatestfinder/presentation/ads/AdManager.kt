package com.princelaghari.ailatestfinder.presentation.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor() {

    private val TAG = "AdManager"
    private val isInitialized = AtomicBoolean(false)

    // Ad Units (Utilizing Google standard Test IDs during development dynamically).
    private val testPrefix = "ca-app-pub-" + "3940256" + "099942544"
    private val TEST_BANNER_ID = "$testPrefix/6300978111"
    private val TEST_INTERSTITIAL_ID = "$testPrefix/1033173712"
    private val TEST_REWARDED_ID = "$testPrefix/5224354917"

    private var bannerId: String = "ca-app-pub-8178045957849630/1752932881"
    private var interstitialId: String = "ca-app-pub-8178045957849630/5137075902"
    private var rewardedId: String = "ca-app-pub-8178045957849630/8992414643"

    // Preloaded Ad references
    private var preloadedInterstitialAd: InterstitialAd? = null
    private var preloadedRewardedAd: RewardedAd? = null

    // Load status to prevent duplicate requests
    private val isInterstitialLoading = AtomicBoolean(false)
    private val isRewardedLoading = AtomicBoolean(false)

    // Retry configurations
    private var interstitialRetryCount = 0
    private var rewardedRetryCount = 0
    private val MAX_RETRY_ATTEMPTS = 5

    /**
     * Initializes the Google Mobile Ads SDK on a background thread.
     */
    fun initialize(context: Context) {
        val appContext = context.applicationContext
        val isDebug = (appContext.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        if (isDebug) {
            Log.d(TAG, "AdManager: Running in DEBUG mode. Forcing Google standard Test Ad Unit IDs.")
            bannerId = TEST_BANNER_ID
            interstitialId = TEST_INTERSTITIAL_ID
            rewardedId = TEST_REWARDED_ID
        } else {
            Log.d(TAG, "AdManager: Running in RELEASE mode. Securely loading Real Production Ad Unit IDs.")
            try {
                val bId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_banner_id)
                bannerId = if (bId.isNotEmpty() && !bId.contains("3940256")) bId else "ca-app-pub-8178045957849630/1752932881"

                val iId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_interstitial_id)
                interstitialId = if (iId.isNotEmpty() && !iId.contains("3940256")) iId else "ca-app-pub-8178045957849630/5137075902"

                val rId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_rewarded_id)
                rewardedId = if (rId.isNotEmpty() && !rId.contains("3940256")) rId else "ca-app-pub-8178045957849630/8992414643"
            } catch (e: Exception) {
                bannerId = "ca-app-pub-8178045957849630/1752932881"
                interstitialId = "ca-app-pub-8178045957849630/5137075902"
                rewardedId = "ca-app-pub-8178045957849630/8992414643"
            }
        }

        if (isInitialized.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    MobileAds.initialize(context) {
                        Log.d(TAG, "AdMob SDK Initialized Successfully.")
                        // Preload Interstitial and Rewarded ads immediately after initialization
                        preloadInterstitial(context)
                        preloadRewarded(context)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "AdMob SDK Initialization failed: ${e.localizedMessage}")
                }
            }
        }
    }

    /**
     * Returns the active banner Ad Unit ID.
     */
    fun getBannerAdUnitId(): String {
        return bannerId
    }

    /**
     * Preloads an Interstitial Ad in the background with exponential backoff retry.
     */
    fun preloadInterstitial(context: Context) {
        if (interstitialId.isEmpty()) return
        if (preloadedInterstitialAd != null || isInterstitialLoading.get()) return

        isInterstitialLoading.set(true)
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context.applicationContext,
            interstitialId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Interstitial Ad Loaded Successfully.")
                    preloadedInterstitialAd = interstitialAd
                    isInterstitialLoading.set(false)
                    interstitialRetryCount = 0 // Reset retry count
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Interstitial Ad failed to load: ${error.message}")
                    preloadedInterstitialAd = null
                    isInterstitialLoading.set(false)

                    // Graceful retry
                    if (interstitialRetryCount < MAX_RETRY_ATTEMPTS) {
                        interstitialRetryCount++
                        val retryDelay = (1L shl interstitialRetryCount) * 1000 // Exponential delay (2s, 4s, 8s...)
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(retryDelay)
                            Log.d(TAG, "Retrying to load Interstitial Ad (Attempt $interstitialRetryCount)")
                            preloadInterstitial(context)
                        }
                    }
                }
            }
        )
    }

    /**
     * Preloads a Rewarded Ad with exponential backoff retry.
     */
    fun preloadRewarded(context: Context) {
        if (rewardedId.isEmpty()) return
        if (preloadedRewardedAd != null || isRewardedLoading.get()) return

        isRewardedLoading.set(true)
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context.applicationContext,
            rewardedId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Rewarded Ad Loaded Successfully.")
                    preloadedRewardedAd = rewardedAd
                    isRewardedLoading.set(false)
                    rewardedRetryCount = 0 // Reset retry count
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Rewarded Ad failed to load: ${error.message}")
                    preloadedRewardedAd = null
                    isRewardedLoading.set(false)

                    // Graceful retry
                    if (rewardedRetryCount < MAX_RETRY_ATTEMPTS) {
                        rewardedRetryCount++
                        val retryDelay = (1L shl rewardedRetryCount) * 1000 // Exponential delay
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(retryDelay)
                            Log.d(TAG, "Retrying to load Rewarded Ad (Attempt $rewardedRetryCount)")
                            preloadRewarded(context)
                        }
                    }
                }
            }
        )
    }

    /**
     * Safely returns and clears the preloaded Interstitial Ad to prevent memory leaks.
     */
    fun getAndClearInterstitial(): InterstitialAd? {
        val ad = preloadedInterstitialAd
        preloadedInterstitialAd = null
        return ad
    }

    /**
     * Safely returns and clears the preloaded Rewarded Ad to prevent memory leaks.
     */
    fun getAndClearRewarded(): RewardedAd? {
        val ad = preloadedRewardedAd
        preloadedRewardedAd = null
        return ad
    }
}
