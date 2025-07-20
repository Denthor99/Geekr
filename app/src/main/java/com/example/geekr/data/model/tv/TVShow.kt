package com.example.geekr.data.model.tv

data class TVShow(
    val id: Int,
    val name: String,
    val overview: String?,
    val poster_path: String?,
    val vote_average: String?,
    val first_air_date: String?
)
