package com.gowayki.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import com.gowayki.app.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GoWaykiTheme { GoWaykiRaiz() } }
    }
}

@androidx.compose.runtime.Composable
fun GoWaykiRaiz(vm: GoWaykiViewModel = viewModel()) {
    val usuario by vm.usuario.collectAsState()
    when {
        usuario == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        usuario!!.uid.isEmpty() -> LoginScreen(vm)
        else -> AppConSesion(vm)
    }
}

@androidx.compose.runtime.Composable
private fun AppConSesion(vm: GoWaykiViewModel) {
    val nav = rememberNavController()
    val ruta = nav.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        bottomBar = {
            if (ruta?.startsWith("detalle") != true) NavigationBar {
                listOf(Triple("rutas", "Rutas", Icons.Filled.DirectionsBus), Triple("favoritas", "Favoritas", Icons.Filled.Star), Triple("perfil", "Perfil", Icons.Filled.Person))
                    .forEach { (destino, texto, icono) ->
                        NavigationBarItem(
                            selected = ruta == destino,
                            onClick = { nav.navigate(destino) { popUpTo("rutas"); launchSingleTop = true } },
                            icon = { Icon(icono, contentDescription = null) },
                            label = { Text(texto) }
                        )
                    }
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = "rutas", modifier = Modifier.padding(padding)) {
            composable("rutas") { RutasScreen(vm) { id -> nav.navigate("detalle/$id") } }
            composable("favoritas") { FavoritasScreen(vm) { id -> nav.navigate("detalle/$id") } }
            composable("perfil") { PerfilScreen(vm) }
            composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                RutaDetalleScreen(vm, it.arguments!!.getString("id")!!) { nav.popBackStack() }
            }
        }
    }
}
