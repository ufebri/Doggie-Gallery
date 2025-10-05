package com.raylabs.doggie.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.raylabs.doggie.BuildConfig
import com.raylabs.doggie.R

object AdsHelper {

    private const val TAG = "AdsHelper"
    private var initialized = false
    private var rewardedAd: RewardedAd? = null
    private var loadingRewarded = false

    fun init(appContext: Context) {
        if (!BuildConfig.ENABLE_ADS) return
        if (!initialized) {
            MobileAds.initialize(appContext.applicationContext) {
                Log.d(TAG, "MobileAds.initialize complete.")
            }
            initialized = true
        }
    }

    fun loadBanner(activity: Activity, adContainer: FrameLayout?) {
        if (!BuildConfig.ENABLE_ADS) {
            adContainer?.visibility = View.GONE
            return
        }
        if (adContainer == null) {
            Log.e(TAG, "Ad container is null.")
            return
        }

        adContainer.removeAllViews()

        val adUnitId = activity.getString(R.string.admob_banner_id)
        if (adUnitId.isBlank() || adUnitId.equals("null", ignoreCase = true)) {
            val message = "AdMob banner ID is missing for buildType=${BuildConfig.BUILD_TYPE}."
            Log.e(TAG, message)
            reportToCrashlytics(
                event = message,
                extras = mapOf(
                    "build_type" to BuildConfig.BUILD_TYPE,
                    "ad_unit_id" to adUnitId
                )
            )
            return
        }

        val adView = AdView(activity).apply {
            this.adUnitId = adUnitId
            setAdSize(getAdaptiveAdSize(activity))
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "Ad loaded successfully.")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e(TAG, "Ad failed to load: ${loadAdError.message}")
                Log.e(TAG, "Error code: ${loadAdError.code}")
                Log.e(TAG, "Error domain: ${loadAdError.domain}")
                reportToCrashlytics(
                    event = "Banner ad load failed",
                    loadAdError = loadAdError,
                    extras = mapOf(
                        "build_type" to BuildConfig.BUILD_TYPE,
                        "ad_unit_id" to adUnitId
                    )
                )
            }
        }

        adContainer.addView(adView)
        Log.d(TAG, "Requesting ad...")
        adView.loadAd(AdRequest.Builder().build())
    }

    fun preloadRewarded(context: Context) {
        if (!BuildConfig.ENABLE_ADS) return
        if (rewardedAd != null || loadingRewarded) return

        val adUnitId = context.getString(R.string.admob_reward_id)
        if (adUnitId.isBlank() || adUnitId.equals("null", ignoreCase = true)) {
            val message = "AdMob rewarded ID is missing for buildType=${BuildConfig.BUILD_TYPE}."
            Log.e(TAG, message)
            reportToCrashlytics(
                event = message,
                extras = mapOf(
                    "build_type" to BuildConfig.BUILD_TYPE,
                    "ad_unit_id" to adUnitId
                )
            )
            return
        }

        loadingRewarded = true
        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded")
                    rewardedAd = ad
                    loadingRewarded = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    reportToCrashlytics(
                        event = "Rewarded ad load failed",
                        loadAdError = loadAdError,
                        extras = mapOf(
                            "build_type" to BuildConfig.BUILD_TYPE,
                            "ad_unit_id" to adUnitId
                        )
                    )
                    loadingRewarded = false
                }
            }
        )
    }

    fun showRewarded(activity: Activity, onClosed: (() -> Unit)? = null) {
        if (!BuildConfig.ENABLE_ADS) {
            onClosed?.invoke()
            return
        }
        val ad = rewardedAd
        if (ad == null) {
            preloadRewarded(activity.applicationContext)
            onClosed?.invoke()
            return
        }

        rewardedAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed")
                preloadRewarded(activity.applicationContext)
                onClosed?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                reportToCrashlytics(
                    "Rewarded show failed",
                    extras = mapOf("error" to adError.message)
                )
                preloadRewarded(activity.applicationContext)
                onClosed?.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad shown")
            }
        }

        ad.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
        }
    }

    private fun getAdaptiveAdSize(activity: Activity): AdSize {
        val density = activity.resources.displayMetrics.density
        val adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            windowMetrics.bounds.width().coerceAtLeast(0)
        } else {
            activity.resources.displayMetrics.widthPixels
        }

        val adWidth = (adWidthPixels / density).toInt().coerceAtLeast(1)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    private fun reportToCrashlytics(
        event: String,
        loadAdError: LoadAdError? = null,
        extras: Map<String, String?> = emptyMap()
    ) {

        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("AdsHelper: $event")

            extras.forEach { (key, value) ->
                crashlytics.setCustomKey(key, value ?: "null")
            }

            loadAdError?.let { error ->
                crashlytics.setCustomKey("ad_error_code", error.code)
                crashlytics.setCustomKey("ad_error_domain", error.domain)
                crashlytics.setCustomKey("ad_error_message", error.message)
                error.responseInfo?.let { responseInfo ->
                    crashlytics.setCustomKey(
                        "ad_response_id",
                        responseInfo.responseId ?: ""
                    )
                    crashlytics.setCustomKey(
                        "ad_adapter_class",
                        responseInfo.mediationAdapterClassName ?: ""
                    )
                }
            }

            val throwableMessage = buildString {
                append(event)
                loadAdError?.let { error ->
                    append(" | code=${error.code}")
                    append(" domain=${error.domain}")
                    append(" message=${error.message}")
                }
            }

            crashlytics.recordException(RuntimeException(throwableMessage))
        }.onFailure { throwable ->
            Log.e(TAG, "Unable to report ad issue to Crashlytics", throwable)
        }
    }
}
