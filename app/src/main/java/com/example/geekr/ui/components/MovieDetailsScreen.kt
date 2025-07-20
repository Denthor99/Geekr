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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.geekr.ui.viewmodel.MovieViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.substringAfter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieDetailScreen(navController: NavHostController, movieId: Int, viewModel: MovieViewModel = viewModel()) {
    val movieDetails by viewModel.getMovieDetails(BuildConfig.API_KEY, movieId).observeAsState()
    val movieRecommendations by viewModel.recommendationsMovies.collectAsState()
    val movieCredits by viewModel.movieCredits.collectAsState()
    val movieTrailerURL by viewModel.getMovieTrailerUrl(BuildConfig.API_KEY, movieId).observeAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val uidUser = user?.uid

    var mostrarTrailerDialog by remember {mutableStateOf(false)}
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
    Log.d("DEBUG", "Datos: $movieDetails")

    // Cargar detalles y recomendaciones cuando la pantalla se abre
    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(BuildConfig.API_KEY, movieId)
        Log.d("DEBUG","Datos cargados de la peli: "+movieId)
        viewModel.loadMovieCredits(BuildConfig.API_KEY, movieId)
        viewModel.loadRecommendationsMovies(BuildConfig.API_KEY, movieId)

        // Comprobacion de favorito
        uidUser?.let {
            auxFunctions.verificarFavorito(it,"peliculas", movieId.toString()){ resultado->
                esFavorito = resultado
            }
            auxFunctions.obtenerBiblioteca(it) { lista ->
                val movie = lista.find { it.id == movieId.toString() }
                movie?.let {
                    estadoSeleccionado = it.estado
                    episodiosVistos = it.episodiosVistos
                }
            }

        }
    }

    movieDetails?.let { movie ->
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
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.backdrop_path}"),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.4f
                        )

                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
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
                                text = movie.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (estadoSeleccionado) {
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
                                    listOf("No visto", "Pendiente", "Completado", "Dropeado").forEach { estado ->
                                        DropdownMenuItem(
                                            text = { Text(estado) },
                                            onClick = {
                                                uidUser?.let { userId ->
                                                    auxFunctions.agregarObra(
                                                        userId, "peliculas", Obra(
                                                            id = movie.id.toString(),
                                                            titulo = movie.title,
                                                            rutaImagen = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                                                            estado = estado.lowercase(),
                                                            tipo = "peliculas"
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
                            }
                        }

                        IconButton(
                            onClick = {
                                uidUser?.let {
                                    if (esFavorito) {
                                        mostrarEliminarFavDialog = true
                                    } else {
                                        val favorito = Favorito(
                                            id = movie.id.toString(),
                                            titulo = movie.title,
                                            tipo = "peliculas",
                                            rutaImagen = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
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
                                        auxFunctions.eliminarFavorito(it, "peliculas", movieId.toString(),
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
                            text = { Text("¿Estás seguro de que quieres eliminar ${movie.title} de favoritos?") }
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF2D3748), shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    ) {
                        Text(text = "\uD83C\uDFA6 Título original:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = movie.original_title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (!movie.tagline.isNullOrBlank()) {
                            Text(text = "Subtítulo:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = movie.tagline, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Text(text = "📅 Estreno:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = auxFunctions.formatDate(movie.release_date), style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "📄 Descripción", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = movie.overview ?: "Sin descripción", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    }
                }

                item {
                    if (!movieCredits.isNullOrEmpty()) {
                        CarruselActores(
                            actors = movieCredits,
                            imageUrl = { it.profile_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" } },
                            name = { it.name },
                            character = { it.character }
                        )
                    }
                }

                item {
                    Text(text = "🎬 Compañía productora:", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = movie.production_companies.joinToString(", ") { it.name }, style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "⭐ Puntuación: ${movie.vote_average}/10 (${movie.vote_count} votos)", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "⏳ Duración: ${movie.runtime ?: "Desconocido"} min", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "💰 Presupuesto: ${movie.budget?.let { "$$it" } ?: "No disponible"}", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🎟️ Recaudación: ${movie.revenue?.let { "$$it" } ?: "No disponible"}", color = Color.White)
                }

                item {
                    if (!movieTrailerURL.isNullOrBlank()) {
                        val videoId = movieTrailerURL!!.substringAfter("v=")
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
                    if (movieRecommendations.isNotEmpty()) {
                        CarruselItems(
                            navController = navController,
                            items = movieRecommendations,
                            title = "🔗 También te podría gustar",
                            imageUrl = { it.poster_path?.let { path -> "https://image.tmdb.org/t/p/w500$path" } },
                            itemId = { it.id },
                            onItemClick = { movieId ->
                                navController.navigate("movie_details/$movieId")
                            }
                        )
                    }
                }

                item {
                    SeccionComentarios(tipoContenido = "peliculas", contenidoId = movieId)
                }
                item {
                    if (mostrarEliminarFavDialog) {
                        AlertDialog(
                            onDismissRequest = { mostrarEliminarFavDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    uidUser?.let {
                                        auxFunctions.eliminarFavorito(
                                            it, "peliculas", movieId.toString(),
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
                            text = { Text("¿Estás seguro de que quieres eliminar ${movie.title} de favoritos?") }
                        )
                    }
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