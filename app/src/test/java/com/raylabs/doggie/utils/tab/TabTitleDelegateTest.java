package com.raylabs.doggie.utils.tab;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TabTitleDelegateTest {

    @Test(expected = IllegalArgumentException.class)
    public void ctor_throws_whenNull() {
        new TabTitleDelegate(null);
    }

    @Test
    public void requireAt_returnsCorrectTitle() {
        TabTitleDelegate d = new TabTitleDelegate(new String[]{"Home", "Popular", "Liked"});
        assertEquals("Home", d.requireAt(0));
        assertEquals("Popular", d.requireAt(1));
        assertEquals("Liked", d.requireAt(2));
        assertEquals(3, d.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void requireAt_throws_whenNegative() {
        TabTitleDelegate d = new TabTitleDelegate(new String[]{"Only"});
        d.requireAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void requireAt_throws_whenOutOfRange() {
        TabTitleDelegate d = new TabTitleDelegate(new String[]{"Only"});
        d.requireAt(3);
    }
}