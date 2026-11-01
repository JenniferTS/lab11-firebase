package com.gowayki.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoWaykiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Firebase Emulator Suite de mi PC (10.0.2.2 = la PC vista desde el emulador de Android).
        // Con el proyecto real de la consola de Firebase, se quitan estas dos lineas.
        FirebaseAuth.getInstance().useEmulator(HOST, 9099)
        FirebaseFirestore.getInstance().useEmulator(HOST, 8085)
    }

    companion object {
        const val HOST = "10.0.2.2"
    }
}
