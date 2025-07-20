package com.example.geekr.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.geekr.shared.auxFunctions.compressImageToBase64
import com.example.geekr.shared.auxFunctions.subirImagenPerfil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    var userName by remember { mutableStateOf(currentUser?.displayName ?: "Usuario sin nombre") }
    var photoBase64 by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Carga de imagen desde Firestore
    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    doc.getString("photoBase64")?.let { photoBase64 = it }
                }
        }
    }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Volver a inicio",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("home")
                        }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Ir a inicio", tint = Color.White)
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4B5DA6))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val imageBitmap = rememberImageBitmapFromBase64(photoBase64)

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = com.example.geekr.R.drawable.unknow_perfil),
                                        contentDescription = "Sin foto de perfil",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            Text(
                                text = currentUser?.email ?: "Email no disponible",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9333EA),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cambiar nombre de perfil", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val pickImageLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let {
                            val base64Image = compressImageToBase64(context, it, quality = 30)

                            if (base64Image != null) {
                                subirImagenPerfil(base64Image, currentUser!!.uid) { result ->
                                    if (result != null) {
                                        photoBase64 = result
                                    }
                                }
                            } else {
                                Toast.makeText(context, "La imagen es demasiado pesada o falló la compresión", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Seleccionar imagen")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }

            if (showEditDialog) {
                EditProfileDialog(
                    currentUser = currentUser,
                    onDismiss = { showEditDialog = false },
                    onSave = { newName ->
                        userName = newName
                        showEditDialog = false
                    }
                )
            }
        }
    }




// Utilidad para convertir Base64 a ImageBitmap
@Composable
fun rememberImageBitmapFromBase64(base64: String?): ImageBitmap? {
    return remember(base64) {
        try {
            if (base64 == null) return@remember null
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}


@Composable
fun EditProfileDialog(
    currentUser: FirebaseUser?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    var newName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Cambiar nombre de usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nombre de usuario") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                )?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Actualizamos también Firestore con el nuevo nombre
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(currentUser.uid)
                            .set(mapOf("name" to newName), SetOptions.merge())
                            .addOnSuccessListener {
                                onSave(newName)
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error al actualizar nombre en Firestore",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Error al actualizar nombre en Firebase Auth",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }) {
                Text("Guardar")
            }
        }
        ,
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}