package com.example.geekr.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geekr.data.model.manga.MangaData
import com.example.geekr.data.model.manga.MangaDetailData
import com.example.geekr.data.model.manga.MangaMedia
import com.example.geekr.data.network.GraphQLRequest
import com.example.geekr.data.network.GraphQLResponse
import com.example.geekr.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MangaRepository {

    // Metodo para obtener mangas por nombre
    fun getMangaByName(name: String): LiveData<List<MangaMedia>?> {
        val result = MutableLiveData<List<MangaMedia>?>()
        val query = """
            query(${'$'}search: String) {
              Page {
                media(type: MANGA, search: ${'$'}search) {
                  id
                  title {
                    romaji
                    english
                    native
                  }
                  coverImage {
                    large
                  }
                  chapters
                  volumes
                  status
                }
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(query, mapOf("search" to name))

        RetrofitClient.aniListApi.getMangaByName(request)
            .enqueue(object : Callback<GraphQLResponse<MangaData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<MangaData>>,
                    response: Response<GraphQLResponse<MangaData>>
                ) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.data?.Page?.media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<MangaData>>, t: Throwable) {
                    result.value = null
                }
            })
        return result
    }

    // Metodo para obtener detalles de un manga por su ID
    fun getMangaById(id: Int): LiveData<MangaMedia?> {
        val result = MutableLiveData<MangaMedia?>()

        val query = """
    query (${'$'}id: Int!) {
      Media(id: ${'$'}id, type: MANGA) {
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
        chapters
        volumes
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

        RetrofitClient.aniListApi.getMangaById(request)
            .enqueue(object : Callback<GraphQLResponse<MangaDetailData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<MangaDetailData>>,
                    response: Response<GraphQLResponse<MangaDetailData>>
                ) {
                    if (response.isSuccessful) {
                        val media = response.body()?.data?.Media
                        result.value = media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<MangaDetailData>>, t: Throwable) {
                    result.value = null
                }
            })

        return result
    }

    fun getPopularMangas(): LiveData<List<MangaMedia>?> {
        val result = MutableLiveData<List<MangaMedia>?>()
        val query = """
        query {
          Page(perPage: 20) {
            media(type: MANGA, sort: POPULARITY_DESC) {
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

        RetrofitClient.aniListApi.getMangasByQuery(request)
            .enqueue(object : Callback<GraphQLResponse<MangaData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<MangaData>>,
                    response: Response<GraphQLResponse<MangaData>>
                ) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.data?.Page?.media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<MangaData>>, t: Throwable) {
                    result.value = null
                }
            })
        return result
    }

    // Metodo para obtener los mangas mejor valorados
    fun getBestMangas(): LiveData<List<MangaMedia>?> {
        val result = MutableLiveData<List<MangaMedia>?>()
        val query = """
        query {
          Page(perPage: 20) {
            media(type: MANGA, sort: SCORE_DESC) {
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

        RetrofitClient.aniListApi.getMangasByQuery(request)
            .enqueue(object : Callback<GraphQLResponse<MangaData>> {
                override fun onResponse(
                    call: Call<GraphQLResponse<MangaData>>,
                    response: Response<GraphQLResponse<MangaData>>
                ) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.data?.Page?.media
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<GraphQLResponse<MangaData>>, t: Throwable) {
                    result.value = null
                }
            })
        return result
    }
}