package com.example.geekr.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import com.example.geekr.data.model.user.Biblioteca
import com.example.geekr.data.model.user.Comentario
import com.example.geekr.data.model.user.Favorito
import com.example.geekr.data.model.user.Obra
import com.example.geekr.data.model.user.UserData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

object auxFunctions {
    private val db = FirebaseFirestore.getInstance()

    fun formatDate(date: String?): String{
        var result = ""
        if(!date.isNullOrBlank()){
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parseoFecha = inputFormat.parse(date)
            result = outputFormat.format(parseoFecha!!)
        } else {
            result = "Fecha de estreno desconocida"
        }
        return result
    }
    fun agregarFavorito(uidUser: String, favorito: Favorito, onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        val favoritoConFecha = favorito.copy(fechaFavorito = Timestamp.now())

        db.collection("favoritos").document(uidUser)
            .collection(favorito.tipo).document(favorito.id)
            .set(favoritoConFecha)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e-> onFailure(e) }
    }
    fun eliminarFavorito(uidUser: String, tipo: String, favoritoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("favoritos").document(uidUser)
            .collection(tipo).document(favoritoId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun verificarFavorito(uidUser: String, tipo: String, id: String, onResult: (Boolean)->Unit){
        db.collection("favoritos").document(uidUser)
            .collection(tipo).document(id).get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener { e->
                println("Error al comprobar favorito: $e")
                onResult(false)
            }
    }
    fun obtenerFavoritos(uid: String, orden: String, tipo: String, onResult: (List<Favorito>) -> Unit) {
        val favoritos = mutableListOf<Favorito>()
        val colecciones = if (tipo == "todos") listOf("peliculas", "seriesTV", "animes", "mangas") else listOf(tipo)
        var consultasCompletadas = 0

        colecciones.forEach { coleccion ->
            db.collection("favoritos").document(uid)
                .collection(coleccion)
                .get()
                .addOnSuccessListener { snapshot ->
                    favoritos.addAll(snapshot.toObjects(Favorito::class.java))

                    consultasCompletadas++
                    if (consultasCompletadas == colecciones.size) {
                        val favoritosOrdenados = when (orden) {
                            "fechaDesc" -> favoritos.sortedByDescending { it.fechaFavorito }
                            "fechaAsc" -> favoritos.sortedBy { it.fechaFavorito }
                            else -> favoritos
                        }
                        onResult(favoritosOrdenados)
                    }
                }
                .addOnFailureListener { e ->
                    println("Error obteniendo favoritos de $coleccion: $e")
                    consultasCompletadas++
                    if (consultasCompletadas == colecciones.size) {
                        onResult(favoritos)
                    }
                }
        }
    }
    // Convierte el Uri de imagen a String Base64
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)  // Ajusta calidad si quieres
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Función comprimir imagen (Firestore me tienes loco deja de petar)
    fun compressImageToBase64(context: Context, uri: Uri, quality: Int = 30, maxSizeInMB: Int = 5): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBytes = inputStream?.readBytes()
            inputStream?.close()

            val maxBytes = maxSizeInMB * 1024 * 1024
            if (originalBytes != null && originalBytes.size > maxBytes) {
                return null
            }

            val bitmap = originalBytes?.let { BitmapFactory.decodeByteArray(originalBytes, 0, it.size) }
            val outputStream = ByteArrayOutputStream()
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            val compressedBytes = outputStream.toByteArray()

            Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Función para subir la imagen Base64 a Firestore
    fun subirImagenPerfil(base64: String, userId: String, onResult: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val data = mapOf("photoBase64" to base64)

        db.collection("users")
            .document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { onResult(base64) }
            .addOnFailureListener { onResult(null) }
    }

    // Guardar comentario
    fun guardarComentario(tipoContenido: String, contenidoId: Int, comentario: Comentario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comentarios").document(tipoContenido)
            .collection(contenidoId.toString())
            .add(comentario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // Obtener comentarios
    fun obtenerComentarios(tipoContenido: String, contenidoId: Int, onResult: (List<Comentario>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comentarios").document(tipoContenido)
            .collection(contenidoId.toString())
            .orderBy("fechaComentario", Query.Direction.DESCENDING) // Orden por fecha
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.documents?.map { it.toObject(Comentario::class.java)!! } ?: emptyList()
                onResult(lista)
            }
    }
    fun obtenerUsuario(uid: String, callback: (UserData) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("name") ?: "Usuario"
                val imagen = doc.getString("photoBase64")
                callback(UserData(nombre, imagen))
            }
            .addOnFailureListener {
                callback(UserData("Usuario", null))
            }
    }

    fun obtenerImagenUsuario(uid: String, onResult: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val imagenBase64 = document.getString("photoBase64") // Asegúrate de que este campo existe en Firestore
                onResult(imagenBase64)
            }
            .addOnFailureListener {
                onResult(null) // Manejo de error si el usuario no tiene imagen
            }
    }
    fun agregarObra(uid: String, tipo: String, obra: Obra, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val episodiosFinal = when (obra.estado.lowercase()) {
            "completado" -> obra.totalEpisodios ?: 0
            "pendiente" -> 0
            else -> obra.episodiosVistos
        }

        val obraFinal = obra.copy(episodiosVistos = episodiosFinal, fechaUltimaActualizacion = Timestamp.now())

        db.collection("bibliotecas").document(uid)
            .collection(tipo).document(obraFinal.id)
            .set(obraFinal, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun obtenerBiblioteca(uid: String, onResult: (List<Obra>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val tipos = listOf("mangas", "animes", "peliculas", "seriesTV")
        val bibliotecaCompleta = mutableListOf<Obra>()
        var consultasCompletadas = 0

        tipos.forEach { tipo ->
            db.collection("bibliotecas").document(uid)
                .collection(tipo).get()
                .addOnSuccessListener { snapshot ->
                    bibliotecaCompleta.addAll(snapshot.toObjects(Obra::class.java))
                    consultasCompletadas++
                    if(consultasCompletadas == tipos.size){
                        onResult(bibliotecaCompleta.sortedByDescending { it.fechaUltimaActualizacion })
                    }
                }
                .addOnFailureListener {
                    consultasCompletadas++
                    if(consultasCompletadas == tipos.size){
                        onResult(bibliotecaCompleta)
                    }
                }
        }
    }
    fun actualizarEpisodiosVistos(uid: String, tipo: String, obraId: String, nuevosEpisodiosVistos: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bibliotecas").document(uid)
            .collection(tipo).document(obraId)
            .update("episodiosVistos", nuevosEpisodiosVistos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }



    suspend fun animarInput(shakeOffset: Animatable<Float, AnimationVector1D>){
        shakeOffset.animateTo(
            targetValue = 8f,
            animationSpec = repeatable(
                iterations = 4, // Repetir 4 veces
                animation = tween(durationMillis = 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse // Movimiento de ida y vuelta
            )
        )
        shakeOffset.animateTo(0f)
    }
}