package com.liye.dogapidemo.presentation.viewmodel

import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.QuizQuestion
import com.liye.dogapidemo.data.repository.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DogQuizViewModelTest {

    @Mock
    private lateinit var mockRepository: DogRepository
    
    private lateinit var viewModel: DogQuizViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be loading`() = runTest {
        // Given
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(
            Result.success(createMockQuizQuestion())
        )
        
        // When
        viewModel = DogQuizViewModel()
        // Inject mock repository using reflection
        val field = DogQuizViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, mockRepository)
        
        // Then
        val initialState = viewModel.uiState.first()
        assertTrue(initialState.isLoading)
    }
    
    @Test
    fun `should load question successfully after initialization`() = runTest {
        // Given
        val mockQuestion = createMockQuizQuestion()
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(Result.success(mockQuestion))
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(mockQuestion, state.currentQuestion)
        assertEquals(1, state.totalQuestions)
    }
    
    @Test
    fun `should handle preload breeds failure`() = runTest {
        // Given
        whenever(mockRepository.preloadBreeds()).thenReturn(
            Result.failure(Exception("Network error"))
        )
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.error!!.contains("Failed to load dog breeds"))
    }
    
    @Test
    fun `should handle generate question failure`() = runTest {
        // Given
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(
            Result.failure(Exception("API error"))
        )
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertTrue(state.error!!.contains("Failed to load question"))
    }
    
    @Test
    fun `selectAnswer should update state with correct answer`() = runTest {
        // Given
        val mockQuestion = createMockQuizQuestion()
        val correctBreed = mockQuestion.correctBreed
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(Result.success(mockQuestion))
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.selectAnswer(correctBreed)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertEquals(correctBreed, state.selectedAnswer)
        assertTrue(state.showResult)
        assertTrue(state.isCorrect)
        assertEquals(1, state.score)
    }
    
    @Test
    fun `selectAnswer should update state with wrong answer`() = runTest {
        // Given
        val mockQuestion = createMockQuizQuestion()
        val wrongBreed = mockQuestion.options.find { it != mockQuestion.correctBreed }!!
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(Result.success(mockQuestion))
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.selectAnswer(wrongBreed)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertEquals(wrongBreed, state.selectedAnswer)
        assertTrue(state.showResult)
        assertFalse(state.isCorrect)
        assertEquals(0, state.score)
    }
    
    @Test
    fun `nextQuestion should load new question when under 10 questions`() = runTest {
        // Given
        val mockQuestion1 = createMockQuizQuestion()
        val mockQuestion2 = createMockQuizQuestion("poodle")
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion())
            .thenReturn(Result.success(mockQuestion1))
            .thenReturn(Result.success(mockQuestion2))
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.nextQuestion()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.totalQuestions)
        assertFalse(state.gameFinished)
        verify(mockRepository, times(2)).generateQuizQuestion()
    }
    
    @Test
    fun `nextQuestion should finish game when reaching 10 questions`() = runTest {
        // Given
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(
            Result.success(createMockQuizQuestion())
        )
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Simulate 10 questions
        val field = DogQuizViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<DogQuizUiState>
        stateFlow.value = stateFlow.value.copy(totalQuestions = 10)
        
        viewModel.nextQuestion()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.gameFinished)
    }
    
    @Test
    fun `restartGame should reset state and load new question`() = runTest {
        // Given
        val mockQuestion = createMockQuizQuestion()
        whenever(mockRepository.preloadBreeds()).thenReturn(Result.success(Unit))
        whenever(mockRepository.generateQuizQuestion()).thenReturn(Result.success(mockQuestion))
        
        // When
        viewModel = DogQuizViewModel()
        injectMockRepository()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Set some state first
        val field = DogQuizViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<DogQuizUiState>
        stateFlow.value = stateFlow.value.copy(score = 5, totalQuestions = 8, gameFinished = true)
        
        viewModel.restartGame()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.first()
        assertEquals(0, state.score)
        assertEquals(1, state.totalQuestions)
        assertFalse(state.gameFinished)
        assertNotNull(state.currentQuestion)
    }
    
    @Test
    fun `clearError should remove error from state`() = runTest {
        // Given
        viewModel = DogQuizViewModel()
        injectMockRepository()
        
        val field = DogQuizViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<DogQuizUiState>
        stateFlow.value = stateFlow.value.copy(error = "Test error")
        
        // When
        viewModel.clearError()
        
        // Then
        val state = viewModel.uiState.first()
        assertNull(state.error)
    }
    
    private fun createMockQuizQuestion(breedName: String = "beagle"): QuizQuestion {
        val breeds = listOf(
            DogBreed("beagle"),
            DogBreed("poodle"),
            DogBreed("bulldog"),
            DogBreed("terrier")
        )
        val correctBreed = breeds.find { it.name == breedName } ?: breeds.first()
        
        return QuizQuestion(
            imageUrl = "https://images.dog.ceo/breeds/$breedName/test.jpg",
            correctBreed = correctBreed,
            options = breeds,
            correctAnswer = correctBreed.getDisplayName()
        )
    }
    
    private fun injectMockRepository() {
        val field = DogQuizViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, mockRepository)
    }
}