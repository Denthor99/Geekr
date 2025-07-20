package com.example.geekr.data.model.user

import com.google.firebase.Timestamp

data class Comentario(
    val uid: String = "",
    val texto: String = "",
    val estadoObra: String = "En proceso",
    val valoracion: Int = 1,
    val fechaComentario: Timestamp = Timestamp.now(),
)