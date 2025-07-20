package com.example.geekr.data.model.user

data class Biblioteca(
    val mangas: MutableList<Obra> = mutableListOf(),
    val animes: MutableList<Obra> = mutableListOf(),
    val series: MutableList<Obra> = mutableListOf(),
    val peliculas: MutableList<Obra> = mutableListOf()
)