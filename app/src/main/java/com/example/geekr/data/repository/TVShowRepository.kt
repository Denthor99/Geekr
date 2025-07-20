package com.example.geekr.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geekr.data.model.Actor
import com.example.geekr.data.model.tv.TVCredits
import com.example.geekr.data.model.tv.TVShow
import com.example.geekr.data.model.tv.TVShowDetails
import com.example.geekr.data.network.RESTResponse
import com.example.geekr.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TVShowRepository {
    // Obtenemos las series mas populares
    fun getPopularTVShows(apiKey: String, page: Int = 1): LiveData<List<TVShow>?>{
        val result = MutableLiveData<List<TVShow>?>()
        RetrofitClient.tmdbApi.getPopularTVShows(apiKey, page = page)
            .enqueue(object: Callback<RESTResponse<TVShow>>{
                override fun onResponse(
                    call: Call<RESTResponse<TVShow>?>,
                    response: Response<RESTResponse<TVShow>?>
                ) {
                    if(response.isSuccessful){
                        val restResponse = response.body()
                        if(restResponse?.statusCode == null || restResponse.statusCode in 200..299){
                            result.value = restResponse?.results
                        } else {
                            result.value = null
                        }
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(
                    call: Call<RESTResponse<TVShow>?>,
                    t: Throwable
                ) {
                    result.value = null
                }

            })
        return result
    }

    fun getBestRatedTVShows(apiKey: String, page: Int = 1): LiveData<List<TVShow>?>{
        val result = MutableLiveData<List<TVShow>?>()
        RetrofitClient.tmdbApi.getBestRatedTVShows(apiKey, page = page)
            .enqueue(object: Callback<RESTResponse<TVShow>>{
                override fun onResponse(
                    call: Call<RESTResponse<TVShow>?>,
                    response: Response<RESTResponse<TVShow>?>
                ) {
                    if(response.isSuccessful){
                        val restResponse = response.body()
                        if(restResponse?.statusCode == null || restResponse.statusCode in 200..299){
                            result.value = restResponse?.results
                        } else {
                            result.value = null
                        }
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(
                    call: Call<RESTResponse<TVShow>?>,
                    t: Throwable
                ) {
                    result.value = null
                }

            })
        return result
    }

    // Obtener detalles de una serie segun su id
    fun getTvShowDetails(apiKey: String, tvShowId: Int): LiveData<TVShowDetails?> {
        val result = MutableLiveData<TVShowDetails?>()

        // Intentamos primero en español
        RetrofitClient.tmdbApi.getTVShowById(tvShowId, apiKey, "es-ES")
            .enqueue(object : Callback<TVShowDetails> {
                override fun onResponse(call: Call<TVShowDetails>, response: Response<TVShowDetails>) {
                    if (response.isSuccessful && response.body() != null) {
                        val tvDetails = response.body()
                        if (!tvDetails?.overview.isNullOrBlank()) {
                            result.value = tvDetails
                        } else {
                            // Si la descripción en español está vacía, intentamos en inglés
                            RetrofitClient.tmdbApi.getTVShowById(tvShowId, apiKey, "en-US")
                                .enqueue(object : Callback<TVShowDetails> {
                                    override fun onResponse(call: Call<TVShowDetails>, response: Response<TVShowDetails>) {
                                        if (response.isSuccessful) {
                                            result.value = response.body()
                                        }
                                    }

                                    override fun onFailure(call: Call<TVShowDetails>, t: Throwable) {
                                        result.value = null
                                    }
                                })
                        }
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<TVShowDetails>, t: Throwable) {
                    result.value = null
                }
            })

        return result
    }


    // Obtenemos una serie de resultados segun el nombre de lo que se esta buscando
    fun searchTVShows(apiKey: String, query: String, page: Int = 1): LiveData<List<TVShow>?> {
        val result = MutableLiveData<List<TVShow>?>()

        RetrofitClient.tmdbApi.getTVShowsByName(apiKey, query, page = page)
            .enqueue(object : Callback<RESTResponse<TVShow>> {
                override fun onResponse(
                    call: Call<RESTResponse<TVShow>>,
                    response: Response<RESTResponse<TVShow>>
                ) {
                    if (response.isSuccessful) {
                        val restResponse = response.body()
                        if (restResponse?.statusCode == null || restResponse.statusCode in 200..299) {
                            result.value = restResponse?.results
                        } else {
                            println("Error en la respuesta: ${restResponse.statusMessage}")
                            result.value = null
                        }
                    } else {
                        println("Error en la búsqueda de series de TV: ${response.code()} - ${response.message()}")
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<RESTResponse<TVShow>>, t: Throwable) {
                    println("Error de conexión: ${t.message}")
                    result.value = null
                }
            })

        return result
    }

    // Obtenemos las recomendaciones de una serie
    fun getTVShowsRecommendationsById(apiKey: String, tvId: Int): LiveData<List<TVShow>?>{
        val result = MutableLiveData<List<TVShow>?>()

        RetrofitClient.tmdbApi.getTVRecommendations(tvId,apiKey)
            .enqueue(object: Callback<RESTResponse<TVShow>>{
                override fun onResponse(
                    call: Call<RESTResponse<TVShow>?>,
                    response: Response<RESTResponse<TVShow>?>
                ) {
                    if (response.isSuccessful) {
                        val restResponse = response.body()
                        if (restResponse?.statusCode == null || restResponse.statusCode in 200..299) {
                            result.value = restResponse?.results
                        } else {
                            println("Error en la respuesta: ${restResponse.statusMessage}")
                            result.value = null
                        }
                    } else {
                        println("Error a la hora de mostrar series recomendadas: ${response.code()} - ${response.message()}")
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<RESTResponse<TVShow>>, t: Throwable) {
                    println("Error de conexión: ${t.message}")
                    result.value = null
                }

            })

        return result
    }
    // Obtenemos los actores de una serie
    fun getTVShowCredits(apiKey: String, tvId: Int): LiveData<List<Actor>?> {
        val result = MutableLiveData<List<Actor>?>()

        RetrofitClient.tmdbApi.getTVShowCredits(tvId, apiKey)
            .enqueue(object : Callback<TVCredits> {
                override fun onResponse(call: Call<TVCredits>, response: Response<TVCredits>) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.cast
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<TVCredits>, t: Throwable) {
                    result.value = null
                }
            })
        return result
    }

}