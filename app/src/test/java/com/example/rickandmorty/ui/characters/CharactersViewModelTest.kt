package com.example.rickandmorty.ui.characters

import com.example.rickandmorty.FakeCharacterRepository
import com.example.rickandmorty.MainDispatcherRule
import com.example.rickandmorty.character
import com.example.rickandmorty.model.CharacterPage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialLoad_populatesFirstPage() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeCharacterRepository().apply {
            pages[1] = CharacterPage(listOf(character()), hasNextPage = true)
        }

        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        assertEquals(listOf("Rick Sanchez"), viewModel.uiState.value.characters.map { it.name })
        assertFalse(viewModel.uiState.value.isInitialLoading)
        assertEquals(true, viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun loadNextPage_appendsCharactersAndStopsAtLastPage() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCharacterRepository().apply {
                pages[1] = CharacterPage(listOf(character()), hasNextPage = true)
                pages[2] = CharacterPage(
                    listOf(character(id = 2, name = "Morty Smith")),
                    hasNextPage = false
                )
            }
            val viewModel = CharactersViewModel(repository)
            advanceUntilIdle()

            viewModel.loadNextPage()
            advanceUntilIdle()

            assertEquals(
                listOf("Rick Sanchez", "Morty Smith"),
                viewModel.uiState.value.characters.map { it.name }
            )
            assertFalse(viewModel.uiState.value.canLoadMore)
        }

    @Test
    fun failedLoad_exposesRetryableError() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeCharacterRepository().apply {
            error = IllegalStateException("network unavailable")
        }

        val viewModel = CharactersViewModel(repository)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isInitialLoading)
    }
}
