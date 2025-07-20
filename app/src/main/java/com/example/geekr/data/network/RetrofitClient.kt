package com.example.geekr.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    private val anilistRetrofit = Retrofit.Builder()
        .baseUrl("https://graphql.anilist.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val aniListApi: AniListApi = anilistRetrofit.create(AniListApi::class.java)

    private val tmdbRetrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val tmdbApi: TMDBApi = tmdbRetrofit.create(TMDBApi::class.java)
}