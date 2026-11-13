package com.example.rickandmorty.network

import com.example.rickandmorty.model.Character
import com.example.rickandmorty.model.CharacterPage
import com.example.rickandmorty.model.Episode
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterPageDto

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path("ids") ids: String): List<EpisodeDto>
}

data class CharacterPageDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

data class PageInfoDto(
    val next: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: NamedResourceDto,
    val location: NamedResourceDto,
    val image: String,
    val episode: List<String>
)

data class NamedResourceDto(
    val name: String
)

data class EpisodeDto(
    val id: Int,
    val name: String,
    @SerializedName("air_date") val airDate: String,
    val episode: String
)

fun CharacterPageDto.toDomain() = CharacterPage(
    characters = results.map(CharacterDto::toDomain),
    hasNextPage = info.next != null
)

fun CharacterDto.toDomain() = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin.name,
    location = location.name,
    imageUrl = image,
    episodeUrls = episode
)

fun EpisodeDto.toDomain() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = episode
)
