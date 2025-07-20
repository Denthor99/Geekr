package com.example.geekr.data.model.cine
import com.example.geekr.data.model.Genre
data class MovieDetails(
    val id: Int,
    val title: String,
    val original_title: String,
    val overview: String,
    val tagline: String?,
    val release_date: String,
    val genres: List<Genre>,
    val runtime: Int?,
    val status: String,
    val budget: Int?,
    val revenue: Int?,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int,
    val poster_path: String?,
    val backdrop_path: String?,
    val homepage: String?,
    val imdb_id: String?,
    val origin_country: List<String>,
    val original_language: String,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val spoken_languages: List<SpokenLanguage>
)