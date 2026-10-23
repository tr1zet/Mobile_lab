package com.example.topicsgrid

import org.junit.Assert.assertEquals
import org.junit.Test

class ArtSpaceViewModelTest {

    @Test
    fun nextArtwork_afterLastArtwork_wrapsToFirstArtwork() {
        val viewModel = ArtSpaceViewModel()

        repeat(3) { viewModel.showNextArtwork() }

        assertEquals(0, viewModel.uiState.value.currentArtworkIndex)
    }

    @Test
    fun previousArtwork_fromFirstArtwork_wrapsToLastArtwork() {
        val viewModel = ArtSpaceViewModel()

        viewModel.showPreviousArtwork()

        assertEquals(2, viewModel.uiState.value.currentArtworkIndex)
    }
}
