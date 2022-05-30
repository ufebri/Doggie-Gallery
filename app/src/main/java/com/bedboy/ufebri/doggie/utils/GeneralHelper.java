package com.bedboy.ufebri.doggie.utils;

import android.content.res.Resources;

public class GeneralHelper {

    /**
     * @param px image value
     * @return int
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
