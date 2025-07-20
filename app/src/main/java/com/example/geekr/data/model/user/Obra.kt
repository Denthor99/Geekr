package com.example.geekr.data.model.user

import com.google.firebase.Timestamp

data class Obra(
    val id: String = "",
    val titulo: String = "",
    val rutaImagen: String = "",
    val estado: String = "",
    val tipo: String = "",
    val episodiosVistos: Int = 0,
    val totalEpisodios: Int? = null,
    val fechaUltimaActualizacion: Timestamp = Timestamp.now()
)