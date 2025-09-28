package com.raylabs.doggie.utils.image

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

object WallpaperHelper {
    fun setWallpaper(context: Context, bitmap: Bitmap): Boolean =
        try {
            WallpaperManager.getInstance(context).setBitmap(bitmap)
            true
        } catch (t: Throwable) {
            false
        }
}