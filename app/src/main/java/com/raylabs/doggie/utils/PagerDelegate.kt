package com.raylabs.doggie.utils

/**
 * Small helper that guards a list of pager items and optional titles.
 */
class PagerDelegate<T> private constructor(
    private val items: List<T>,
    private val titles: List<String>?
) {

    init {
        if (titles != null && items.size != titles.size) {
            throw IllegalArgumentException("Jumlah Fragment dan Judul harus sama!")
        }
    }

    val count: Int
        get() = items.size

    fun requireAt(position: Int): T {
        if (position !in items.indices) {
            throw IndexOutOfBoundsException("Posisi tidak valid: $position, ukuran: ${items.size}")
        }
        return items[position]
    }

    fun getTitleOrNull(position: Int): String? {
        val safeTitles = titles ?: return null
        return safeTitles.getOrNull(position)
    }

    companion object {
        @JvmStatic
        fun <T> of(items: List<T>?, maybeTitles: List<String>?): PagerDelegate<T> {
            requireNotNull(items) { "items cannot be null" }
            return PagerDelegate(items, maybeTitles)
        }
    }
}
