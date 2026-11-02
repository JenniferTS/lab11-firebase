package com.gowayki.app

import android.app.Application
import com.google.firebase.FirebaseApp

// Se conecta al proyecto real de Firebase "gowayki-lab11" (google-services.json)
class GoWaykiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
