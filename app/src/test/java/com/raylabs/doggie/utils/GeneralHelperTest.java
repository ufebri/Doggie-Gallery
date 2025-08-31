package com.raylabs.doggie.utils;

import static org.junit.Assert.assertEquals;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit test for GeneralHelper.pxToDp().
 */
public class GeneralHelperTest {

    @Test
    public void pxToDp_correctConversion() {
        // Arrange
        int px = 200;
        float density = 2.0f; // Example density (xhdpi)

        DisplayMetrics metrics = new DisplayMetrics();
        metrics.density = density;

        // Mock static call Resources.getSystem()
        try (MockedStatic<Resources> mocked = Mockito.mockStatic(Resources.class)) {
            mocked.when(Resources::getSystem).thenReturn(Mockito.mock(Resources.class));
            Mockito.when(Resources.getSystem().getDisplayMetrics()).thenReturn(metrics);

            // Act
            int dp = GeneralHelper.pxToDp(px);

            // Assert
            assertEquals((int) (px / density), dp);
        }
    }

    @Test
    public void pxToDp_returnsZeroWhenPxIsZero() {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.density = 3.0f; // arbitrary density

        try (MockedStatic<Resources> mocked = Mockito.mockStatic(Resources.class)) {
            mocked.when(Resources::getSystem).thenReturn(Mockito.mock(Resources.class));
            Mockito.when(Resources.getSystem().getDisplayMetrics()).thenReturn(metrics);

            int dp = GeneralHelper.pxToDp(0);

            assertEquals(0, dp);
        }
    }
}