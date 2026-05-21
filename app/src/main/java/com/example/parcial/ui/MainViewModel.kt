package com.example.parcial.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial.data.model.Character
import com.example.parcial.data.repository.CharacterRepository
import com.example.parcial.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CharacterRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _charactersState = MutableStateFlow<UiState<List<Character>>>(UiState.Loading)
    val charactersState: StateFlow<UiState<List<Character>>> = _charactersState.asStateFlow()

    private val _detailState = MutableStateFlow<UiState<Character>>(UiState.Loading)
    val detailState: StateFlow<UiState<Character>> = _detailState.asStateFlow()

    init {
        fetchCharacters()
    }

    fun fetchCharacters() {
        viewModelScope.launch {
            _charactersState.value = UiState.Loading
            repository.getCharacters().collect { result ->
                result.onSuccess {
                    _charactersState.value = UiState.Success(it)
                    NotificationHelper.showNotification(
                        context,
                        "Carga Completada",
                        "Se han actualizado ${it.size} personajes."
                    )
                }.onFailure {
                    _charactersState.value = UiState.Error(it.message ?: "Unknown Error")
                }
            }
        }
    }

    fun fetchCharacterDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            repository.getCharacter(id).onSuccess {
                _detailState.value = UiState.Success(it)
            }.onFailure {
                _detailState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }
}