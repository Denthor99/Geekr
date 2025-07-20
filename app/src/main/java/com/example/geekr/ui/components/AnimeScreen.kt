package com.example.geekr.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.geekr.ui.viewmodel.AnimeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun AnimeScreen(navController: NavHostController){
    val auth = Firebase.auth
    val user = auth.currentUser
    if(user != null){
        AnimeScreenWithCompactMenu(navController) {
            AnimeSearchScreen(navController)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeScreenWithCompactMenu(navController: NavHostController, content: @Composable () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
                    onItemClick = { item: String ->
                        coroutineScope.launch {
                            drawerState.close() // Cierra el menú cuando se selecciona un ítem
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
                    modifier = Modifier.background(Color(0xFF1E3A8A)),
                    title = { Text(text = "Geekr no Jutsu", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.White) },
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
fun AnimeSearchScreen(navController: NavHostController, viewModel: AnimeViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val animeList by viewModel.animeList.collectAsState()

    Column(
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
            .padding(24.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchQuery.text.isEmpty()) {
                        Text("Escribe el nombre de un anime...", color = Color.LightGray)
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.searchAnime(searchQuery.text) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(animeList) { media ->
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.08f), shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("anime_details/${media.id}")
                        }
                ) {
                    Text(
                        text = "🎬 ${media.title.romaji ?: ""} / ${media.title.english ?: ""}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📺 Episodios: ${media.episodes ?: "?"}", color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📡 Estado: ${media.status}", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(media.coverImage.large),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }
}
