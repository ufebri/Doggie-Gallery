package com.raylabs.doggie.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ConstantTest {

    @Test
    public void imageItemCountLoaded_is50() {
        // Pastikan constant sesuai nilai yang diharapkan
        assertEquals("50", Constant.IMAGE_ITEM_COUNT_LOADED);
    }

    @Test
    public void imageItemCountLoaded_isNotEmpty() {
        // Pastikan tidak null atau kosong
        assertNotNull(Constant.IMAGE_ITEM_COUNT_LOADED);
        assertFalse(Constant.IMAGE_ITEM_COUNT_LOADED.isEmpty());
    }
}