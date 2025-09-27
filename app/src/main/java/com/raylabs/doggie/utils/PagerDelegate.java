package com.raylabs.doggie.utils;

import java.util.List;

public final class PagerDelegate<T> {
    private final List<T> items;
    private final List<String> titles; // boleh null

    public static <T> PagerDelegate<T> of(List<T> items, List<String> maybeTitles) {
        if (items == null) throw new IllegalArgumentException("items cannot be null");
        if (maybeTitles != null && items.size() != maybeTitles.size()) {
            throw new IllegalArgumentException("Jumlah Fragment dan Judul harus sama!");
        }
        return new PagerDelegate<>(items, maybeTitles);
    }

    private PagerDelegate(List<T> items, List<String> titles) {
        this.items = items;
        this.titles = titles;
    }

    public int getCount() {
        return items.size();
    }

    public T requireAt(int position) {
        if (position < 0 || position >= items.size()) {
            throw new IndexOutOfBoundsException("Posisi tidak valid: " + position + ", ukuran: " + items.size());
        }
        return items.get(position);
    }

    public String getTitleOrNull(int position) {
        if (titles == null) return null;
        if (position < 0 || position >= titles.size()) return null;
        return titles.get(position);
    }
}
