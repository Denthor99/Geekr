package com.example.geekr.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.geekr.BuildConfig
import com.example.geekr.ui.viewmodel.AnimeViewModel
import com.example.geekr.ui.viewmodel.MangaViewModel
import com.example.geekr.ui.viewmodel.MovieViewModel
import com.example.geekr.ui.viewmodel.TVShowViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController, onClickLogOut: () -> Unit={}){
    val auth = Firebase.auth
    val user = auth.currentUser
    if(user != null){
        MainScreenWithCompactMenu(navController) {
            MainScreen(navController)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithCompactMenu(navController: NavHostController,content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Ajustamos el tamaño del contenido del menú aquí
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .width(340.dp)
                    .background(brush = Brush.verticalGradient(
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
                    onItemClick = { item ->
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .background(Color(0xFF1E3A8A)
                        ),
                    title = { Text(text = "GeekrVerse", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.White) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Icon", tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content()
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    animeViewModel: AnimeViewModel = viewModel(),
    mangaViewModel: MangaViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel(),
    tvShowViewModel: TVShowViewModel = viewModel()
) {
    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        // Cargar películas y series
        movieViewModel.apply {
            loadPopularMovies(BuildConfig.API_KEY)
            loadBestRatedMovies(BuildConfig.API_KEY)
        }
        tvShowViewModel.apply {
            loadPopularTVShow(BuildConfig.API_KEY)
            loadBestRatedTVShows(BuildConfig.API_KEY)
        }

        // Cargar animes y mangas
        animeViewModel.apply {
            loadPopularAnimes()
            loadBestAnimes()
        }
        mangaViewModel.apply {
            loadPopularMangas()
            loadBestMangas()
        }

    }

    // Observar datos de los carruseles
    val popularAnimes = animeViewModel.popularAnimes.collectAsState()
    val bestRatedAnimes = animeViewModel.bestRatedAnimes.collectAsState()
    val popularMangas = mangaViewModel.popularMangas.collectAsState()
    val bestRatedMangas = mangaViewModel.bestMangas.collectAsState()
    val popularMovies = movieViewModel.popularMovies.collectAsState()
    val popularTVShows = tvShowViewModel.popularTVShows.collectAsState()
    val bestRatedMovies = movieViewModel.bestRatedMovies.collectAsState()
    val bestRatedTVShows = tvShowViewModel.bestRatedTVShow.collectAsState()

    // Comprobamos que todos los datos estén cargados
    val isLoading = popularAnimes.value.isEmpty()
            || bestRatedAnimes.value.isEmpty()
            || popularMangas.value.isEmpty()
            || bestRatedMangas.value.isEmpty()
            || popularMovies.value.isEmpty()
            || bestRatedMovies.value.isEmpty()
            || popularTVShows.value.isEmpty()
            || bestRatedTVShows.value.isEmpty()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF4B5DA6),
                            Color(0xFF786FA8),
                            Color(0xFF9333EA)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF4B5DA6),
                            Color(0xFF786FA8),
                            Color(0xFF9333EA)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Películas más populares",
                    items = popularMovies.value,
                    extractImageUrl = { movie -> "https://image.tmdb.org/t/p/w500${movie.poster_path}" },
                    extractId = { movie -> movie.id },
                    type = "movie"
                )

            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Series más populares",
                    items = popularTVShows.value,
                    extractImageUrl = { tvShow -> "https://image.tmdb.org/t/p/w500${tvShow.poster_path}" },
                    extractId = { tvShow -> tvShow.id },
                    type = "tv"
                )

            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Animes más populares",
                    items = popularAnimes.value,
                    extractImageUrl = { anime -> anime.coverImage.large.toString() },
                    extractId = { anime -> anime.id },
                    type = "anime"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Mangas más populares",
                    items = popularMangas.value,
                    extractImageUrl = { manga -> manga.coverImage.large.toString() },
                    extractId = { manga -> manga.id },
                    type = "manga"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Películas mejor valoradas",
                    items = bestRatedMovies.value,
                    extractImageUrl = { movie -> "https://image.tmdb.org/t/p/w500${movie.poster_path}" },
                    extractId = { movie -> movie.id },
                    type = "movie"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Series mejor valoradas",
                    items = bestRatedTVShows.value,
                    extractImageUrl = { tvShow -> "https://image.tmdb.org/t/p/w500${tvShow.poster_path}" },
                    extractId = { tvShow -> tvShow.id },
                    type = "tv"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Animes mejor valorados",
                    items = bestRatedAnimes.value,
                    extractImageUrl = { anime -> anime.coverImage.large.toString() },
                    extractId = { anime -> anime.id },
                    type = "anime"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Carrusel(
                    navController = navController,
                    title = "Mangas mejor valorados",
                    items = bestRatedMangas.value,
                    extractImageUrl = { manga -> manga.coverImage.large.toString() },
                    extractId = { manga -> manga.id },
                    type = "manga"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun <T> Carrusel(navController: NavHostController,
    title: String, items: List<T>,
                 extractImageUrl: (T) -> String, extractId: (T) -> Int,
                 type: String) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp
            ),
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Column(
                    modifier = Modifier
                        .width(146.dp)
                        .clickable{
                            val id = extractId(item)
                            when (type) {
                                "anime" -> navController.navigate("anime_details/$id")
                                "manga" -> navController.navigate("manga_details/$id")
                                "movie" -> navController.navigate("movie_details/$id")
                                "tv" -> navController.navigate("tvShow_details/$id")
                            }
                        }
                ) {
                    // Imagen del carrusel utilizando la función extractImageUrl
                    val imageUrl = extractImageUrl(item)
                    val painter = rememberAsyncImagePainter(imageUrl)
                    androidx.compose.foundation.Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .height(185.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}