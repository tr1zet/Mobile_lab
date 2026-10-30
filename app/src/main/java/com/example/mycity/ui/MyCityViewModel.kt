package com.example.mycity.ui

import androidx.lifecycle.ViewModel
import com.example.mycity.data.KemerovoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MyCityUiState(
    val selectedCategoryId: String = KemerovoRepository.categories.first().id,
    val selectedPlaceId: String = KemerovoRepository.places.first().id
)

class MyCityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyCityUiState())
    val uiState: StateFlow<MyCityUiState> = _uiState.asStateFlow()

    fun selectCategory(categoryId: String) {
        val firstPlace = KemerovoRepository.placesForCategory(categoryId).firstOrNull() ?: return
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                selectedPlaceId = firstPlace.id
            )
        }
    }

    fun selectPlace(placeId: String) {
        val place = KemerovoRepository.place(placeId) ?: return
        _uiState.update {
            it.copy(
                selectedCategoryId = place.categoryId,
                selectedPlaceId = place.id
            )
        }
    }
}
