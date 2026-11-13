package com.example.rickandmorty.ui.character

import com.example.rickandmorty.FakeCharacterRepository
import com.example.rickandmorty.MainDispatcherRule
import com.example.rickandmorty.character
import com.example.rickandmorty.episode
import com.example.rickandmorty.model.CharacterDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadCharacter_returnsCharacterAndEpisodes() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCharacterRepository().apply {
                details = CharacterDetails(character(), listOf(episode()))
            }

            val viewModel = CharacterViewModel(characterId = 1, repository = repository)
            advanceUntilIdle()

            assertEquals("Rick Sanchez", viewModel.uiState.value.details?.character?.name)
            assertEquals("Pilot", viewModel.uiState.value.details?.episodes?.single()?.name)
            assertFalse(viewModel.uiState.value.isLoading)
        }
}
