package com.gowayki.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.gowayki.app.data.Favorito
import com.gowayki.app.data.GoWaykiRepository
import com.gowayki.app.data.Ruta
import com.gowayki.app.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(val cargando: Boolean = false, val error: String? = null)

class GoWaykiViewModel(private val repo: GoWaykiRepository = GoWaykiRepository()) : ViewModel() {

    // null = todavia no se sabe; Usuario() con uid vacio = sin sesion
    val usuario: StateFlow<Usuario?> = repo.usuario
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _auth = MutableStateFlow(AuthUiState())
    val auth: StateFlow<AuthUiState> = _auth.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val rutas: StateFlow<List<Ruta>> = combine(repo.rutas.catch { emit(emptyList()) }, _busqueda) { lista, q ->
        if (q.isBlank()) lista
        else lista.filter { r ->
            listOf(r.nombre, r.origen, r.destino).any { it.contains(q, ignoreCase = true) } ||
                r.paraderos.any { it.nombre.contains(q, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favoritos: StateFlow<List<Favorito>> = repo.favoritos.catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun ruta(id: String) = repo.ruta(id)

    fun onBusqueda(q: String) { _busqueda.value = q }

    // ---------- Authentication ----------
    fun iniciarSesion(email: String, password: String) = autenticar { repo.iniciarSesion(email.trim(), password) }
    fun crearCuenta(email: String, password: String) = autenticar { repo.crearCuenta(email.trim(), password) }
    fun entrarComoInvitado() = autenticar { repo.entrarComoInvitado() }
    fun cerrarSesion() = repo.cerrarSesion()

    private fun autenticar(accion: suspend () -> Unit) {
        viewModelScope.launch {
            _auth.value = AuthUiState(cargando = true)
            _auth.value = try {
                accion()
                repo.cargarRutasSiFaltan()
                AuthUiState()
            } catch (e: FirebaseAuthException) {
                AuthUiState(error = traducir(e.errorCode))
            } catch (e: Exception) {
                AuthUiState(error = e.message ?: "Algo salió mal")
            }
        }
    }

    private fun traducir(codigo: String) = when (codigo) {
        "ERROR_INVALID_EMAIL" -> "El correo no es válido"
        "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con ese correo"
        "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL", "ERROR_USER_NOT_FOUND" -> "Correo o contraseña incorrectos"
        else -> "No se pudo iniciar sesión ($codigo)"
    }

    // ---------- Firestore: favoritos ----------
    fun alternarFavorito(ruta: Ruta) {
        viewModelScope.launch {
            val existente = favoritos.value.firstOrNull { it.rutaId == ruta.id }
            if (existente != null) repo.quitarFavorito(existente.id) else repo.agregarFavorito(ruta)
        }
    }

    fun quitarFavorito(favorito: Favorito) {
        viewModelScope.launch { repo.quitarFavorito(favorito.id) }
    }
}
