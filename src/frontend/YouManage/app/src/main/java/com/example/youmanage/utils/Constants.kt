package com.example.youmanage.utils

import androidx.datastore.preferences.core.stringPreferencesKey
import org.json.JSONObject

object Constants {

    const val BASE_URL = "http://localhost:8000"
    const val WEB_SOCKET = "ws://localhost:8000/ws/"
    const val WEB_CLIENT_ID = "564163999961-mdjogd7b76hkc9fksc670ffn5702q0rg.apps.googleusercontent.com"

    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

}