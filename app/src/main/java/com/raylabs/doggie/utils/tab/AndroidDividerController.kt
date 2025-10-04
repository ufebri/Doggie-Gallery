package com.raylabs.doggie.utils.tab

import android.view.ViewGroup
import android.widget.LinearLayout

class AndroidDividerController(private val strip: ViewGroup) : DividerController {
    override fun isLinearLayout(): Boolean = strip is LinearLayout

    override fun removeDividers() {
        if (strip is LinearLayout) {
            strip.showDividers = LinearLayout.SHOW_DIVIDER_NONE
        }
    }
}
