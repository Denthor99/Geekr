package com.example.geekr.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekr.data.model.Actor
import com.example.geekr.data.model.tv.TVShow
import com.example.geekr.data.model.tv.TVShowDetails
import com.example.geekr.data.repository.GenericRepository
import com.example.geekr.data.repository.TVShowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TVShowViewModel: ViewModel() {
    private val repository = TVShowRepository()
    private val genericRepository = GenericRepository()
    private val _tvShowList = MutableStateFlow<List<TVShow>>(emptyList())
    private val _popularTVShows = MutableStateFlow<List<TVShow>>(emptyList())
    private val _bestRatedTVShows = MutableStateFlow<List<TVShow>>(emptyList())
    private val _recommendationTVShows = MutableStateFlow<List<TVShow>>(emptyList())
    private val _tvShowCredits = MutableStateFlow<List<Actor>>(emptyList())

    val popularTVShows: StateFlow<List<TVShow>> get() = _popularTVShows
    val tvShowList: StateFlow<List<TVShow>> get() = _tvShowList
    val bestRatedTVShow: StateFlow<List<TVShow>> get() = _bestRatedTVShows
    val recommendationTVShows: StateFlow<List<TVShow>> get() = _recommendationTVShows
    val tvShowCredits: StateFlow<List<Actor>> get() = _tvShowCredits

    fun loadPopularTVShow(apiKey: String){
        viewModelScope.launch {
            repository.getPopularTVShows(apiKey).observeForever { mediaList ->
                _popularTVShows.value = mediaList?:emptyList()
            }
        }
    }

    fun searchTVShow(apiKey: String, name: String){
        viewModelScope.launch {
            repository.searchTVShows(apiKey, name).observeForever { mediaList ->
                _tvShowList.value = mediaList?:emptyList()
            }
        }
    }
    fun getTVShowDetails(apiKey: String, id: Int): LiveData<TVShowDetails?>{
        return repository.getTvShowDetails(apiKey, id)
    }

    fun loadBestRatedTVShows(apiKey: String){
        viewModelScope.launch {
            repository.getBestRatedTVShows(apiKey).observeForever { mediaList ->
                _bestRatedTVShows.value = mediaList?:emptyList()
            }
        }
    }

    fun loadRecommendationsTVShows(apiKey: String, tvShowId: Int){
        viewModelScope.launch {
            repository.getTVShowsRecommendationsById(apiKey, tvShowId).observeForever { mediaList ->
                _recommendationTVShows.value = mediaList?:emptyList()
            }
        }
    }

    fun loadTVShowCredits(apiKey: String, tvShowId: Int){
        viewModelScope.launch {
            repository.getTVShowCredits(apiKey, tvShowId).observeForever { mediaList ->
                _tvShowCredits.value = mediaList?:emptyList()
            }
        }
    }

    fun getTVShowTrailerUrl(apiKey: String, tvShowId: Int): LiveData<String?> {
        return genericRepository.getTrailerUrl(apiKey, tvShowId, false)
    }

}