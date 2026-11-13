package com.example.rickandmorty.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.model.Character
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val errorMessage: String? = null
)

class CharactersViewModel(
    private val repository: CharacterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var nextPage = 1

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        val current = _uiState.value
        if (current.isInitialLoading || current.isLoadingMore || !current.canLoadMore) return

        _uiState.update {
            it.copy(
                isInitialLoading = it.characters.isEmpty(),
                isLoadingMore = it.characters.isNotEmpty(),
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val page = repository.getCharacters(nextPage)
                _uiState.update { state ->
                    state.copy(
                        characters = state.characters + page.characters,
                        isInitialLoading = false,
                        isLoadingMore = false,
                        canLoadMore = page.hasNextPage,
                        errorMessage = null
                    )
                }
                nextPage += 1
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isLoadingMore = false,
                        errorMessage = "Не удалось загрузить персонажей. Проверьте интернет-соединение."
                    )
                }
            }
        }
    }

    fun retry() = loadNextPage()

    class Factory(
        private val repository: CharacterRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(CharactersViewModel::class.java))
            return CharactersViewModel(repository) as T
        }
    }
}
