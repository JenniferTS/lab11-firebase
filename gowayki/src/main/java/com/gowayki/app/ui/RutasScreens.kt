package com.gowayki.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowayki.app.data.Ruta
import java.util.Locale

private val Amarillo = Color(0xFFFFB300)

// ---------- Lista de rutas (Firestore, en tiempo real) ----------
@Composable
fun RutasScreen(vm: GoWaykiViewModel, onAbrir: (String) -> Unit) {
    val rutas by vm.rutas.collectAsState()
    val favoritos by vm.favoritos.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val usuario by vm.usuario.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().background(Fondo),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            val nombre = usuario?.email?.substringBefore("@") ?: "invitada"
            Text("Hola, $nombre 👋", color = Tinta.copy(alpha = 0.7f))
            Text("¿A dónde vas hoy?", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Tinta)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                busqueda, vm::onBusqueda,
                placeholder = { Text("Busca una ruta, distrito o paradero") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true, shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (rutas.isEmpty()) item {
            Text(if (busqueda.isBlank()) "Cargando rutas..." else "No encontré rutas con \"$busqueda\"", color = Color.Gray)
        }
        items(rutas, key = { it.id }) { ruta ->
            TarjetaRuta(ruta, esFavorita = favoritos.any { it.rutaId == ruta.id }, onFavorito = { vm.alternarFavorito(ruta) }, onClick = { onAbrir(ruta.id) })
        }
    }
}

@Composable
private fun TarjetaRuta(ruta: Ruta, esFavorita: Boolean, onFavorito: () -> Unit, onClick: () -> Unit) {
    val linea = colorLinea(ruta.colorLinea)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(linea), contentAlignment = Alignment.Center) {
                Text(ruta.nombre.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(ruta.nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Tinta)
                Text("${ruta.origen} → ${ruta.destino}", fontSize = 13.sp, color = Tinta.copy(alpha = 0.7f))
                Text(
                    "⏱ ${ruta.tiempoEstimadoMinutos} min · S/ ${String.format(Locale.US, "%.2f", ruta.costoSoles)} · ${ruta.paraderos.size} paraderos",
                    fontSize = 12.sp, color = linea, fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onFavorito) {
                Icon(
                    if (esFavorita) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (esFavorita) "Quitar de favoritas ${ruta.nombre}" else "Agregar a favoritas ${ruta.nombre}",
                    tint = if (esFavorita) Amarillo else Color.Gray
                )
            }
        }
    }
}

// ---------- Detalle de una ruta con sus paraderos ----------
@Composable
fun RutaDetalleScreen(vm: GoWaykiViewModel, id: String, onVolver: () -> Unit) {
    val ruta by remember(id) { vm.ruta(id) }.collectAsState(initial = null)
    val favoritos by vm.favoritos.collectAsState()
    val r = ruta ?: run {
        Box(Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val linea = colorLinea(r.colorLinea)
    val esFavorita = favoritos.any { it.rutaId == r.id }

    LazyColumn(Modifier.fillMaxSize().background(Fondo)) {
        item {
            Column(Modifier.fillMaxWidth().background(linea).padding(bottom = 20.dp)) {
                IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White) }
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Text(r.nombre, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("${r.origen} → ${r.destino}", color = Color.White.copy(alpha = 0.95f))
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Pastilla("⏱ ${r.tiempoEstimadoMinutos} min"); Pastilla("S/ ${String.format(Locale.US, "%.2f", r.costoSoles)}"); Pastilla("${r.paraderos.size} paraderos")
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(20.dp)) {
                Text(r.descripcion, color = Tinta)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { vm.alternarFavorito(r) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (esFavorita) Color.White else Rojo, contentColor = if (esFavorita) Rojo else Color.White),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Rojo, RoundedCornerShape(50))
                ) {
                    Icon(if (esFavorita) Icons.Filled.Star else Icons.Outlined.StarBorder, contentDescription = null)
                    Text(if (esFavorita) "  Quitar de mis favoritas" else "  Guardar en mis favoritas")
                }
                Spacer(Modifier.height(16.dp))
                Text("Paraderos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Tinta)
            }
        }
        itemsIndexed(r.paraderos.sortedBy { it.orden }) { i, p ->
            val ultimo = i == r.paraderos.size - 1
            Row(Modifier.padding(horizontal = 20.dp).height(IntrinsicSize.Min)) {
                Box(Modifier.width(24.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                    if (!ultimo) Box(Modifier.width(4.dp).fillMaxHeight().padding(top = 12.dp).background(linea))
                    Box(Modifier.size(16.dp).clip(CircleShape).background(Color.White).border(4.dp, linea, CircleShape))
                }
                Column(Modifier.padding(start = 12.dp, bottom = 18.dp)) {
                    Text(p.nombre, fontWeight = if (i == 0 || ultimo) FontWeight.Bold else FontWeight.Normal, color = Tinta)
                    Text(String.format(Locale.US, "%.4f, %.4f", p.latitud, p.longitud), fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun Pastilla(t: String) {
    Text(t, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.25f)).padding(horizontal = 10.dp, vertical = 4.dp))
}

// ---------- Mis favoritas (solo las del usuario, en tiempo real) ----------
@Composable
fun FavoritasScreen(vm: GoWaykiViewModel, onAbrir: (String) -> Unit) {
    val favoritos by vm.favoritos.collectAsState()
    LazyColumn(
        Modifier.fillMaxSize().background(Fondo),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Mis rutas favoritas", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Tinta)
            Text("Se guardan en tu cuenta de GoWayki", color = Color.Gray)
        }
        if (favoritos.isEmpty()) item { Text("Todavía no tienes favoritas. Toca la ⭐ de una ruta para guardarla.") }
        items(favoritos.sortedBy { it.creadoEn }, key = { it.id }) { f ->
            Card(
                shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().clickable { onAbrir(f.rutaId) }
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Amarillo)
                    Text(f.nombreRuta, fontWeight = FontWeight.Bold, color = Tinta, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    TextButton(onClick = { vm.quitarFavorito(f) }) { Text("Quitar", color = Rojo) }
                }
            }
        }
    }
}

// ---------- Perfil ----------
@Composable
fun PerfilScreen(vm: GoWaykiViewModel) {
    val usuario by vm.usuario.collectAsState()
    val favoritos by vm.favoritos.collectAsState()
    Column(Modifier.fillMaxSize().background(Fondo).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Mi perfil", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Tinta)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(usuario?.email ?: "Cuenta de invitada", fontWeight = FontWeight.Bold, color = Tinta)
                Text(if (usuario?.esInvitado == true) "Sesión anónima" else "Sesión con correo y contraseña", color = Color.Gray, fontSize = 13.sp)
                Text("Rutas favoritas: ${favoritos.size}", color = Tinta)
            }
        }
        OutlinedButton(onClick = vm::cerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión", color = Rojo) }
    }
}
