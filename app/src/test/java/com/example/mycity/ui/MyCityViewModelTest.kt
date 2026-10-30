package com.example.mycity.ui

import com.example.mycity.data.KemerovoRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class MyCityViewModelTest {

    @Test
    fun initialState_selectsFirstCategoryAndPlace() {
        val viewModel = MyCityViewModel()

        assertEquals(KemerovoRepository.categories.first().id, viewModel.uiState.value.selectedCategoryId)
        assertEquals(KemerovoRepository.places.first().id, viewModel.uiState.value.selectedPlaceId)
    }

    @Test
    fun selectCategory_selectsItsFirstPlace() {
        val viewModel = MyCityViewModel()
        val category = KemerovoRepository.categories.last()
        val firstPlace = KemerovoRepository.placesForCategory(category.id).first()

        viewModel.selectCategory(category.id)

        assertEquals(category.id, viewModel.uiState.value.selectedCategoryId)
        assertEquals(firstPlace.id, viewModel.uiState.value.selectedPlaceId)
    }

    @Test
    fun selectPlace_updatesCategoryAndPlace() {
        val viewModel = MyCityViewModel()
        val place = KemerovoRepository.places.last()

        viewModel.selectPlace(place.id)

        assertEquals(place.categoryId, viewModel.uiState.value.selectedCategoryId)
        assertEquals(place.id, viewModel.uiState.value.selectedPlaceId)
    }

    @Test
    fun invalidIdentifiers_doNotChangeState() {
        val viewModel = MyCityViewModel()
        val initial = viewModel.uiState.value

        viewModel.selectCategory("unknown")
        viewModel.selectPlace("unknown")

        assertEquals(initial, viewModel.uiState.value)
    }
}
