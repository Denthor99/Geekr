package com.example.geekr.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geekr.data.model.Actor
import com.example.geekr.data.model.cine.Movie
import com.example.geekr.data.model.cine.MovieCredits
import com.example.geekr.data.model.cine.MovieDetails
import com.example.geekr.data.network.RESTResponse
import com.example.geekr.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MovieRepository {
    // Obtenemos la lista de peliculas populares
    fun getPopularMovies(apiKey: String, page: Int = 1): LiveData<List<Movie>?>{
        val result = MutableLiveData<List<Movie>?>()
        RetrofitClient.tmdbApi.getPopularMovies(apiKey, page = page)
            .enqueue(object: Callback<RESTResponse<Movie>>{
                override fun onResponse(
                    call: Call<RESTResponse<Movie>?>,
                    response: Response<RESTResponse<Movie>?>
                ) {
                    if (response.isSuccessful){
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
                    call: Call<RESTResponse<Movie>?>,
                    t: Throwable
                ) {
                    result.value = null
                }

            })
        return result
    }

    // Obtenemos la lista de peliculas populares
    fun getBestRatedMovies(apiKey: String, page: Int = 1): LiveData<List<Movie>?>{
        val result = MutableLiveData<List<Movie>?>()
        RetrofitClient.tmdbApi.getBestRatedMovies(apiKey, page = page)
            .enqueue(object: Callback<RESTResponse<Movie>>{
                override fun onResponse(
                    call: Call<RESTResponse<Movie>?>,
                    response: Response<RESTResponse<Movie>?>
                ) {
                    if (response.isSuccessful){
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
                    call: Call<RESTResponse<Movie>?>,
                    t: Throwable
                ) {
                    result.value = null
                }

            })
        return result
    }

    // Obtenemos una pelicula segun su id
    fun getMovieDetails(apiKey: String, movieId: Int): LiveData<MovieDetails?> {
        val result = MutableLiveData<MovieDetails?>()

        // Intentamos primero en español
        RetrofitClient.tmdbApi.getMovieById(movieId, apiKey, "es-ES")
            .enqueue(object : Callback<MovieDetails> {
                override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                    if (response.isSuccessful && response.body() != null) {
                        val movie = response.body()
                        if (!movie?.overview.isNullOrBlank()) {
                            result.value = movie
                        } else {
                            // Si la descripción en español está vacía, intentamos en inglés
                            RetrofitClient.tmdbApi.getMovieById(movieId, apiKey, "en-US")
                                .enqueue(object : Callback<MovieDetails> {
                                    override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                                        if (response.isSuccessful) {
                                            result.value = response.body()
                                        }
                                    }

                                    override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                                        result.value = null
                                    }
                                })
                        }
                    } else {
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                    result.value = null
                }
            })

        return result
    }

    // Obtenemos una serie de resultados segun el nombre de lo que se esta buscando
    fun searchMovies(apiKey: String, query: String, page: Int = 1): LiveData<List<Movie>?> {
        val result = MutableLiveData<List<Movie>?>()

        RetrofitClient.tmdbApi.getMoviesByName(apiKey, query, page = page)
            .enqueue(object : Callback<RESTResponse<Movie>> {
            override fun onResponse(
                call: Call<RESTResponse<Movie>>,
                response: Response<RESTResponse<Movie>>
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
                    println("Error en la búsqueda de películas: ${response.code()} - ${response.message()}")
                    result.value = null
                }
            }

            override fun onFailure(call: Call<RESTResponse<Movie>>, t: Throwable) {
                println("Error de conexión: ${t.message}")
                result.value = null
            }
        })

        return result
    }
    // Obtenemos las peliculas recomendadas
    fun getMoviesRecommendationsById(apiKey: String, tvId: Int): LiveData<List<Movie>?>{
        val result = MutableLiveData<List<Movie>?>()

        RetrofitClient.tmdbApi.getMovieRecommendations(tvId,apiKey)
            .enqueue(object: Callback<RESTResponse<Movie>>{
                override fun onResponse(
                    call: Call<RESTResponse<Movie>?>,
                    response: Response<RESTResponse<Movie>?>
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
                        println("Error a la hora de mostrar peliculas recomendadas: ${response.code()} - ${response.message()}")
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<RESTResponse<Movie>>, t: Throwable) {
                    println("Error de conexión: ${t.message}")
                    result.value = null
                }

            })

        return result
    }

    // Obtenemos el nombre de los actores
    fun getMovieCredits(apiKey: String, movieId: Int): LiveData<List<Actor>?> {
        val result = MutableLiveData<List<Actor>?>()

        RetrofitClient.tmdbApi.getMovieCredits(movieId, apiKey)
            .enqueue(object : Callback<MovieCredits> {
                override fun onResponse(call: Call<MovieCredits>, response: Response<MovieCredits>) {
                    if (response.isSuccessful) {
                        result.value = response.body()?.cast
                    } else {
                        Log.e("ERROR", "Error al obtener créditos: ${response.code()} - ${response.message()}")
                        result.value = null
                    }
                }

                override fun onFailure(call: Call<MovieCredits>, t: Throwable) {
                    Log.e("ERROR", "Error de conexión al obtener créditos: ${t.message}")
                    result.value = null
                }
            })

        return result
    }

}