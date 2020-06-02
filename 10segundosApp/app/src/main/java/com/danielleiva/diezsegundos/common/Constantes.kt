package com.danielleiva.diezsegundos.common

class Constantes {
    companion object {
        //http://10.0.2.2:9000/
        val TMDBAPI_BASE_URL: String = "http://10.0.2.2:9000/"
        val TIMEOUT_INMILIS = 30000L
        val SHARED_PREFS_FILE: String? = "SHARED_PREFERENCES_FILE"
        val TOKEN = "TOKEN"

        fun getRandomAvatar(nombre : String) : String{
            return "https://api.adorable.io/avatars/150/$nombre.png"
        }
    }


}