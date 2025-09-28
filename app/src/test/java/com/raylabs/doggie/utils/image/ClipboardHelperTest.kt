package com.raylabs.doggie.utils.image

import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ClipboardHelperTest {

    @Test
    fun `returns false when service missing`() {
        val ctx = mock<Context> {
            on { getSystemService(Context.CLIPBOARD_SERVICE) } doReturn null
        }
        assertFalse(ClipboardHelper.copyText(ctx, "lbl", "txt"))
    }
}