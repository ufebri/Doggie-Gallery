package com.raylabs.doggie.utils.image

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/** Abstraksi waktu untuk tes */
fun interface TimeProvider {
    fun now(): Long
}

object SystemTimeProvider : TimeProvider {
    override fun now() = System.currentTimeMillis()
}

/** Abstraksi kompresi untuk tes */
fun interface BitmapCompressor {
    fun compressJpeg(bitmap: Bitmap, quality: Int, os: OutputStream): Boolean
}

object DefaultBitmapCompressor : BitmapCompressor {
    override fun compressJpeg(bitmap: Bitmap, quality: Int, os: OutputStream) =
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os)
}

/** Abstraksi I/O galeri supaya gampang di-mock di unit test */
interface GalleryWriter {
    fun writeQPlus(
        resolver: ContentResolver,
        displayName: String,
        mime: String,
        relativePath: String,
        writer: (OutputStream) -> Boolean
    ): Boolean

    fun writeLegacy(
        targetDir: File,
        fileName: String,
        writer: (OutputStream) -> Boolean
    ): Boolean
}

object DefaultGalleryWriter : GalleryWriter {
    override fun writeQPlus(
        resolver: ContentResolver,
        displayName: String,
        mime: String,
        relativePath: String,
        writer: (OutputStream) -> Boolean
    ): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, mime)
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return false

        resolver.openOutputStream(uri).use { os ->
            if (os == null || !writer(os)) return false
        }

        val publish = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
        resolver.update(uri, publish, null, null)
        return true
    }

    override fun writeLegacy(
        targetDir: File,
        fileName: String,
        writer: (OutputStream) -> Boolean
    ): Boolean {
        if (!targetDir.exists() && !targetDir.mkdirs()) return false
        val out = File(targetDir, fileName)
        FileOutputStream(out).use { os -> if (!writer(os)) return false }
        // MediaScannerConnection optional – bisa ditangani di caller kalau perlu
        return true
    }
}

/** Strategi storage agar test tidak tergantung Build.VERSION */
enum class StorageStrategy { MEDIA_STORE, LEGACY, AUTO }

/** ImageSaver yang bisa di-unit test */
class ImageSaver(
    private val time: TimeProvider = SystemTimeProvider,
    private val compressor: BitmapCompressor = DefaultBitmapCompressor,
    private val writer: GalleryWriter = DefaultGalleryWriter,
    private val strategy: StorageStrategy = StorageStrategy.AUTO,
    private val relativeDir: String = Environment.DIRECTORY_PICTURES + "/DoggieGallery",
    private val quality: Int = 90
) {
    fun saveToGallery(context: Context, bitmap: Bitmap?): Boolean {
        if (bitmap == null) return false
        val fileName = "doggie_${time.now()}.jpg"
        val mime = "image/jpeg"

        val use = when (strategy) {
            StorageStrategy.AUTO ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) StorageStrategy.MEDIA_STORE else StorageStrategy.LEGACY

            else -> strategy
        }

        return when (use) {
            StorageStrategy.MEDIA_STORE ->
                writer.writeQPlus(
                    context.contentResolver,
                    displayName = fileName,
                    mime = mime,
                    relativePath = relativeDir
                ) { os -> compressor.compressJpeg(bitmap, quality, os) }

            StorageStrategy.LEGACY -> {
                val pictures =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val target = File(pictures, "DoggieGallery")
                writer.writeLegacy(target, fileName) { os ->
                    compressor.compressJpeg(
                        bitmap,
                        quality,
                        os
                    )
                }
            }

            else -> false
        }
    }
}