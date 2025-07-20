package com.example.geekr.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geekr.data.model.VideoList
import com.example.geekr.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenericRepository {
    fun getTrailerUrl(apiKey: String, id: Int, isMovie: Boolean): LiveData<String?> {
        val result = MutableLiveData<String?>()

        val call = if (isMovie) {
            RetrofitClient.tmdbApi.getMovieVideos(id, apiKey)
        } else {
            RetrofitClient.tmdbApi.getTVShowVideos(id, apiKey)
        }

        call.enqueue(object : Callback<VideoList> {
            override fun onResponse(call: Call<VideoList>, response: Response<VideoList>) {
                if (response.isSuccessful) {
                    val videos = response.body()?.results
                    val trailer = videos?.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
                    result.value = trailer?.let { "https://www.youtube.com/watch?v=${it.key}" }
                } else {
                    result.value = null
                }
            }

            override fun onFailure(call: Call<VideoList>, t: Throwable) {
                result.value = null
            }
        })

        return result
    }
}