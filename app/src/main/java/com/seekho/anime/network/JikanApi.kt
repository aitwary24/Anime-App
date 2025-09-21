package com.seekho.anime.network

import com.seekho.anime.model.AnimeDto
import com.seekho.anime.model.TopAnimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JikanApi {
    @GET("top/anime")
    suspend fun getTopAnime(): Response<TopAnimeResponse>

    @GET("anime/{id}/full")
    suspend fun getAnimeDetails(@Path("id") id: Int): Response<AnimeDto>
}
