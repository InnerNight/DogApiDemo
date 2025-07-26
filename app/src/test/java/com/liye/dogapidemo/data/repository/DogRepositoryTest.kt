package com.liye.dogapidemo.data.repository

import com.liye.dogapidemo.data.model.BreedsResponse
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.DogImageResponse
import com.liye.dogapidemo.data.network.DogApiService
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class DogRepositoryTest {

    @Mock
    private lateinit var mockApiService: DogApiService
    
    private lateinit var repository: DogRepository
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = DogRepository()
        // Use reflection to inject the mock
        val field = DogRepository::class.java.getDeclaredField("apiService")
        field.isAccessible = true
        field.set(repository, mockApiService)
    }
    
    @After
    fun tearDown() {
        repository.cleanup()
    }
    
    @Test
    fun `getAllBreeds should return breeds from API`() = runTest {
        // Given
        val mockResponse = BreedsResponse(
            message = mapOf(
                "beagle" to emptyList(),
                "bulldog" to listOf("english", "french"),
                "terrier" to listOf("scottish", "yorkshire")
            ),
            status = "success"
        )
        `when`(mockApiService.getAllBreeds()).thenReturn(mockResponse)
        
        // When
        val result = repository.getAllBreeds()
        
        // Then
        assertEquals(3, result.size)
        assertEquals("beagle", result[0].name)
        assertEquals(emptyList<String>(), result[0].subBreeds)
        assertEquals("bulldog", result[1].name)
        assertEquals(listOf("english", "french"), result[1].subBreeds)
        assertEquals("terrier", result[2].name)
        assertEquals(listOf("scottish", "yorkshire"), result[2].subBreeds)
    }
    
    @Test
    fun `getAllBreeds should cache breeds after first call`() = runTest {
        // Given
        val mockResponse = BreedsResponse(
            message = mapOf("beagle" to emptyList()),
            status = "success"
        )
        `when`(mockApiService.getAllBreeds()).thenReturn(mockResponse)
        
        // When
        repository.getAllBreeds()
        repository.getAllBreeds()
        
        // Then
        verify(mockApiService, times(1)).getAllBreeds()
    }
    
    @Test
    fun `generateQuizQuestion should return quiz question with 4 options`() = runTest {
        // Given
        val mockBreedsResponse = BreedsResponse(
            message = mapOf(
                "beagle" to emptyList(),
                "bulldog" to emptyList(),
                "terrier" to emptyList(),
                "poodle" to emptyList(),
                "labrador" to emptyList()
            ),
            status = "success"
        )
        val mockImageResponse = DogImageResponse(
            message = "https://images.dog.ceo/breeds/beagle/n02088364_1000.jpg",
            status = "success"
        )
        
        `when`(mockApiService.getAllBreeds()).thenReturn(mockBreedsResponse)
        `when`(mockApiService.getRandomDogImageByBreed(any())).thenReturn(mockImageResponse)
        
        // When
        val result = repository.generateQuizQuestion()
        
        // Then
        assertTrue(result.isSuccess)
        val question = result.getOrNull()
        assertNotNull(question)
        assertEquals(4, question?.options?.size)
        assertNotNull(question?.imageUrl)
        assertNotNull(question?.correctBreed)
        assertNotNull(question?.correctAnswer)
    }
    
    @Test
    fun `generateQuizQuestion should handle API errors gracefully`() = runTest {
        // Given
        `when`(mockApiService.getAllBreeds()).thenThrow(RuntimeException("Network error"))
        
        // When
        val result = repository.generateQuizQuestion()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `preloadBreeds should return success when API call succeeds`() = runTest {
        // Given
        val mockResponse = BreedsResponse(
            message = mapOf("beagle" to emptyList()),
            status = "success"
        )
        `when`(mockApiService.getAllBreeds()).thenReturn(mockResponse)
        
        // When
        val result = repository.preloadBreeds()
        
        // Then
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `preloadBreeds should return failure when API call fails`() = runTest {
        // Given
        `when`(mockApiService.getAllBreeds()).thenThrow(RuntimeException("Network error"))
        
        // When
        val result = repository.preloadBreeds()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}