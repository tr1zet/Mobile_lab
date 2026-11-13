package com.example.rickandmorty.model

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeUrls: List<String>
)

data class Episode(
    val id: Int,
    val name: String,
    val airDate: String,
    val code: String
)

data class CharacterPage(
    val characters: List<Character>,
    val hasNextPage: Boolean
)

data class CharacterDetails(
    val character: Character,
    val episodes: List<Episode>
)
