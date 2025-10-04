package com.raylabs.doggie.utils.tab

/** Controller abstraction so TabDividerDelegate can work with different tab layouts. */
interface DividerController {
    /** return true kalau underlying view group adalah LinearLayout (atau setara) */
    fun isLinearLayout(): Boolean

    /** hilangkan divider (dipanggil hanya kalau isLinearLayout() == true) */
    fun removeDividers()
}
