package com.example.geekr.data.network

import com.example.geekr.data.model.anime.AnimeData
import com.example.geekr.data.model.anime.AnimeDetailData
import com.example.geekr.data.model.manga.MangaData
import com.example.geekr.data.model.manga.MangaDetailData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AniListApi {

    // Metodo para obtener animes por un nombre (usando GraphQLRequest como cuerpo)
    @POST("https://graphql.anilist.co/")
    fun getAnimeByName(@Body request: GraphQLRequest): Call<GraphQLResponse<AnimeData>>

    // Metodo para obtener anime por ID
    @POST("https://graphql.anilist.co/")
    fun getAnimeById(@Body request: GraphQLRequest): Call<GraphQLResponse<AnimeDetailData>>

    // Metodo para obtener mangas por un nombre
    @POST("https://graphql.anilist.co/")
    fun getMangaByName(@Body request: GraphQLRequest): Call<GraphQLResponse<MangaData>>

    // Metodo para obtener manga por ID
    @POST("https://graphql.anilist.co/")
    fun getMangaById(@Body request: GraphQLRequest): Call<GraphQLResponse<MangaDetailData>>

    // Metodo generico para las queries de anime
    @POST("https://graphql.anilist.co/")
    fun getAnimesByQuery(@Body request: GraphQLRequest): Call<GraphQLResponse<AnimeData>>

    // Metodo generico para las queries de manga
    @POST("https://graphql.anilist.co/")
    fun getMangasByQuery(@Body request: GraphQLRequest): Call<GraphQLResponse<MangaData>>
}
