package com.gowayki.app.data

import com.google.firebase.firestore.DocumentId

// Mismos datos que la tabla "rutas" y "paraderos" del prototipo web de GoWayki
data class Paradero(
    val nombre: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val orden: Int = 0
)

data class Ruta(
    @DocumentId val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val origen: String = "",
    val destino: String = "",
    val tiempoEstimadoMinutos: Int = 0,
    val costoSoles: Double = 0.0,
    val colorLinea: String = "#F83A34",
    val paraderos: List<Paradero> = emptyList()
)

// Una ruta favorita de un usuario (cada usuario solo ve las suyas)
data class Favorito(
    @DocumentId val id: String = "",
    val userId: String = "",
    val rutaId: String = "",
    val nombreRuta: String = "",
    val creadoEn: Long = 0
)

data class Usuario(val uid: String = "", val email: String? = null, val esInvitado: Boolean = true)
