package com.example.geekr.ui.components

import android.graphics.ColorSpace
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geekr.data.model.user.Comentario
import com.example.geekr.data.model.user.UserData
import com.example.geekr.shared.auxFunctions.guardarComentario
import com.example.geekr.shared.auxFunctions.obtenerComentarios
import com.example.geekr.shared.auxFunctions.obtenerImagenUsuario
import com.example.geekr.shared.auxFunctions.obtenerUsuario
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun SeccionComentarios(tipoContenido: String, contenidoId: Int) {
    val comentarios = remember { mutableStateListOf<Comentario>() }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(contenidoId) {
        obtenerComentarios(tipoContenido, contenidoId) { comentarios.clear(); comentarios.addAll(it) }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("💬 Comentarios", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { mostrarDialogo = true }, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Añadir comentario")
        }

        if (mostrarDialogo) {
            ComentarioDialog(tipoContenido, contenidoId) { mostrarDialogo = false }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            comentarios.forEach { comentario ->
                ComentarioItem(comentario)
            }
        }

    }
}

@Composable
fun ComentarioDialog(tipoContenido: String, contenidoId: Int, onDismiss: () -> Unit) {
    var nuevoComentario by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("En proceso") }
    var valoracionSeleccionada by remember { mutableStateOf(0) }
    var expandedEstado by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    if (nuevoComentario.isNotBlank()) {
                        val comentario = Comentario(
                            texto = nuevoComentario,
                            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            estadoObra = estadoSeleccionado,
                            valoracion = valoracionSeleccionada
                        )
                        guardarComentario(tipoContenido, contenidoId, comentario, onSuccess = {
                            nuevoComentario = ""
                            onDismiss()
                        }, onFailure = { e ->
                            println("Error al guardar comentario: $e")
                        })
                    }
                }
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        },
        title = { Text("Añadir comentario") },
        text = {
            Column {
                TextField(
                    value = nuevoComentario,
                    onValueChange = { nuevoComentario = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    placeholder = { Text("Escribe un comentario...") }
                )

                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Estado:")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { expandedEstado = !expandedEstado }) {
                        Text(estadoSeleccionado)
                    }
                    DropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {
                        listOf("Completado", "En proceso", "Dropeado").forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado) },
                                onClick = {
                                    estadoSeleccionado = estado
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Evaluacion:")
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Spacer(modifier = Modifier.width(8.dp))
                    repeat(5) { index ->
                        IconButton(onClick = { valoracionSeleccionada = index + 1 }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                tint = if (index < valoracionSeleccionada) Color.Yellow else Color.Gray,
                                contentDescription = "Estrella ${index + 1}"
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ComentarioItem(comentario: Comentario) {
    var datosUser by remember { mutableStateOf<UserData?>(null) }

    LaunchedEffect(comentario.uid) {
        obtenerUsuario(comentario.uid) { usuario ->
            datosUser = usuario
        }    }


    // Extraemos los datos del usuario
    val nombre = datosUser?.nombre?:"Usuario"
    val imagen = datosUser?.imagenBase64

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3748))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            val bitmap = rememberImageBitmapFromBase64(imagen ?: "")
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Imagen de usuario",
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = com.example.geekr.R.drawable.unknow_perfil),
                    contentDescription = "Sin foto de perfil",
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    fontSize = 16.sp
                )

                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(comentario.fechaComentario.toDate()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comentario.texto,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                tint = if (index < comentario.valoracion) Color.Yellow else Color.Gray,
                                contentDescription = null
                            )
                        }
                    }

                    val estadoColor = when (comentario.estadoObra) {
                        "Completado" -> Color(0xFF6FCF97)
                        "En proceso" -> Color(0xFFF2C94C)
                        "Dropeado" -> Color(0xFFDD755F)
                        else -> Color(0xFFBDBDBD)
                    }
                    Card(
                        modifier = Modifier.padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = estadoColor)
                    ) {
                        Text(
                            text = comentario.estadoObra,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}