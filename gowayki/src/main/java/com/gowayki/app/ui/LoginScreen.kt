package com.gowayki.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(vm: GoWaykiViewModel) {
    val estado by vm.auth.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var modoRegistro by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Fondo)) {
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                .background(Brush.verticalGradient(listOf(Rojo, RojoOscuro)))
                .statusBarsPadding()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.DirectionsBus, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            Text("GoWayki", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("Muévete por Arequipa sin perderte", color = Color.White.copy(alpha = 0.9f))
        }
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (modoRegistro) "Crear cuenta" else "Iniciar sesión", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Tinta)
            OutlinedTextField(
                email, { email = it }, label = { Text("Correo") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                password, { password = it }, label = { Text("Contraseña") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth()
            )
            estado.error?.let { Text(it, color = Rojo, fontSize = 13.sp) }
            Button(
                onClick = { if (modoRegistro) vm.crearCuenta(email, password) else vm.iniciarSesion(email, password) },
                enabled = !estado.cargando && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text(if (estado.cargando) "Un momento..." else if (modoRegistro) "Crear mi cuenta" else "Entrar") }
            TextButton(onClick = { modoRegistro = !modoRegistro }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (modoRegistro) "Ya tengo cuenta, iniciar sesión" else "¿No tienes cuenta? Regístrate")
            }
            HorizontalDivider()
            OutlinedButton(onClick = vm::entrarComoInvitado, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) {
                Text("Entrar como invitada")
            }
        }
    }
}
