package com.example.rickandmorty.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.ui.character.CharacterScreen
import com.example.rickandmorty.ui.character.CharacterViewModel
import com.example.rickandmorty.ui.characters.CharactersScreen
import com.example.rickandmorty.ui.characters.CharactersViewModel

private object Routes {
    const val Characters = "characters"
    const val Character = "character/{characterId}"

    fun character(id: Int) = "character/$id"
}

@Composable
fun RickAndMortyApp(repository: CharacterRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Characters
    ) {
        composable(Routes.Characters) {
            val viewModel: CharactersViewModel = viewModel(
                factory = CharactersViewModel.Factory(repository)
            )
            CharactersScreen(
                viewModel = viewModel,
                onCharacterClick = { id -> navController.navigate(Routes.character(id)) }
            )
        }

        composable(
            route = Routes.Character,
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { entry ->
            val characterId = entry.arguments?.getInt("characterId") ?: return@composable
            val viewModel: CharacterViewModel = viewModel(
                factory = CharacterViewModel.Factory(characterId, repository)
            )
            CharacterScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
