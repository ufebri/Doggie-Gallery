package com.raylabs.doggie.ui.detail

import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object ImageActionHelper {

    private const val GALLERY_DIR_NAME = "DoggieGallery"
    private const val SHARE_CACHE_DIR = "shared_images"

    @WorkerThread
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
        var outputStream: OutputStream? = null
        return try {
            val fileName = "doggie_${System.currentTimeMillis()}.jpg"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = context.contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/" + GALLERY_DIR_NAME
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return false

                outputStream = resolver.openOutputStream(uri)
                if (outputStream == null || !bitmap.compress(
                        CompressFormat.JPEG,
                        90,
                        outputStream
                    )
                ) {
                    return false
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetDir = File(picturesDir, GALLERY_DIR_NAME)
                if (!targetDir.exists() && !targetDir.mkdirs()) {
                    return false
                }
                val imageFile = File(targetDir, fileName)
                outputStream = FileOutputStream(imageFile)
                if (!bitmap.compress(CompressFormat.JPEG, 90, outputStream)) {
                    return false
                }
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(imageFile.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )
            }
            true
        } catch (_: IOException) {
            false
        } finally {
            try {
                outputStream?.close()
            } catch (_: IOException) {
            }
        }
    }

    @WorkerThread
    fun createShareUri(context: Context, bitmap: Bitmap): Uri? {
        val cacheDir = File(context.cacheDir, SHARE_CACHE_DIR)
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            return null
        }

        val imageFile = File(cacheDir, "doggie_share_${System.currentTimeMillis()}.jpg")
        return try {
            FileOutputStream(imageFile).use { out ->
                if (!bitmap.compress(CompressFormat.JPEG, 90, out)) {
                    return null
                }
            }
            val authority = context.packageName + ".provider"
            FileProvider.getUriForFile(context, authority, imageFile)
        } catch (_: IOException) {
            null
        }
    }

    @WorkerThread
    fun setWallpaper(context: Context, bitmap: Bitmap): Boolean {
        return try {
            WallpaperManager.getInstance(context).setBitmap(bitmap)
            true
        } catch (_: IOException) {
            false
        }
    }
}
