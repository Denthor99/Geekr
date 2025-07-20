package com.example.geekr.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.geekr.data.model.user.Favorito
import com.example.geekr.shared.auxFunctions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritosScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val opcionesOrden = listOf("Fecha (Desc)", "Fecha (Asc)")
    val opcionesTipo = listOf("todos", "animes", "mangas", "peliculas", "seriesTV")

    var favoritos by remember { mutableStateOf(listOf<Favorito>()) }
    var ordenSeleccionado by remember { mutableStateOf("fechaDesc") }
    var tipoSeleccionado by remember { mutableStateOf("todos") }
    var expanded by remember { mutableStateOf(false) }
    var mostrarEliminarFavDialog by remember { mutableStateOf(false) }
    var favoritoSeleccionado by remember { mutableStateOf<Favorito?>(null) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(uid, ordenSeleccionado,tipoSeleccionado) {
        uid?.let {
            isLoading = true
            auxFunctions.obtenerFavoritos(it, ordenSeleccionado, tipoSeleccionado) { lista ->
                favoritos = lista
                isLoading = false
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .width(340.dp)
                    .background(Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF4B5DA6),
                            Color(0xFF786FA8),
                            Color(0xFF9333EA)
                        )
                    ))
            ) {
                CompactDrawerContent(
                    navController = navController,
                    onItemClick = { item: String ->
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.background(Color(0xFF1E3A8A)),
                    title = { Text(text = "⭐ Mis Favoritos", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Icon", tint = Color.White)
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Filtrar", tint = Color.White)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                opcionesOrden.forEach { orden ->
                                    DropdownMenuItem(
                                        text = { Text(orden) },
                                        onClick = {
                                            ordenSeleccionado = when (orden) {
                                                "Fecha (Desc)" -> "fechaDesc"
                                                "Fecha (Asc)" -> "fechaAsc"
                                                else -> "fechaDesc"
                                            }
                                            expanded = false
                                        }
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                opcionesTipo.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo) },
                                        onClick = {
                                            tipoSeleccionado = tipo
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box( // Aplicamos fondo dentro de Scaffold
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF4B5DA6),
                            Color(0xFF786FA8),
                            Color(0xFF9333EA)
                        )
                    )
                )
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                        return@Column
                    }

                    // Filtrar por tipo de contenido
                    val favoritosFiltrados = if (tipoSeleccionado == "todos") {
                        favoritos
                    } else {
                        favoritos.filter { it.tipo == tipoSeleccionado }
                    }

                    if (!isLoading && favoritosFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No hay favoritos",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        return@Column
                    }


                    // Agrupar favoritos por fecha de adición
                    val favoritosAgrupados = favoritos.groupBy { favorito ->
                        SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es","ES")).format(favorito.fechaFavorito.toDate())
                    }

                    favoritosAgrupados.forEach { (fecha, favoritosDelDia) ->
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        favoritosDelDia.forEach { favorito ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                .combinedClickable (
                                    onClick = {
                                    when (favorito.tipo) {
                                        "animes" -> navController.navigate("anime_details/${favorito.id}")
                                        "mangas" -> navController.navigate("manga_details/${favorito.id}")
                                        "peliculas" -> navController.navigate("movie_details/${favorito.id}")
                                        "seriesTV" -> navController.navigate("tvShow_details/${favorito.id}")
                                    }
                                },
                                    onLongClick = {
                                        favoritoSeleccionado = favorito
                                        mostrarEliminarFavDialog = true
                                    })) {

                                Image(
                                    painter = rememberAsyncImagePainter(favorito.rutaImagen),
                                    contentDescription = null,
                                    modifier = Modifier.height(150.dp).weight(1f),
                                    contentScale = ContentScale.Fit
                                )

                                Column(modifier = Modifier.weight(2f).padding(start = 16.dp, top = 8.dp)) {
                                    Text(text = favorito.titulo, style = MaterialTheme.typography.titleMedium, color = Color.White)

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = when (favorito.tipo) {
                                            "animes" -> Color(0xFF9333EA)
                                            "mangas" -> Color(0xFF4B5DA6)
                                            "peliculas" -> Color(0xFF1E3A8A)
                                            "seriesTV" -> Color(0xFF009688)
                                            else -> Color.Gray
                                        },
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = favorito.tipo.capitalize(),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
    if (mostrarEliminarFavDialog && favoritoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarEliminarFavDialog = false },
            confirmButton = {
                Button(onClick = {
                    uid?.let { userId ->
                        auxFunctions.eliminarFavorito(userId, favoritoSeleccionado!!.tipo, favoritoSeleccionado!!.id,
                            onSuccess = {
                                favoritos = favoritos.filter { it.id != favoritoSeleccionado!!.id }
                                mostrarEliminarFavDialog = false
                            },
                            onFailure = { e -> println("Error al eliminar favorito: ${e.message}") }
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
            title = { Text("Confirmar eliminación", color = Color.Black) },
            text = { Text("¿Estás seguro de que quieres eliminar \"${favoritoSeleccionado!!.titulo}\" de favoritos?", color = Color.Gray) }
        )
    }
}