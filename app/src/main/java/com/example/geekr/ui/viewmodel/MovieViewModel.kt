package com.example.geekr.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekr.data.model.Actor
import com.example.geekr.data.model.cine.Movie
import com.example.geekr.data.model.cine.MovieDetails
import com.example.geekr.data.repository.GenericRepository
import com.example.geekr.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel:ViewModel() {
    private val repository = MovieRepository()
    private val genericRepository = GenericRepository()
    private val _moviesList = MutableStateFlow<List<Movie>>(emptyList())
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val _bestRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val _recommendationsMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val _movieCredits = MutableStateFlow<List<Actor>>(emptyList())
    val moviesList: StateFlow<List<Movie>> get() = _moviesList
    val popularMovies: StateFlow<List<Movie>> get() = _popularMovies
    val bestRatedMovies: StateFlow<List<Movie>> get() = _bestRatedMovies
    val recommendationsMovies: StateFlow<List<Movie>> get() = _recommendationsMovies
    val movieCredits: StateFlow<List<Actor>> get() = _movieCredits

    fun loadPopularMovies(apiKey: String){
        viewModelScope.launch {
            repository.getPopularMovies(apiKey).observeForever { mediaList ->
                _popularMovies.value = mediaList?:emptyList()
            }
        }
    }
    fun searchMovie(apiKey: String, name: String){
        viewModelScope.launch {
            repository.searchMovies(apiKey, name).observeForever { mediaList ->
                _moviesList.value = mediaList?:emptyList()
            }
        }
    }
    fun getMovieDetails(apiKey: String, id: Int): LiveData<MovieDetails?>{
        return repository.getMovieDetails(apiKey, id)
    }

    fun loadBestRatedMovies(apiKey: String){
        viewModelScope.launch {
            repository.getBestRatedMovies(apiKey).observeForever { mediaList ->
                _bestRatedMovies.value = mediaList?:emptyList()
            }
        }
    }

    fun loadRecommendationsMovies(apiKey: String, movieId: Int){
        viewModelScope.launch {
            repository.getMoviesRecommendationsById(apiKey,movieId).observeForever { mediaList ->
                _recommendationsMovies.value = mediaList?:emptyList()
            }
        }
    }

    fun loadMovieCredits(apiKey: String, movieId: Int){
        viewModelScope.launch {
            repository.getMovieCredits(apiKey, movieId).observeForever { mediaList ->
                _movieCredits.value = mediaList?:emptyList()
            }
        }
    }

    fun getMovieTrailerUrl(apiKey: String, movieId: Int): LiveData<String?> {
        return genericRepository.getTrailerUrl(apiKey, movieId, true)
    }
}