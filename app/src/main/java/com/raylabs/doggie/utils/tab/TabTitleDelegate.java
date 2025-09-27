package com.raylabs.doggie.utils.tab;

public final class TabTitleDelegate {
    private final String[] titles;

    public TabTitleDelegate(String[] titles) {
        if (titles == null) throw new IllegalArgumentException("titles cannot be null");
        this.titles = titles;
    }

    public String requireAt(int position) {
        if (position < 0 || position >= titles.length) {
            throw new IndexOutOfBoundsException("Invalid index: " + position + ", size: " + titles.length);
        }
        return titles[position];
    }

    public int size() {
        return titles.length;
    }
}