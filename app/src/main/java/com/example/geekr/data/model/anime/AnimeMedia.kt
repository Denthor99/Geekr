package com.example.geekr.data.model.anime

import com.example.geekr.data.model.CoverImage
import com.example.geekr.data.model.FuzzyDate
import com.example.geekr.data.model.Title

data class AnimeMedia(
    val id: Int,
    val title: Title,
    val coverImage: CoverImage,
    val episodes: Int?,
    val status: String,
    val description: String?,
    val genres: List<String>?,
    val averageScore: Int?,
    val startDate: FuzzyDate?,
    val endDate: FuzzyDate?,
    val studios: StudioConnection?,
    val recommendations: Recommendations?
)