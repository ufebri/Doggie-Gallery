package com.raylabs.doggie.utils.tab

class TabTitleDelegate(titlesInput: Array<String>?) {

    private val titles: Array<String>

    init {
        requireNotNull(titlesInput) { "titles cannot be null" }
        titles = titlesInput
    }

    fun requireAt(position: Int): String {
        if (position !in titles.indices) {
            throw IndexOutOfBoundsException("Invalid index: $position, size: ${titles.size}")
        }
        return titles[position]
    }

    fun size(): Int = titles.size
}
