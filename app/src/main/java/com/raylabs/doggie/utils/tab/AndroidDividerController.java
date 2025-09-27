package com.raylabs.doggie.utils.tab;

import android.view.ViewGroup;
import android.widget.LinearLayout;

public final class AndroidDividerController implements DividerController {
    private final ViewGroup strip;

    public AndroidDividerController(ViewGroup strip) {
        this.strip = strip;
    }

    @Override
    public boolean isLinearLayout() {
        return strip instanceof LinearLayout;
    }

    @Override
    public void removeDividers() {
        if (strip instanceof LinearLayout) {
            ((LinearLayout) strip).setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        }
    }
}