package com.example.tasks.series.model

data class Show(
    val id: Int,
    val name: String,
    val summary: String,
    val image: String?,
    val genres: String,
    val rating: Double?,
    val premiered: String?,
    val status: String,
    val language: String,
    val url: String,
    val favorite: Boolean = false
)

data class Episode(
    val id: Int,
    val showId: Int,
    val name: String,
    val season: Int,
    val number: Int?,
    val airdate: String?
)

sealed interface RefreshResult {
    data object Success : RefreshResult
    data class Error(val message: String) : RefreshResult
}
