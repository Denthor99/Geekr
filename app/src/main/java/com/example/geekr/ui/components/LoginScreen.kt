package com.example.geekr.ui.components

import android.R
import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geekr.shared.auxFunctions.animarInput
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(onClickRegister: () -> Unit = {}, onSuccessfullLogin: () -> Unit = {}) {
    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity
    var passwordVisible by remember { mutableStateOf(false) }
    var msgError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputPasswd by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var recoveryEmail by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Defino una animacion para los inputs
    val emailOffset = remember { Animatable(0f) }
    val passwordOffset = remember { Animatable(0f) }

    LaunchedEffect(emailError) {
        if (emailError.isNotEmpty()) {
            animarInput(emailOffset)
        }
    }
    LaunchedEffect(passwordError){
        if (passwordError.isNotEmpty()) {
            animarInput(passwordOffset)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = com.example.geekr.R.drawable.logo_geekr),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Email", color = Color.White) },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Icono email", tint = Color.White) },
                modifier = Modifier.fillMaxWidth().offset(x=emailOffset.value.dp),
                supportingText = {
                    if (emailError.isNotEmpty()) {
                        Text(text = emailError, color = Color.Red)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = inputPasswd,
                onValueChange = { inputPasswd = it },
                label = { Text("Contraseña", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono password",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña", tint = Color.White)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = passwordOffset.value.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                supportingText = {
                    if (passwordError.isNotEmpty()) {
                        Text(text = passwordError, color = Color.Red)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                )
            )

            if (msgError.isNotEmpty()) {
                Text(
                    msgError,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val isValidEmail: Boolean = validateEmail(inputEmail).first
                    val isValidPasswd: Boolean = validatePassword(inputPasswd).first
                    emailError = validateEmail(inputEmail).second
                    passwordError = validatePassword(inputPasswd).second

                    if (isValidEmail && isValidPasswd) {
                        auth.signInWithEmailAndPassword(inputEmail, inputPasswd)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    onSuccessfullLogin()
                                } else {
                                    msgError = when (task.exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrecta"
                                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo"
                                        else -> "Error al iniciar sesión, intenta de nuevo"
                                    }
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9900),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Iniciar sesión", fontSize = 20.sp)
            }

            TextButton(onClick = { showDialog = true }) {
                Text("¿Olvidaste tu contraseña?", color = Color(0xFFFF9900))
            }

            TextButton(onClick = onClickRegister) {
                Text("¿No tienes una cuenta? Regístrate", color = Color(0xFFFF9900))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Recuperar contraseña") },
            text = {
                Column {
                    Text("Introduce tu correo electrónico:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = recoveryEmail,
                        onValueChange = { recoveryEmail = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (recoveryEmail.isNotEmpty()) {
                            auth.sendPasswordResetEmail(recoveryEmail)
                                .addOnCompleteListener { task ->
                                    Toast.makeText(
                                        context,
                                        if (task.isSuccessful) "Correo de recuperación enviado"
                                        else "Error: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    showDialog = false
                                }
                        } else {
                            Toast.makeText(context, "Introduce un correo válido", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}


