package com.seekho.anime.di


import com.seekho.anime.network.JikanApi
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitModule {
    private val moshi = Moshi.Builder().build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val api: JikanApi = Retrofit.Builder()
        .baseUrl("https://api.jikan.moe/v4/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(JikanApi::class.java)
}
