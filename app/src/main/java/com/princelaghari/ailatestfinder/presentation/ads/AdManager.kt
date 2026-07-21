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

    // Fallback real Production IDs if resources fail to fetch
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
     * Initializes the Google Mobile Ads SDK and dynamically loads the real Ad Unit IDs from strings.xml
     */
    fun initialize(context: Context) {
        val appContext = context.applicationContext

        // Programmatically bind and load real verified Ad Unit IDs directly from strings.xml
        try {
            val bId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_banner_id)
            if (bId.isNotEmpty()) {
                bannerId = bId
            }
            val iId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_interstitial_id)
            if (iId.isNotEmpty()) {
                interstitialId = iId
            }
            val rId = appContext.getString(com.princelaghari.ailatestfinder.R.string.admob_rewarded_id)
            if (rId.isNotEmpty()) {
                rewardedId = rId
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resolve production Ad Unit IDs from strings.xml", e)
        }

        Log.d(TAG, "AdManager: Configured Banner Ad Unit: $bannerId")
        Log.d(TAG, "AdManager: Configured Interstitial Ad Unit: $interstitialId")
        Log.d(TAG, "AdManager: Configured Rewarded Ad Unit: $rewardedId")

        if (isInitialized.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    MobileAds.initialize(context) {
                        Log.d(TAG, "AdMob SDK Initialized Successfully with real configurations.")
                        preloadInterstitial(context)
                        preloadRewarded(context)
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "AdMob SDK Initialization failed safely bypassed: ${e.localizedMessage}")
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

        try {
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
        } catch (e: Throwable) {
            isInterstitialLoading.set(false)
            Log.e(TAG, "Failed to load Interstitial Ad safely bypassed", e)
        }
    }

    /**
     * Preloads a Rewarded Ad with exponential backoff retry.
     */
    fun preloadRewarded(context: Context) {
        if (rewardedId.isEmpty()) return
        if (preloadedRewardedAd != null || isRewardedLoading.get()) return

        isRewardedLoading.set(true)
        val adRequest = AdRequest.Builder().build()

        try {
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
        } catch (e: Throwable) {
            isRewardedLoading.set(false)
            Log.e(TAG, "Failed to load Rewarded Ad safely bypassed", e)
        }
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
