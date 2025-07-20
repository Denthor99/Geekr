package com.example.geekr.data.network

import com.example.geekr.data.model.VideoList
import com.example.geekr.data.model.cine.Movie
import com.example.geekr.data.model.cine.MovieCredits
import com.example.geekr.data.model.cine.MovieDetails
import com.example.geekr.data.model.tv.TVCredits
import com.example.geekr.data.model.tv.TVShow
import com.example.geekr.data.model.tv.TVShowDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<Movie>>
    @GET("movie/{movie_id}")
    fun getMovieById(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<MovieDetails>
    @GET("tv/popular")
    fun getPopularTVShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<TVShow>>
    @GET("tv/{tv_id}")
    fun getTVShowById(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<TVShowDetails>
    @GET("search/movie")
    fun getMoviesByName(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): Call<RESTResponse<Movie>>
    @GET("search/tv")
    fun getTVShowsByName(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): Call<RESTResponse<TVShow>>
    @GET("movie/top_rated")
    fun getBestRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<Movie>>
    @GET("tv/top_rated")
    fun getBestRatedTVShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<TVShow>>
    @GET("movie/{movie_id}/recommendations")
    fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<Movie>>
    @GET("tv/{tv_id}/recommendations")
    fun getTVRecommendations(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<RESTResponse<TVShow>>
    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieCredits>
    @GET("tv/{tv_id}/credits")
    fun getTVShowCredits(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Call<TVCredits>
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<VideoList>
    @GET("tv/{tv_id}/videos")
    fun getTVShowVideos(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Call<VideoList>
}