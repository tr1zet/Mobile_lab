package com.example.dessertclicker.ui

import com.example.dessertclicker.data.Datasource
import org.junit.Assert.assertEquals
import org.junit.Test

class DessertViewModelTest {

    @Test
    fun dessertClicked_updatesSalesAndRevenue() {
        val viewModel = DessertViewModel()
        val firstDessert = Datasource.dessertList.first()

        viewModel.onDessertClicked()

        val state = viewModel.uiState.value
        assertEquals(1, state.dessertsSold)
        assertEquals(firstDessert.price, state.revenue)
    }

    @Test
    fun dessertClicked_atProductionThreshold_changesDessert() {
        val viewModel = DessertViewModel()
        val secondDessert = Datasource.dessertList[1]

        repeat(secondDessert.startProductionAmount) {
            viewModel.onDessertClicked()
        }

        val state = viewModel.uiState.value
        assertEquals(1, state.currentDessertIndex)
        assertEquals(secondDessert.imageId, state.currentDessertImageId)
        assertEquals(secondDessert.price, state.currentDessertPrice)
    }
}
