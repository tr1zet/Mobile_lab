package com.example.topicsgrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArtSpaceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ArtSpaceUiState())
    val uiState: StateFlow<ArtSpaceUiState> = _uiState.asStateFlow()

    fun showNextArtwork() {
        _uiState.update { state ->
            state.copy(
                currentArtworkIndex = (state.currentArtworkIndex + 1) % state.artworks.size
            )
        }
    }

    fun showPreviousArtwork() {
        _uiState.update { state ->
            val previousIndex = if (state.currentArtworkIndex == 0) {
                state.artworks.lastIndex
            } else {
                state.currentArtworkIndex - 1
            }
            state.copy(currentArtworkIndex = previousIndex)
        }
    }
}
