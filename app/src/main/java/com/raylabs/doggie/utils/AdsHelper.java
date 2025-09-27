package com.raylabs.doggie.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.raylabs.doggie.R;

public final class AdsHelper {

    private static boolean initialized;

    private AdsHelper() {
    }

    public static void init(@NonNull Context context) {
        if (!initialized) {
            MobileAds.initialize(context);
            initialized = true;
        }
    }

    private static AdSize getAdaptiveAdSize(@NonNull Context context) {
        // Step 1: Determine the screen width (less navigation bar width)
        Activity activity = (Activity) context;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (adWidthPixels / density);

        // Step 2: Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static void loadBanner(@Nullable FrameLayout adContainer) {
        if (adContainer == null) {
            return;
        }

        adContainer.removeAllViews();
        Context context = adContainer.getContext();

        if (!(context instanceof Activity)) {
            // Adaptive banners require an Activity context
            // Fallback or log an error
            // For now, let's skip loading if not an Activity context for safety
            return;
        }

        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.admob_banner_id));

        // Get adaptive ad size
        AdSize adaptiveSize = getAdaptiveAdSize(context);
        adView.setAdSize(adaptiveSize);

        adContainer.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
