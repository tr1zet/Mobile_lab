package com.example.rickandmorty

import com.example.rickandmorty.data.CharacterRepository
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.model.CharacterDetails
import com.example.rickandmorty.model.CharacterPage
import com.example.rickandmorty.model.Episode

class FakeCharacterRepository : CharacterRepository {
    val pages = mutableMapOf<Int, CharacterPage>()
    var details = CharacterDetails(character(), emptyList())
    var error: Throwable? = null

    override suspend fun getCharacters(page: Int): CharacterPage {
        error?.let { throw it }
        return requireNotNull(pages[page])
    }

    override suspend fun getCharacterDetails(id: Int): CharacterDetails {
        error?.let { throw it }
        return details
    }
}

fun character(id: Int = 1, name: String = "Rick Sanchez") = Character(
    id = id,
    name = name,
    status = "Alive",
    species = "Human",
    type = "",
    gender = "Male",
    origin = "Earth (C-137)",
    location = "Citadel of Ricks",
    imageUrl = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
    episodeUrls = listOf("https://rickandmortyapi.com/api/episode/1")
)

fun episode(id: Int = 1) = Episode(
    id = id,
    name = "Pilot",
    airDate = "December 2, 2013",
    code = "S01E01"
)
