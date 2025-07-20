package com.example.geekr.ui.components

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.geekr.BuildConfig
import com.example.geekr.data.model.user.Favorito
import com.example.geekr.data.model.user.Obra
import com.example.geekr.shared.auxFunctions
import com.example.geekr.ui.viewmodel.TVShowViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.substringAfter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TVShowDetailScreen(navController: NavHostController, tvShowId: Int, viewModel: TVShowViewModel = viewModel()) {
    val tvShowDetails by viewModel.getTVShowDetails(BuildConfig.API_KEY, tvShowId).observeAsState()
    val tvShowCredits by viewModel.tvShowCredits.collectAsState()
    val tvShowRecommendations by viewModel.recommendationTVShows.collectAsState()
    val tvShowTrailerURL by viewModel.getTVShowTrailerUrl(BuildConfig.API_KEY, tvShowId).observeAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val uidUser = user?.uid

    var mostrarTrailerDialog by remember { mutableStateOf(false) }
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


    LaunchedEffect(tvShowId) {
        viewModel.getTVShowDetails(BuildConfig.API_KEY, tvShowId)
        viewModel.loadTVShowCredits(BuildConfig.API_KEY, tvShowId)
        viewModel.loadRecommendationsTVShows(BuildConfig.API_KEY, tvShowId)

        uidUser?.let {
            auxFunctions.verificarFavorito(it, "seriesTV", tvShowId.toString()) { resultado ->
                esFavorito = resultado
            }
            auxFunctions.obtenerBiblioteca(it) { lista ->
                val tvShow = lista.find { it.id == tvShowId.toString() }
                tvShow?.let {
                    estadoSeleccionado = it.estado
                    episodiosVistos = it.episodiosVistos
                }
            }
        }
    }

    tvShowDetails?.let { tvShow ->
        Scaffold(
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            containerColor = Color(0xFF1E293B)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(26.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${tvShow.backdrop_path}"),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.4f
                        )
                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${tvShow.poster_path}"),
                            contentDescription = null,
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
                                text = tvShow.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )

                            Box {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (estadoSeleccionado) {
                                        "en curso" -> Color(0xFF3182CE) // Azul
                                        "pendiente" -> Color(0xFFDD6B20) // Naranja
                                        "completado" -> Color(0xFF38A169) // Verde
                                        "dropeado" -> Color(0xFFE53E3E) // Rojo
                                        else -> Color.Gray
                                    },
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clickable { expanded = true }
                                ) {
                                    Text(
                                        text = when (estadoSeleccionado) {
                                            "en curso" -> "📺 En curso"
                                            "pendiente" -> "⏳ Pendiente"
                                            "completado" -> "✅ Completado"
                                            "dropeado" -> "🚫 Dropeado"
                                            else -> "No visto"
                                        },
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    listOf("En curso", "Pendiente", "Completado", "Dropeado", "No visto").forEach { estado ->
                                        DropdownMenuItem(
                                            text = { Text(estado) },
                                            onClick = {
                                                uidUser?.let { userId ->
                                                    val episodiosFinal = when (estado.lowercase()) {
                                                        "completado" -> tvShow.number_of_episodes ?: 0
                                                        "pendiente" -> 0
                                                        else -> episodiosVistos
                                                    }

                                                    auxFunctions.agregarObra(
                                                        userId, "seriesTV", Obra(
                                                            id = tvShowId.toString(),
                                                            titulo = tvShow.name,
                                                            rutaImagen = "https://image.tmdb.org/t/p/w500${tvShow.poster_path}",
                                                            estado = estado.lowercase(),
                                                            tipo = "seriesTV",
                                                            episodiosVistos = episodiosFinal,
                                                            totalEpisodios = tvShow.number_of_episodes
                                                        ),
                                                        onSuccess = { estadoSeleccionado = estado.lowercase(); episodiosVistos = episodiosFinal },
                                                        onFailure = { e -> println("Error al actualizar estado: ${e.message}") }
                                                    )
                                                }
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (estadoSeleccionado != "completado" && estadoSeleccionado != "No visto" && tvShow.number_of_episodes != null) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Episodios vistos: $episodiosVistos/${tvShow.number_of_episodes}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )

                                        Slider(
                                            value = episodiosVistos.toFloat(),
                                            onValueChange = { nuevoValor ->
                                                episodiosVistos = nuevoValor.toInt()
                                                uidUser?.let {
                                                    auxFunctions.actualizarEpisodiosVistos(
                                                        it,
                                                        "seriesTV",
                                                        tvShowId.toString(),
                                                        episodiosVistos,
                                                        {},
                                                        {})
                                                }
                                            },
                                            valueRange = 0f..tvShow.number_of_episodes.toFloat(),
                                            colors = SliderDefaults.colors(
                                                thumbColor = when {
                                                    episodiosVistos == tvShow.number_of_episodes -> Color.Green
                                                    episodiosVistos > tvShow.number_of_episodes / 2 -> Color.Yellow
                                                    else -> Color.Red
                                                },
                                                activeTrackColor = Color.White,
                                                inactiveTrackColor = Color.Gray
                                            ),
                                            modifier = Modifier
                                                .widthIn(max = 300.dp)
                                                .padding(horizontal = 16.dp)
                                                .height(10.dp)
                                        )
                                    }
                                }
                            }}


                            IconButton(
                            onClick = {
                                uidUser?.let {
                                    if (esFavorito) {
                                        mostrarEliminarFavDialog = true
                                    } else {
                                        val favorito = Favorito(
                                            id = tvShowId.toString(),
                                            titulo = tvShow.name,
                                            tipo = "seriesTV",
                                            rutaImagen = "https://image.tmdb.org/t/p/w500${tvShow.poster_path}"
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
                                modifier = Modifier.size(24.dp)
                            )
                    }
                }

                if (mostrarEliminarFavDialog) {
                        AlertDialog(
                            onDismissRequest = { mostrarEliminarFavDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    uidUser?.let {
                                        auxFunctions.eliminarFavorito(it, "seriesTV", tvShowId.toString(),
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
                            text = { Text("¿Estás seguro de que quieres eliminar ${tvShow.name} de favoritos?") }
                        )
                    }

                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF2D3748), shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    ) {
                        Text(text = "📅 Primera emisión:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = auxFunctions.formatDate(tvShow.first_air_date), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "📄 Descripción", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = tvShow.overview ?: "Sin descripción", style = MaterialTheme.typography.titleSmall, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (!tvShowCredits.isNullOrEmpty()) {
                            CarruselActores(
                                actors = tvShowCredits,
                                imageUrl = { it.profile_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" } },
                                name = { it.name },
                                character = { it.character }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "🎬 Número de temporadas: ${tvShow.number_of_seasons}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "📺 Episodios totales: ${tvShow.number_of_episodes}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "⭐ Puntuación: ${tvShow.vote_average}/10 (${tvShow.vote_count} votos)", color = Color.White)
                    }
                }

                item {
                    if (!tvShowTrailerURL.isNullOrBlank()) {
                        val videoId = tvShowTrailerURL!!.substringAfter("v=")
                        Button(
                            onClick = { mostrarTrailerDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(text = "🎬 Ver Tráiler", color = Color.White)
                        }

                        if (mostrarTrailerDialog) {
                            YouTubeTrailerDialog(videoId = videoId, onDismiss = { mostrarTrailerDialog = false })
                        }
                    }
                }

                item {
                    if (tvShowRecommendations.isNotEmpty()) {
                        CarruselItems(
                            navController = navController,
                            items = tvShowRecommendations,
                            title = "📺 También te podría gustar",
                            imageUrl = { it.poster_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" } },
                            itemId = { it.id },
                            onItemClick = { tvShowId ->
                                navController.navigate("tvShow_details/$tvShowId")
                            }
                        )
                    }
                }
                item {
                    SeccionComentarios(tipoContenido = "seriesTV", contenidoId = tvShowId)
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