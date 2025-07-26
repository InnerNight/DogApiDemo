package com.liye.dogapidemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.model.QuizQuestion
import com.liye.dogapidemo.data.repository.DogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * single datasource
 */
data class DogQuizUiState(
    val isLoading: Boolean = false,
    val currentQuestion: QuizQuestion? = null,
    val selectedAnswer: DogBreed? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val error: String? = null,
    val gameFinished: Boolean = false
)

class DogQuizViewModel : ViewModel() {
    private val repository = DogRepository()
    
    private val _uiState = MutableStateFlow(DogQuizUiState())
    val uiState: StateFlow<DogQuizUiState> = _uiState.asStateFlow()
    
    init {
        initializeQuiz()
    }
    
    private fun initializeQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            repository.preloadBreeds().fold(
                onSuccess = {
                    loadNextQuestion()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load dog breeds: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun loadNextQuestion() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            repository.generateQuizQuestion().fold(
                onSuccess = { question ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentQuestion = question,
                        selectedAnswer = null,
                        showResult = false,
                        totalQuestions = _uiState.value.totalQuestions + 1
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load question: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun selectAnswer(breed: DogBreed) {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        
        val isCorrect = breed.name == currentQuestion.correctBreed.name
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        
        _uiState.value = currentState.copy(
            selectedAnswer = breed,
            showResult = true,
            isCorrect = isCorrect,
            score = newScore
        )
    }
    
    fun nextQuestion() {
        if (_uiState.value.totalQuestions >= 10) {
            _uiState.value = _uiState.value.copy(gameFinished = true)
        } else {
            loadNextQuestion()
        }
    }
    
    fun restartGame() {
        _uiState.value = DogQuizUiState()
        loadNextQuestion()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}