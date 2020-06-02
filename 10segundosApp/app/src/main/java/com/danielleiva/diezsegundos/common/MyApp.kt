package com.danielleiva.diezsegundos.common

import android.app.Application

class MyApp: Application() {
    //definimos el @Component para tener acceso a las dependencias en la aplicación
    val appComponent: AplicationComponent = DaggerAplicationComponent.create()

    companion object {
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}