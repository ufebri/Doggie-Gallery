package com.raylabs.doggie.utils.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardHelper {
    fun copyText(context: Context, label: String, text: String): Boolean {
        val cm =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return false
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
        return true
    }
}