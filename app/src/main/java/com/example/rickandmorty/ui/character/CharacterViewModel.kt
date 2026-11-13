package com.example.rickandmorty.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.model.CharacterDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterUiState(
    val isLoading: Boolean = false,
    val details: CharacterDetails? = null,
    val errorMessage: String? = null
)

class CharacterViewModel(
    private val characterId: Int,
    private val repository: CharacterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterUiState())
    val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val details = repository.getCharacterDetails(characterId)
                _uiState.value = CharacterUiState(details = details)
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить информацию о персонаже."
                    )
                }
            }
        }
    }

    class Factory(
        private val characterId: Int,
        private val repository: CharacterRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CharacterViewModel::class.java))
            return CharacterViewModel(characterId, repository) as T
        }
    }
}
