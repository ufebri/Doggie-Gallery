package com.raylabs.doggie.source.local.entity;

import org.junit.Test;
import static org.junit.Assert.*;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;

public class DoggieEntityTest {

    @Test
    public void doggieEntityConstructorAndGetters_workCorrectly() {
        // Arrange
        String expectedType = "pug";
        String expectedLink = "https://images.dog.ceo/breeds/pug/n02088094_1007.jpg";
        String expectedTag = "for-you";

        // Act
        DoggieEntity entity = new DoggieEntity(expectedType, expectedLink, expectedTag);

        // Assert
        assertNotNull(entity);
        assertEquals(expectedType, entity.getType());
        assertEquals(expectedLink, entity.getLink());
        assertEquals(expectedTag, entity.getTag());
    }

    @Test
    public void doggieEntity_fieldsAreNonNull() {
        DoggieEntity entity = new DoggieEntity("beagle", "link", "popular");

        assertNotNull(entity.getType());
        assertNotNull(entity.getLink());
        assertNotNull(entity.getTag());
    }

    @Test
    public void doggieEntity_equalityCheckByFields() {
        DoggieEntity dog1 = new DoggieEntity("husky", "link1", "liked");
        DoggieEntity dog2 = new DoggieEntity("husky", "link1", "liked");

        // Because no equals() is overridden, they should not be equal (different instances)
        assertNotEquals(dog1, dog2);

        // But individual fields should match
        assertEquals(dog1.getType(), dog2.getType());
        assertEquals(dog1.getLink(), dog2.getLink());
        assertEquals(dog1.getTag(), dog2.getTag());
    }
}