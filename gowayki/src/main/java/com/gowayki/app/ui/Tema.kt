package com.gowayki.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Rojo corporativo de GoWayki (#F83A34), el mismo del prototipo web
val Rojo = Color(0xFFF83A34)
val RojoOscuro = Color(0xFFC92A25)
val Fondo = Color(0xFFFFF7F6)
val Tinta = Color(0xFF231F20)

fun colorLinea(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) { Rojo }

@Composable
fun GoWaykiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Rojo, secondary = RojoOscuro, background = Fondo, surface = Color.White),
        content = content
    )
}
