package com.example.geekr.data.model.tv

import com.example.geekr.data.model.Genre

data class TVShowDetails(
    val id: Int,
    val name: String,
    val original_name: String,
    val overview: String?,
    val tagline: String?,
    val first_air_date: String?,
    val last_air_date: String?,
    val status: String,
    val number_of_seasons: Int?,
    val number_of_episodes: Int?,
    val genres: List<Genre>,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int,
    val homepage: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val origin_country: List<String>,
    val original_language: String,
    val networks: List<Network>,
    val production_companies: List<ProductionCompany>,
    val created_by: List<Creator>,
    val spoken_languages: List<SpokenLanguage>
)
