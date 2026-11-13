package com.example.rickandmorty.data

import com.example.rickandmorty.model.CharacterDetails
import com.example.rickandmorty.model.CharacterPage
import com.example.rickandmorty.network.RickAndMortyApi
import com.example.rickandmorty.network.toDomain

interface CharacterRepository {
    suspend fun getCharacters(page: Int): CharacterPage
    suspend fun getCharacterDetails(id: Int): CharacterDetails
}

class NetworkCharacterRepository(
    private val api: RickAndMortyApi
) : CharacterRepository {
    override suspend fun getCharacters(page: Int): CharacterPage =
        api.getCharacters(page).toDomain()

    override suspend fun getCharacterDetails(id: Int): CharacterDetails {
        val character = api.getCharacter(id).toDomain()
        val episodeIds = character.episodeUrls.mapNotNull { url ->
            url.substringAfterLast('/').toIntOrNull()
        }
        val episodes = when (episodeIds.size) {
            0 -> emptyList()
            1 -> listOf(api.getEpisode(episodeIds.single()).toDomain())
            else -> api.getEpisodes(episodeIds.joinToString(",")).map { it.toDomain() }
        }
        return CharacterDetails(character = character, episodes = episodes)
    }
}
