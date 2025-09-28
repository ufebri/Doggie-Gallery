package com.raylabs.doggie.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.raylabs.doggie.R

object AdsHelper {

    private const val TAG = "AdsHelper"
    private var initialized = false

    fun init(appContext: Context) {
        if (!initialized) {
            MobileAds.initialize(appContext.applicationContext) {
                Log.d(TAG, "MobileAds.initialize complete.")
            }
            initialized = true
        }
    }

    fun loadBanner(activity: Activity, adContainer: FrameLayout?) {
        if (adContainer == null) {
            Log.e(TAG, "Ad container is null.")
            return
        }

        adContainer.removeAllViews()

        val adView = AdView(activity).apply {
            adUnitId = activity.getString(R.string.admob_banner_id)
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
            }
        }

        adContainer.addView(adView)
        Log.d(TAG, "Requesting ad...")
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun getAdaptiveAdSize(activity: Activity): AdSize {
        val display: Display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val adWidthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}
