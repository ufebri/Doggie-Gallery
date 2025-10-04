package com.raylabs.doggie.utils.tab

object TabDividerDelegate {
    /** logic murni yang bisa di-unit test */
    @JvmStatic
    fun apply(controller: DividerController?) {
        if (controller?.isLinearLayout() == true) {
            controller.removeDividers()
        }
    }
}
