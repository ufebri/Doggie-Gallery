package com.raylabs.doggie.domain.model

import com.raylabs.doggie.vo.BreedCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BreedCategoryTest {

    @Test
    fun `constructor sets all fields correctly`() {
        val category = BreedCategory(
            breed = "hound",
            subBreed = "afghan",
            displayName = "Afghan Hound",
            imageUrl = "http://example.com/afghan.jpg"
        )

        assertEquals("hound", category.breed)
        assertEquals("afghan", category.subBreed)
        assertEquals("Afghan Hound", category.displayName)
        assertEquals("http://example.com/afghan.jpg", category.imageUrl)
    }

    @Test
    fun `copy creates new object with overridden fields`() {
        val original = BreedCategory("pug", null, "Pug", "url1")
        val copy = original.copy(imageUrl = "url2")

        assertEquals("pug", copy.breed)          // unchanged
        assertEquals("url2", copy.imageUrl)      // updated
        assertNotEquals(original, copy)          // equals harus beda
    }

    @Test
    fun `equals and hashCode work for identical objects`() {
        val a = BreedCategory("beagle", null, "Beagle", "url")
        val b = BreedCategory("beagle", null, "Beagle", "url")

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `toString contains class name and properties`() {
        val cat = BreedCategory("shiba", null, "Shiba Inu", "url")
        val text = cat.toString()
        assertTrue(text.contains("BreedCategory"))
        assertTrue(text.contains("shiba"))
        assertTrue(text.contains("Shiba Inu"))
    }
}