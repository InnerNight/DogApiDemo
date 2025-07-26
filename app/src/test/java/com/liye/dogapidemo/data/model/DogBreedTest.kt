package com.liye.dogapidemo.data.model

import org.junit.Assert.*
import org.junit.Test

class DogBreedTest {
    
    @Test
    fun `getDisplayName should capitalize first letter`() {
        // Given
        val breed = DogBreed("beagle")
        
        // When
        val displayName = breed.getDisplayName()
        
        // Then
        assertEquals("Beagle", displayName)
    }
    
    @Test
    fun `getDisplayName should handle empty string`() {
        // Given
        val breed = DogBreed("")
        
        // When
        val displayName = breed.getDisplayName()
        
        // Then
        assertEquals("", displayName)
    }
    
    @Test
    fun `getAllVariants should return single breed when no sub-breeds`() {
        // Given
        val breed = DogBreed("beagle")
        
        // When
        val variants = breed.getAllVariants()
        
        // Then
        assertEquals(listOf("beagle"), variants)
    }
    
    @Test
    fun `getAllVariants should return sub-breeds plus main breed`() {
        // Given
        val breed = DogBreed("bulldog", listOf("english", "french"))
        
        // When
        val variants = breed.getAllVariants()
        
        // Then
        val expected = listOf("english bulldog", "french bulldog", "bulldog")
        assertEquals(expected, variants)
    }
    
    @Test
    fun `getAllVariants should handle empty sub-breeds list`() {
        // Given
        val breed = DogBreed("poodle", emptyList())
        
        // When
        val variants = breed.getAllVariants()
        
        // Then
        assertEquals(listOf("poodle"), variants)
    }
}