package com.example.geekr.data.model.manga

import com.example.geekr.data.model.CoverImage
import com.example.geekr.data.model.FuzzyDate
import com.example.geekr.data.model.Title

data class MangaMedia(val id: Int,
                      val title: Title,
                      val coverImage: CoverImage,
                      val chapters: Int?,
                      val volumes: Int?,
                      val status: String,
                      val description: String?,
                      val genres: List<String>?,
                      val averageScore: Int?,
                      val startDate: FuzzyDate?,
                      val endDate: FuzzyDate?,
                      val recommendations: Recommendations
)
