package com.example.geekr.data.model.user

import com.google.firebase.Timestamp

data class Favorito(
    val id: String = "",
    val titulo: String = "",
    val tipo: String = "",
    val rutaImagen: String = "",
    val fechaFavorito: Timestamp = Timestamp.now()
)
