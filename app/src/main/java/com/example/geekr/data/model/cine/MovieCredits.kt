package com.example.geekr.data.model.cine

import com.example.geekr.data.model.Actor

data class MovieCredits(
    val id: Int,
    val cast: List<Actor>
)