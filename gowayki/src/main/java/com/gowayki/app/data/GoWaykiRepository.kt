package com.gowayki.app.data

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await

// Unico lugar de la app que habla con Firebase (Authentication + Cloud Firestore)
class GoWaykiRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // ---------- Authentication ----------
    // Usuario() con uid vacio = no hay sesion
    val usuario: Flow<Usuario> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser?.let { Usuario(it.uid, it.email, it.isAnonymous) } ?: Usuario())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun iniciarSesion(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    // Si ya entro como invitada, se vincula la cuenta para no perder sus favoritas
    suspend fun crearCuenta(email: String, password: String) {
        val actual = auth.currentUser
        if (actual != null && actual.isAnonymous) {
            actual.linkWithCredential(EmailAuthProvider.getCredential(email, password)).await()
        } else {
            auth.createUserWithEmailAndPassword(email, password).await()
        }
    }

    suspend fun entrarComoInvitado() {
        auth.signInAnonymously().await()
    }

    fun cerrarSesion() = auth.signOut()

    // ---------- Firestore: rutas ----------
    val rutas: Flow<List<Ruta>> = db.collection(RUTAS).orderBy("nombre").dataObjects()

    fun ruta(id: String): Flow<Ruta?> = db.collection(RUTAS).document(id).dataObjects()

    // Si la coleccion esta vacia, sube las rutas del prototipo web (solo la primera vez)
    suspend fun cargarRutasSiFaltan() {
        val existentes = db.collection(RUTAS).limit(1).get().await()
        if (existentes.isEmpty) {
            val lote = db.batch()
            DatosIniciales.rutas.forEach { r -> lote.set(db.collection(RUTAS).document(r.id), r.copy(id = "")) }
            lote.commit().await()
        }
    }

    // ---------- Firestore: favoritos (en tiempo real, solo los del usuario) ----------
    @OptIn(ExperimentalCoroutinesApi::class)
    val favoritos: Flow<List<Favorito>> = usuario.flatMapLatest { u ->
        if (u.uid.isEmpty()) flowOf(emptyList())
        else db.collection(FAVORITOS).whereEqualTo("userId", u.uid).dataObjects()
    }

    suspend fun agregarFavorito(ruta: Ruta) {
        val uid = auth.currentUser?.uid ?: return
        db.collection(FAVORITOS).add(
            Favorito(userId = uid, rutaId = ruta.id, nombreRuta = ruta.nombre, creadoEn = System.currentTimeMillis())
        ).await()
    }

    suspend fun quitarFavorito(favoritoId: String) {
        db.collection(FAVORITOS).document(favoritoId).delete().await()
    }

    companion object {
        const val RUTAS = "rutas"
        const val FAVORITOS = "favoritos"
    }
}
