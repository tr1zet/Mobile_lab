package com.example.topicsgrid

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Artwork(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val artistRes: Int,
    @param:StringRes val yearRes: Int
)

data class ArtSpaceUiState(
    val artworks: List<Artwork> = defaultArtworks,
    val currentArtworkIndex: Int = 0
) {
    val currentArtwork: Artwork
        get() = artworks[currentArtworkIndex]
}

private val defaultArtworks = listOf(
    Artwork(
        imageRes = R.drawable.user_photo_sunset,
        titleRes = R.string.artwork_one_title,
        artistRes = R.string.artwork_one_artist,
        yearRes = R.string.artwork_one_year
    ),
    Artwork(
        imageRes = R.drawable.user_photo_tiger,
        titleRes = R.string.artwork_two_title,
        artistRes = R.string.artwork_two_artist,
        yearRes = R.string.artwork_two_year
    ),
    Artwork(
        imageRes = R.drawable.user_photo_cat,
        titleRes = R.string.artwork_three_title,
        artistRes = R.string.artwork_three_artist,
        yearRes = R.string.artwork_three_year
    )
)
