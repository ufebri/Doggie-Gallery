package com.raylabs.doggie.utils.tab;

public interface DividerController {
    /** return true kalau underlying view group adalah LinearLayout (atau setara) */
    boolean isLinearLayout();

    /** hilangkan divider (dipanggil hanya kalau isLinearLayout() == true) */
    void removeDividers();
}