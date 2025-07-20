package com.example.geekr.ui.components
import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter


@Composable
fun <T> CarruselItems(
    navController: NavHostController,
    items: List<T>,
    title: String,
    imageUrl: (T) -> String?,
    itemId: (T) -> Int,
    onItemClick: (Int) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF81E6D9),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            val url = imageUrl(item) ?: com.example.geekr.R.drawable.unknow_perfil

            Box(
                modifier = Modifier.width(150.dp).clickable { onItemClick(itemId(item)) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp).background(Color(0xFF2D3748), shape = MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
fun <T> CarruselActores(
    actors: List<T>,
    imageUrl: (T) -> String?,
    name: (T) -> String,
    character: (T) -> String?
) {
    if (actors.isNotEmpty()) {
        Text(text = "🎭 Reparto Principal", style = MaterialTheme.typography.titleMedium, color = Color(0xFF81E6D9))
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(actors) { actor ->
                val url = imageUrl(actor) ?: com.example.geekr.R.drawable.unknow_perfil

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = name(actor),
                        modifier = Modifier.size(140.dp).background(Color(0xFF2D3748))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = name(actor), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    character(actor)?.let {
                        Text(text = it, style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    }
                }
            }
        }
    }
}
