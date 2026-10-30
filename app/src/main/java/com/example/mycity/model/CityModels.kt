package com.example.mycity.model

import androidx.annotation.DrawableRes

data class CityCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int
)

data class CityPlace(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val addressHint: String,
    @DrawableRes val imageRes: Int
)
