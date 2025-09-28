package com.raylabs.doggie.utils.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.OutputStream

class ImageSaverTest {

    @Test
    fun `returns false when bitmap is null`() {
        val saver = ImageSaver(strategy = StorageStrategy.MEDIA_STORE)
        val ctx = mock<Context>()
        assertFalse(saver.saveToGallery(ctx, null))
    }

    @Test
    fun `MEDIA_STORE path succeeds when writer and compressor succeed`() {
        val ctx = mock<Context> {
            on { contentResolver } doReturn mock<ContentResolver>()
        }
        val bmp = mock<Bitmap>()
        val time = TimeProvider { 123L }
        val compressor = mock<BitmapCompressor> {
            on { compressJpeg(eq(bmp), eq(90), any()) } doReturn true
        }
        val writer = mock<GalleryWriter> {
            on {
                writeQPlus(any(), eq("doggie_123.jpg"), eq("image/jpeg"), any(), any())
            } doAnswer { (it.arguments[4] as (OutputStream) -> Boolean).invoke(mock()); true }
        }

        val saver = ImageSaver(time, compressor, writer, StorageStrategy.MEDIA_STORE)
        assertTrue(saver.saveToGallery(ctx, bmp))
        verify(writer).writeQPlus(any(), eq("doggie_123.jpg"), eq("image/jpeg"), any(), any())
    }

    @Test
    fun `LEGACY path fails when writer fails`() {
        val ctx = mock<Context>()
        val bmp = mock<Bitmap>()
        val writer = mock<GalleryWriter> {
            on { writeLegacy(any(), any(), any()) } doReturn false
        }
        val saver = ImageSaver(writer = writer, strategy = StorageStrategy.LEGACY)
        assertFalse(saver.saveToGallery(ctx, bmp))
    }
}