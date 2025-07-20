package com.example.geekr.data.network

data class RESTRequest(
    val endpoint: String,
    val params: Map<String, String> = emptyMap()
)
