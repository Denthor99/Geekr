package com.example.geekr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.geekr.data.model.anime.AnimeMedia
import com.example.geekr.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimeViewModel : ViewModel() {
    private val repository = AnimeRepository()
    private val _animeList = MutableStateFlow<List<AnimeMedia>>(emptyList())
    private val _popularAnimes = MutableStateFlow<List<AnimeMedia>>(emptyList())
    private val _bestRatedAnimes = MutableStateFlow<List<AnimeMedia>>(emptyList())
    val animeList: StateFlow<List<AnimeMedia>> get() = _animeList
    val popularAnimes: StateFlow<List<AnimeMedia>> get() = _popularAnimes
    val bestRatedAnimes: StateFlow<List<AnimeMedia>> get() = _bestRatedAnimes

    fun searchAnime(name: String) {
        viewModelScope.launch {
            repository.getAnimeByName(name).observeForever { mediaList ->
                _animeList.value = mediaList ?: emptyList()
            }
        }
    }
    fun getAnimeDetails(id: Int): LiveData<AnimeMedia?>{
        return repository.getAnimeById(id)
    }


    // Métodos para cargar datos de anime en los carruseles
    fun loadPopularAnimes() {
        viewModelScope.launch {
            repository.getPopularAnimes().observeForever { mediaList ->
                _popularAnimes.value = mediaList ?: emptyList()
            }
        }
    }

    fun loadBestAnimes() {
        viewModelScope.launch {
            repository.getBestAnimes().observeForever { mediaList ->
                _bestRatedAnimes.value = mediaList ?: emptyList()
            }
        }
    }
}
