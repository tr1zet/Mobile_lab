package com.example.dessertclicker.data

import androidx.annotation.DrawableRes

data class DessertUiState(
    val currentDessertIndex: Int = 0,
    val dessertsSold: Int = 0,
    val revenue: Int = 0,
    val currentDessertPrice: Int = Datasource.dessertList[currentDessertIndex].price,
    @param:DrawableRes
    val currentDessertImageId: Int = Datasource.dessertList[currentDessertIndex].imageId
)
