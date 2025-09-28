package com.raylabs.doggie.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PagerDelegateTest {

    @Test(expected = IllegalArgumentException.class)
    public void of_throwsWhenItemsNull() {
        PagerDelegate.of(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_throwsWhenSizesMismatch() {
        List<Object> items = Arrays.asList(new Object(), new Object());
        List<String> titles = Collections.singletonList("OnlyOne");
        PagerDelegate.of(items, titles);
    }

    @Test
    public void getCount_returnsSize() {
        List<Object> items = Arrays.asList(new Object(), new Object(), new Object());
        PagerDelegate<Object> d = PagerDelegate.of(items, null);
        assertEquals(3, d.getCount());
    }

    @Test
    public void requireAt_returnsItem_whenValid() {
        Object a = new Object();
        Object b = new Object();
        PagerDelegate<Object> d = PagerDelegate.of(Arrays.asList(a, b), null);
        assertSame(a, d.requireAt(0));
        assertSame(b, d.requireAt(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void requireAt_throws_whenNegative() {
        PagerDelegate<Object> d = PagerDelegate.of(Collections.singletonList(new Object()), null);
        d.requireAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void requireAt_throws_whenOutOfRange() {
        PagerDelegate<Object> d = PagerDelegate.of(Collections.singletonList(new Object()), null);
        d.requireAt(5);
    }

    @Test
    public void getTitleOrNull_returnsNull_whenNoTitles() {
        PagerDelegate<Object> d = PagerDelegate.of(Collections.singletonList(new Object()), null);
        assertNull(d.getTitleOrNull(0));
    }

    @Test
    public void getTitleOrNull_returnsTitle_whenProvided() {
        List<Object> items = Arrays.asList(new Object(), new Object());
        List<String> titles = Arrays.asList("Dogs", "Favorites");
        PagerDelegate<Object> d = PagerDelegate.of(items, titles);
        assertEquals("Dogs", d.getTitleOrNull(0));
        assertEquals("Favorites", d.getTitleOrNull(1));
    }

    @Test
    public void getTitleOrNull_returnsNull_whenIndexOutOfRange() {
        List<Object> items = Collections.singletonList(new Object());
        List<String> titles = Collections.singletonList("Dogs");
        PagerDelegate<Object> d = PagerDelegate.of(items, titles);
        assertNull(d.getTitleOrNull(2));
        assertNull(d.getTitleOrNull(-1));
    }
}
