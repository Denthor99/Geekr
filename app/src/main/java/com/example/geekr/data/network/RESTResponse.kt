package com.example.geekr.data.network

data class RESTResponse<T>(
    val results: List<T>,
    val statusCode: Int?,
    val statusMessage: String?
)
