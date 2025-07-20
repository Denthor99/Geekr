package com.example.geekr.data.network

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any> = emptyMap()
)