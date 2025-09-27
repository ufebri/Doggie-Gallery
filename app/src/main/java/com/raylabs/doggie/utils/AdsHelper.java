package com.raylabs.doggie.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.raylabs.doggie.R;

public final class AdsHelper {

    private static boolean initialized;
    private static final String TAG = "AdsHelper";

    private AdsHelper() {
    }

    public static void init(@NonNull Context appContext) {
        if (!initialized) {
            MobileAds.initialize(appContext.getApplicationContext(), initializationStatus -> {
                Log.d(TAG, "MobileAds.initialize complete.");
            });
            initialized = true;
        }
    }

    // getAdaptiveAdSize sekarang menggunakan Activity secara langsung
    private static AdSize getAdaptiveAdSize(@NonNull Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (adWidthPixels / density);

        // Step 2: Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    // loadBanner sekarang menerima Activity secara eksplisit
    public static void loadBanner(@NonNull Activity activity, @Nullable FrameLayout adContainer) {
        if (adContainer == null) {
            Log.e(TAG, "Ad container is null.");
            return;
        }

        adContainer.removeAllViews();
        // Tidak ada lagi pengecekan context instanceof Activity karena kita sudah menerima Activity

        AdView adView = new AdView(activity); // Gunakan Activity di sini

        // Menggunakan Test Ad Unit ID untuk sementara
        String TEST_AD_UNIT_ID = activity.getString(R.string.admob_banner_id);
        Log.d(TAG, "Using Test Ad Unit ID: " + TEST_AD_UNIT_ID);
        adView.setAdUnitId(TEST_AD_UNIT_ID);

        // Dapatkan adaptive ad size menggunakan Activity
        AdSize adaptiveSize = getAdaptiveAdSize(activity);
        adView.setAdSize(adaptiveSize);

        adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "Ad loaded successfully.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "Ad failed to load: " + loadAdError.getMessage());
                Log.e(TAG, "Error code: " + loadAdError.getCode());
                Log.e(TAG, "Error domain: " + loadAdError.getDomain());
            }
        });

        Log.d(TAG, "Requesting ad...");
        adView.loadAd(adRequest);
    }
}
