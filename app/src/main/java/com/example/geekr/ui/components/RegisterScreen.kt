package com.example.geekr.ui.components

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geekr.shared.auxFunctions.animarInput
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RegisterScreen(onClickBack: () -> Unit = {}, onSuccessfulRegister: () -> Unit = {}) {
    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados de los inputs
    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputPasswd by remember { mutableStateOf("") }
    var inputConfirmPasswd by remember { mutableStateOf("") }

    // Estado de los mensajes de error
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordConfirmationError by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }

    // Animaciones para shake en inputs con error
    val nameShakeOffset = remember { Animatable(0f) }
    val emailShakeOffset = remember { Animatable(0f) }
    val passwordShakeOffset = remember { Animatable(0f) }
    val confirmPasswordShakeOffset = remember { Animatable(0f) }

    LaunchedEffect(nameError) { if (nameError.isNotEmpty()) animarInput(nameShakeOffset) }
    LaunchedEffect(emailError) { if (emailError.isNotEmpty()) animarInput(emailShakeOffset) }
    LaunchedEffect(passwordError) { if (passwordError.isNotEmpty()) animarInput(passwordShakeOffset) }
    LaunchedEffect(passwordConfirmationError) { if (passwordConfirmationError.isNotEmpty()) animarInput(confirmPasswordShakeOffset) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", fontSize = 20.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF4B5DA6), Color(0xFF786FA8), Color(0xFF9333EA))
                    )
                )
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = com.example.geekr.R.drawable.logo_geekr),
                contentDescription = "Logo registro",
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Regístrate",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Nombre", color = Color.White) },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Nombre", tint = Color.White) },
                modifier = Modifier.fillMaxWidth().offset(x = nameShakeOffset.value.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                ),
                supportingText = {
                    if (nameError.isNotEmpty()) {
                        Text(text = nameError, color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Email", color = Color.White) },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.White) },
                modifier = Modifier.fillMaxWidth().offset(x = emailShakeOffset.value.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                ),
                supportingText = {
                    if (emailError.isNotEmpty()) {
                        Text(text = emailError, color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = inputPasswd,
                onValueChange = { inputPasswd = it },
                label = { Text("Contraseña", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Contraseña",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = passwordShakeOffset.value.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                ),
                supportingText = {
                    if (passwordError.isNotEmpty()) {
                        Text(text = passwordError, color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = inputConfirmPasswd,
                onValueChange = { inputConfirmPasswd = it },
                label = { Text("Confirmar Contraseña", color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirmar Contraseña",
                        tint = Color.White
                    )
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = confirmPasswordShakeOffset.value.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                ),
                supportingText = {
                    if (passwordConfirmationError.isNotEmpty()) {
                        Text(text = passwordConfirmationError, color = Color.Red)
                    }
                }
            )

            if (registerError.isNotEmpty()) {
                Text(registerError, color = Color.Red)
            }

            Button(
                onClick = {
                val isValidName = validateName(inputName).first
                val isValidEmail = validateEmail(inputEmail).first
                val isValidPassword = validatePassword(inputPasswd).first
                val isValidConfirmPassword =
                    validateConfirmPassword(inputPasswd, inputConfirmPasswd).first

                nameError = validateName(inputName).second
                emailError = validateEmail(inputEmail).second
                passwordError = validatePassword(inputPasswd).second
                passwordConfirmationError =
                    validateConfirmPassword(inputPasswd, inputConfirmPasswd).second


                if (isValidName && isValidEmail && isValidPassword && isValidConfirmPassword) {
                    auth.createUserWithEmailAndPassword(inputEmail, inputPasswd).
                    addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful){
                            val user = auth.currentUser
                            user?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(inputName)
                                    .build()
                            )?.addOnCompleteListener { profileTask ->
                                if(profileTask.isSuccessful){
                                    val datosUser = hashMapOf(
                                        "uid" to user.uid,
                                        "name" to inputName
                                    )
                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(user.uid)
                                        .set(datosUser)
                                        .addOnSuccessListener {
                                            onSuccessfulRegister()
                                        }
                                        .addOnFailureListener {
                                            registerError = "Error al guardar en Firestore"
                                        }

                                    onSuccessfulRegister()

                                } else {
                                    registerError = "Error guardando el nombre de usuario"
                                }
                            }
                        }else{
                            registerError = when(task.exception){
                                is FirebaseAuthInvalidCredentialsException -> "Correo invalido"
                                is FirebaseAuthUserCollisionException -> "Correo ya registrado"
                                else -> "Error al registrarse"

                            }
                        }
                    }

                } else {
                    registerError = "Hubo un error en el register"
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
                Text("Registrarse", fontSize = 20.sp)
            }
        }
    }
}