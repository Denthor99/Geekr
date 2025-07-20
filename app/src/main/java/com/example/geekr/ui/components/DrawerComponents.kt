package com.example.geekr.ui.components

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CompactDrawerContent(
    navController: NavHostController,
    onItemClick: (String) -> Unit
) {
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid
    var userName by remember {
        mutableStateOf(
            auth.currentUser?.displayName ?: auth.currentUser?.email ?: "Usuario Invitado"
        )
    }
    var photoBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Observa la ruta actual para resaltar el ítem activo
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItem = remember(currentRoute) {
        when (currentRoute) {
            "home" -> "Inicio"
            "anime" -> "Animes"
            "manga" -> "Mangas"
            "series" -> "Series"
            "peliculas" -> "Peliculas"
            "favoritos" -> "Mis Favoritos"
            "biblioteca" -> "Mi Biblioteca"
            else -> ""
        }
    }

    LaunchedEffect(auth.currentUser, userId) {
        userName = auth.currentUser?.displayName ?: auth.currentUser?.email ?: "Usuario Invitado"
        if (userId != null) {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(userId).get().await()
            val base64 = doc.getString("photoBase64")
            base64?.let {
                val bytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                photoBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        }
    }

    val drawerItems = listOf(
        "Inicio" to Icons.Default.Home,
        "Animes" to Icons.Default.PlayArrow,
        "Mangas" to Icons.Default.Book,
        "Series" to Icons.Default.LiveTv,
        "Peliculas" to Icons.Default.VideoLibrary,
        "Mis Favoritos" to Icons.Default.Favorite,
        "Mi Biblioteca" to Icons.Default.LibraryBooks,
        "Cerrar sesión" to Icons.Default.ExitToApp
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E3A8A),
                        Color(0xFF4B5DA6),
                        Color(0xFF786FA8),
                        Color(0xFF9333EA)
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.clickable { navController.navigate("user_profile") }) {
            if (photoBitmap != null) {
                Image(
                    bitmap = photoBitmap!!.asImageBitmap(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = com.example.geekr.R.drawable.unknow_perfil),
                    contentDescription = "Sin foto de perfil",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        drawerItems.forEach { (label, icon) ->
            val isSelected = label == selectedItem
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onItemClick(label)
                        when (label) {
                            "Inicio" -> navController.navigate("home")
                            "Animes" -> navController.navigate("anime")
                            "Mangas" -> navController.navigate("manga")
                            "Series" -> navController.navigate("series")
                            "Peliculas" -> navController.navigate("peliculas")
                            "Mis Favoritos" -> navController.navigate("favoritos")
                            "Mi Biblioteca" -> navController.navigate("biblioteca")
                            "Cerrar sesión" -> {
                                auth.signOut()
                                navController.navigate("login") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        }
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    color = if (isSelected) Color.Yellow else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "© 2025 Geekr · De Cádiz al mundo",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}