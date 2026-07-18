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

    // Ad Units (Currently utilizing Google standard Test IDs as requested.
    // Real production IDs will replace these once provided by the user).
    private val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

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
        return TEST_BANNER_ID
    }

    /**
     * Preloads an Interstitial Ad in the background with exponential backoff retry.
     */
    fun preloadInterstitial(context: Context) {
        if (preloadedInterstitialAd != null || isInterstitialLoading.get()) return

        isInterstitialLoading.set(true)
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context.applicationContext,
            TEST_INTERSTITIAL_ID,
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
        if (preloadedRewardedAd != null || isRewardedLoading.get()) return

        isRewardedLoading.set(true)
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context.applicationContext,
            TEST_REWARDED_ID,
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
