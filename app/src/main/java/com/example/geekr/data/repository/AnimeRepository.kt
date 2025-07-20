package com.example.geekr.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geekr.data.model.anime.AnimeData
import com.example.geekr.data.model.anime.AnimeDetailData
import com.example.geekr.data.model.anime.AnimeMedia
import com.example.geekr.data.network.GraphQLRequest
import com.example.geekr.data.network.GraphQLResponse
import com.example.geekr.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnimeRepository {

    // Metodo para obtener animes por nombre
    fun getAnimeByName(name: String): LiveData<List<AnimeMedia>?> {
        val result = MutableLiveData<List<AnimeMedia>?>()
        val query = """
            query(${'$'}search: String) {
              Page {
                media(type: ANIME, search: ${'$'}search) {
                  id
                  title {
                    romaji
                    english
                  }
                  coverImage {
                    large
                  }
                  episodes
                  status
                }
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(query, mapOf("search" to name))

        RetrofitClient.aniListApi.getAnimeByName(request)
            .enqueue(object : Callback<GraphQLResponse<AnimeData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<AnimeData>>,
                    response: Response<GraphQLResponse<AnimeData>>
                ) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.data?.Page?.media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<AnimeData>>, t: Throwable) {
                    result.value = null
                }
            })
        return result
    }

    // Méeodo para obtener detalles de un anime por su ID
    fun getAnimeById(id: Int): LiveData<AnimeMedia?> {
        val result = MutableLiveData<AnimeMedia?>()

        val query = """
    query (${'$'}id: Int!) {
      Media(id: ${'$'}id, type: ANIME) {
        id
        title {
          romaji
          english
          native
        }
        coverImage {
          large
          medium
        }
        episodes
        status
        description
        genres
        averageScore
        startDate {
          year
          month
          day
        }
        endDate {
          year
          month
          day
        }
        studios {
          nodes {
            name
          }
        }
        recommendations(perPage: 10) {
          nodes {
            mediaRecommendation {
              id
              title {
                romaji
                english
                native
              }
              coverImage {
                medium
              }
            }
          }
        }
      }
    }
""".trimIndent()


        val request = GraphQLRequest(query, mapOf("id" to id))

        RetrofitClient.aniListApi.getAnimeById(request)
            .enqueue(object : Callback<GraphQLResponse<AnimeDetailData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<AnimeDetailData>>,
                    response: Response<GraphQLResponse<AnimeDetailData>>
                ) {
                    if (response.isSuccessful) {
                        val media = response.body()?.data?.Media
                        result.value = media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<AnimeDetailData>>, t: Throwable) {
                    result.value = null
                }
            })

        return result
    }
    // Metodo para obtener los animes mas populares
    fun getPopularAnimes(): LiveData<List<AnimeMedia>?> {
        val result = MutableLiveData<List<AnimeMedia>?>()
        val query = """
        query {
          Page(perPage: 20) {
            media(type: ANIME, sort: POPULARITY_DESC) {
              id
              coverImage {
                large
                medium
              }
            }
          }
        }
    """.trimIndent()

        val request = GraphQLRequest(query)

        RetrofitClient.aniListApi.getAnimesByQuery(request).enqueue(object : Callback<GraphQLResponse<AnimeData>> {
            override fun onResponse(
                call: Call<GraphQLResponse<AnimeData>>,
                response: Response<GraphQLResponse<AnimeData>>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()?.data?.Page?.media
                } else {
                    result.value = null
                }
            }

            override fun onFailure(call: Call<GraphQLResponse<AnimeData>>, t: Throwable) {
                result.value = null
            }
        })
        return result
    }

    // Metodo para obtener los animes mejor valorados
    fun getBestAnimes(): LiveData<List<AnimeMedia>?> {
        val result = MutableLiveData<List<AnimeMedia>?>()
        val query = """
        query {
            Page(perPage: 20) {
            media(type: ANIME, sort: SCORE_DESC) {
                id
                coverImage {
                    large
                    medium
                }
            }
        }
    }
    """.trimIndent()

        val request = GraphQLRequest(query)

        RetrofitClient.aniListApi.getAnimesByQuery(request).enqueue(object : Callback<GraphQLResponse<AnimeData>> {
            override fun onResponse(
                call: Call<GraphQLResponse<AnimeData>>,
                response: Response<GraphQLResponse<AnimeData>>
            ) {
                if (response.isSuccessful) {
                    result.value = response.body()?.data?.Page?.media
                } else {
                    result.value = null
                }
            }

            override fun onFailure(call: Call<GraphQLResponse<AnimeData>>, t: Throwable) {
                result.value = null
            }
        })
        return result
    }
}