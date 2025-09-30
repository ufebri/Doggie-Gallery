package com.raylabs.doggie.utils.image

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class WallpaperHelperTest {

    @Test
    fun `returns true on success`() {
        val ctx = mock<Context>()
        val bmp = mock<Bitmap>()
        val wm = mock<WallpaperManager>()

        val stat: MockedStatic<WallpaperManager> = Mockito.mockStatic(WallpaperManager::class.java)
        stat.`when`<WallpaperManager> { WallpaperManager.getInstance(ctx) }.thenReturn(wm)
        try {
            assertTrue(WallpaperHelper.setWallpaper(ctx, bmp))
            verify(wm).setBitmap(bmp)
        } finally {
            stat.close()
        }
    }

    @Test
    fun `returns false on exception`() {
        val ctx = mock<Context>()
        val bmp = mock<Bitmap>()

        val stat: MockedStatic<WallpaperManager> = Mockito.mockStatic(WallpaperManager::class.java)
        stat.`when`<WallpaperManager> { WallpaperManager.getInstance(ctx) }
            .thenThrow(RuntimeException("boom"))
        try {
            assertFalse(WallpaperHelper.setWallpaper(ctx, bmp))
        } finally {
            stat.close()
        }
    }
}