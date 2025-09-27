package com.raylabs.doggie.utils.tab;

public final class TabDividerDelegate {
    private TabDividerDelegate() {}

    /** logic murni yang bisa di-unit test */
    public static void apply(DividerController controller) {
        if (controller == null) return;
        if (controller.isLinearLayout()) {
            controller.removeDividers();
        }
    }
}