package com.example.geekr.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geekr.data.model.manga.MangaMedia
import com.example.geekr.data.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MangaViewModel : ViewModel() {
    private val repository = MangaRepository()
    private val _mangaList = MutableStateFlow<List<MangaMedia>>(emptyList())
    private val _popularMangas = MutableStateFlow<List<MangaMedia>>(emptyList())
    private val _bestMangas = MutableStateFlow<List<MangaMedia>>(emptyList())
    val mangaList: StateFlow<List<MangaMedia>> get() = _mangaList
    val popularMangas: StateFlow<List<MangaMedia>> get() = _popularMangas
    val bestMangas: StateFlow<List<MangaMedia>> get() = _bestMangas

    fun searchManga(name: String){
        viewModelScope.launch {
            repository.getMangaByName(name).observeForever { mediaList ->
                _mangaList.value = mediaList?:emptyList()
            }
        }
    }

    fun getMangaDetails(id: Int): LiveData<MangaMedia?>{
        return repository.getMangaById(id)
    }

    // Metodos para cargar datos de mangas en carruseles
    fun loadPopularMangas(){
        viewModelScope.launch {
            repository.getPopularMangas().observeForever { mediaList ->
                _popularMangas.value = mediaList ?: emptyList()
            }
        }
    }

    fun loadBestMangas(){
        viewModelScope.launch {
            repository.getBestMangas().observeForever { mediaList ->
                _bestMangas.value = mediaList ?: emptyList()
            }
        }
    }
}