package com.example.tasks.series.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvmazeApi {
    @GET("shows")
    suspend fun shows(@Query("page") page: Int): List<ShowDto>

    @GET("search/shows")
    suspend fun search(@Query("q") query: String): List<SearchDto>

    @GET("shows/{id}")
    suspend fun show(@Path("id") id: Int): ShowDto

    @GET("shows/{id}/episodes")
    suspend fun episodes(@Path("id") id: Int): List<EpisodeDto>
}

data class ImageDto(val medium: String?, val original: String?)
data class RatingDto(val average: Double?)
data class SearchDto(val show: ShowDto)
data class ShowDto(
    val id: Int,
    val name: String?,
    val summary: String?,
    val image: ImageDto?,
    val genres: List<String>?,
    val rating: RatingDto?,
    val premiered: String?,
    val status: String?,
    val language: String?,
    val url: String?
)
data class EpisodeDto(
    val id: Int,
    val name: String?,
    val season: Int,
    val number: Int?,
    val airdate: String?
)
