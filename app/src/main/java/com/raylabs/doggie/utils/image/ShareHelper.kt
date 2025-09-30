package com.raylabs.doggie.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Adapter supaya FileProvider bisa di-mock di unit test */
fun interface FileProviderAdapter {
    fun getUri(context: Context, authority: String, file: File): Uri
}

object DefaultFileProviderAdapter : FileProviderAdapter {
    override fun getUri(context: Context, authority: String, file: File): Uri =
        FileProvider.getUriForFile(context, authority, file)
}

class ShareHelper(
    private val time: TimeProvider = SystemTimeProvider,
    private val compressor: BitmapCompressor = DefaultBitmapCompressor,
    private val fileProvider: FileProviderAdapter = DefaultFileProviderAdapter,
    private val quality: Int = 90
) {
    fun createShareUri(context: Context, bitmap: Bitmap): Uri? {
        val cacheDir = File(context.cacheDir, "shared_images")
        if (!cacheDir.exists() && !cacheDir.mkdirs()) return null

        val file = File(cacheDir, "doggie_share_${time.now()}.jpg")
        FileOutputStream(file).use { os ->
            if (!compressor.compressJpeg(bitmap, quality, os)) return null
        }

        val authority = context.packageName + ".provider"
        return fileProvider.getUri(context, authority, file)
    }
}