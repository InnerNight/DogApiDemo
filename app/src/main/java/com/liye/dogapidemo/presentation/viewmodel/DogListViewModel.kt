package com.liye.dogapidemo.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.liye.dogapidemo.data.model.DogBreed
import com.liye.dogapidemo.data.repository.DogRepository
import com.liye.dogapidemo.data.repository.LocalCacheRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DogListUiState(
    val isLoading: Boolean = false,
    val breeds: List<DogBreed> = emptyList(),
    val error: String? = null
)

class DogListViewModel(private val repository: DogRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DogListUiState())
    val uiState: StateFlow<DogListUiState> = _uiState.asStateFlow()
    
    init {
        loadBreeds()
    }
    
    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val breeds = repository.getAllBreeds()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    breeds = breeds,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    breeds = emptyList(),
                    error = e.message
                )
            }
        }
    }
}

class DogListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DogListViewModel::class.java)) {
            val localCacheRepository = LocalCacheRepository(application)
            val repository = DogRepository(localCacheRepository)
            @Suppress("UNCHECKED_CAST")
            return DogListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}