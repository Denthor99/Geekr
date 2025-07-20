package com.example.geekr.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun NavigationApp(){
    val navController = rememberNavController()
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    var startDestination:String = "login"


    // Cutrada historica pero bueno el launchedEffect da muchos problemas
    if(currentUser!=null){
        startDestination = "home"
    } else {
        startDestination = "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable("login"){ LoginScreen(onClickRegister = {
            navController.navigate("register")
        },
            onSuccessfullLogin = {
                navController.navigate(("home")){
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
            ) }
        composable("register"){ RegisterScreen(onClickBack = {
            navController.popBackStack()
        }, onSuccessfulRegister = {
            navController.navigate("home"){
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }) }
        composable("home"){
            HomeScreen(navController ,onClickLogOut = {
                navController.navigate("login"){
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            })
        }
        composable("anime"){
            AnimeScreen(navController)
        }
        composable("anime_details/{animeId}") { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId")?.toIntOrNull()
            animeId?.let {
                AnimeDetailScreen(navController, animeId = it)
            }
        }
        composable("manga"){
            MangaScreen(navController)
        }
        composable("manga_details/{mangaId}") { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId")?.toIntOrNull()
            mangaId?.let {
                MangaDetailScreen(navController, mangaId = it)
            }

        }
        composable("peliculas") {
            MovieScreen(navController)
        }
        composable("movie_details/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            movieId?.let {
                MovieDetailScreen(navController, movieId = it)
            }
        }
        composable("series") {
            TVShowScreen(navController)
        }
        composable("tvShow_details/{tvShowId}") { backStackEntry ->
            val tvShowId = backStackEntry.arguments?.getString("tvShowId")?.toIntOrNull()
            tvShowId?.let {
                TVShowDetailScreen(navController, tvShowId = it)
            }

        }
        composable("favoritos"){
            FavoritosScreen(navController)
        }
        composable("biblioteca"){
            BibliotecaScreen(navController)
        }
        composable("user_profile"){
            ProfileScreen(navController)
        }
    }
}