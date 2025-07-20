package com.example.geekr.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.geekr.data.model.anime.AnimeMedia
import com.example.geekr.data.model.user.Favorito
import com.example.geekr.data.model.user.Obra
import com.example.geekr.shared.auxFunctions
import com.example.geekr.ui.viewmodel.AnimeViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AnimeDetailScreen(navController: NavHostController, animeId: Int, viewModel: AnimeViewModel = viewModel()) {
    val animeDetails by viewModel.getAnimeDetails(animeId).observeAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val uidUser = user?.uid

    var esFavorito by remember { mutableStateOf(false) }
    var mostrarEliminarFavDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("No visto") }
    var episodiosVistos by remember { mutableStateOf(0) }

    // Animaciones
    val scale = animateFloatAsState(
        targetValue = if (esFavorito) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.4f)
    )

    LaunchedEffect(animeId) {
        viewModel.getAnimeDetails(animeId)

        // Comprobación de favorito
        uidUser?.let {
            auxFunctions.verificarFavorito(it, "animes", animeId.toString()) { resultado ->
                esFavorito = resultado
            }
            auxFunctions.obtenerBiblioteca(it) { lista ->
                val anime = lista.find { it.id == animeId.toString() }
                anime?.let {
                    estadoSeleccionado = it.estado
                    episodiosVistos = it.episodiosVistos
                }
            }
        }
    }

    animeDetails?.let { media ->
        Scaffold(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            containerColor = Color(0xFF1E293B)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(300.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF9333EA), Color(0xFF786FA8), Color.Transparent)
                                )
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(media.coverImage.large),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.title.english ?: media.title.romaji ?: "Desconocido",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (estadoSeleccionado) {
                                    "en curso" -> Color(0xFF3182CE) // Azul
                                    "pendiente" -> Color(0xFFDD6B20) // Naranja
                                    "completado" -> Color(0xFF38A169) // Verde
                                    "dropeado" -> Color(0xFFE53E3E) // Rojo
                                    else -> Color.Gray
                                },
                                modifier = Modifier.padding(top = 4.dp)
                                    .clickable { expanded = true }
                            ) {
                                Text(
                                    text = when (estadoSeleccionado) {
                                        "en curso" -> "📺 En curso"
                                        "pendiente" -> "⏳ Pendiente"
                                        "completado" -> "✅ Completado"
                                        "dropeado" -> "🚫 Dropeado"
                                        else -> "\uD83D\uDE48 No visto"
                                    },
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listOf("En curso", "Pendiente", "Completado", "Dropeado").forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado) },
                                        onClick = {
                                            uidUser?.let { userId ->
                                                auxFunctions.agregarObra(
                                                    userId, "animes", Obra(
                                                        id = animeId.toString(),
                                                        titulo = media.title.romaji ?: "Desconocido",
                                                        rutaImagen = media.coverImage.medium.toString(),
                                                        estado = estado.lowercase(),
                                                        tipo = "animes",
                                                        episodiosVistos = episodiosVistos,
                                                        totalEpisodios = media.episodes
                                                    ),
                                                    onSuccess = { estadoSeleccionado = estado.lowercase() },
                                                    onFailure = { e -> println("Error al actualizar estado: ${e.message}") }
                                                )
                                            }
                                            expanded = false
                                        }
                                    )
                                }
                            }


                            if ((estadoSeleccionado != "completado" && estadoSeleccionado != "No visto" && estadoSeleccionado != "pendiente") && media.episodes != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Episodios vistos: $episodiosVistos/${media.episodes}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )

                                    Slider(
                                        value = episodiosVistos.toFloat(),
                                        onValueChange = { nuevoValor ->
                                            episodiosVistos = nuevoValor.toInt()
                                            uidUser?.let {
                                                auxFunctions.actualizarEpisodiosVistos(it, "animes", animeId.toString(), episodiosVistos, {}, {})
                                            }
                                        },
                                        valueRange = 0f..media.episodes.toFloat(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = when {
                                                episodiosVistos == media.episodes -> Color.Green
                                                episodiosVistos > media.episodes / 2 -> Color.Yellow
                                                else -> Color.Red
                                            },
                                            activeTrackColor = Color.White,
                                            inactiveTrackColor = Color.Gray
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp).height(10.dp) // Grosor aumentado
                                    )
                                }

                            }
                        }

                        IconButton(
                            onClick = {
                                uidUser?.let {
                                    if (esFavorito) {
                                        mostrarEliminarFavDialog = true
                                    } else {
                                        val favorito = Favorito(
                                            id = media.id.toString(),
                                            titulo = media.title.english ?: media.title.romaji ?: "Desconocido",
                                            tipo = "animes",
                                            rutaImagen = media.coverImage.medium.toString()
                                        )
                                        auxFunctions.agregarFavorito(
                                            it,
                                            favorito,
                                            onSuccess = { esFavorito = true },
                                            onFailure = { e -> println("Error al agregar favorito: $e") }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.scale(scale.value)
                        ) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (esFavorito) "Eliminar de favoritos" else "Agregar a favoritos",
                                tint = if (esFavorito) Color.Red else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    if (mostrarEliminarFavDialog) {
                        AlertDialog(
                            onDismissRequest = { mostrarEliminarFavDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    uidUser?.let {
                                        auxFunctions.eliminarFavorito(it, "animes", animeId.toString(),
                                            onSuccess = {
                                                esFavorito = false
                                                mostrarEliminarFavDialog = false
                                            },
                                            onFailure = { e -> println("Error al eliminar favorito: $e") }
                                        )
                                    }
                                }) {
                                    Text("Eliminar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { mostrarEliminarFavDialog = false }) {
                                    Text("Cancelar")
                                }
                            },
                            title = { Text("Confirmar eliminación") },
                            text = { Text("¿Estás seguro de que quieres eliminar ${media.title.english ?: media.title.romaji ?: "Desconocido"} de favoritos?") }
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF2D3748), shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    ) {
                        Text(text = "📄 Descripción", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        CleanDescription(media.description)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "📺 Episodios: ${media.episodes ?: "?"}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "🎭 Géneros: ${media.genres?.joinToString() ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "⭐ Promedio de puntuación: ${media.averageScore ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "📡 Estado: ${media.status ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    }
                }

                item {
                    media.recommendations?.nodes?.let { recommendations ->
                        CarruselRecomendaciones(
                            navController = navController,
                            recomendaciones = recommendations.mapNotNull { it.mediaRecommendation },
                            onItemClick = { animeId -> navController.navigate("anime_details/$animeId") }
                        )
                    }
                }

                item {
                    SeccionComentarios(tipoContenido = "animes", contenidoId = animeId)
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Cargando detalles...", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        }
    }
}

@Composable
fun CarruselRecomendaciones(
    navController: NavHostController,
    recomendaciones: List<AnimeMedia>, // Debe ser una lista de AnimeMedia
    onItemClick: (Int) -> Unit // Acción al hacer clic en un elemento
) {
    val context = LocalContext.current
    Text(
        text = "🔗 Animes Relacionados",
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF81E6D9),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(recomendaciones) { anime ->
            anime?.let {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable {
                            onItemClick(anime.id)
                        }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = rememberAsyncImagePainter(it.coverImage.medium),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color(0xFF2D3748), shape = MaterialTheme.shapes.medium)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CleanDescription(description: String?) {
    val cleanedDescription = description?.let {
        HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    } ?: "Descripción no disponible"

    Text(
        text = cleanedDescription,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
    )
}