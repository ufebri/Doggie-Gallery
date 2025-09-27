package com.raylabs.doggie.utils;

import android.content.res.Resources;

public class GeneralHelper {

    /**
     * @param px image value
     * @return int
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * @param dp image value
     * @return int
     */
    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
