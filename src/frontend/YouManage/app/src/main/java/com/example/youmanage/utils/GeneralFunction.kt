package com.example.youmanage.utils

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.time.LocalDate
import java.util.Locale
import kotlin.io.encoding.ExperimentalEncodingApi

fun extractMessages(jsonResponse: String): String {
    val messages = mutableListOf<String>()
    val jsonObject = JSONObject(jsonResponse)

    for (key in jsonObject.keys()) {
        val messageArray = jsonObject.getJSONArray(key)
        for (i in 0 until messageArray.length()) {
            messages.add(messageArray.getString(i))
        }
    }
    return messages.joinToString(separator = "\n")
}

fun randomColor(index: Int): Int {
    val colors = listOf(
        Color.Red.copy(alpha = 0.6f),
        Color.Blue.copy(alpha = 0.6f),
        Color.Green.copy(alpha = 0.6f),
        Color.Yellow.copy(alpha = 0.6f),
        Color.Magenta.copy(alpha = 0.6f),
        Color.Cyan.copy(alpha = 0.6f),
        Color(0xFFF44336).copy(alpha = 0.6f)
    )

    return colors[index % colors.size].hashCode()
}

// Process Access Token
@OptIn(ExperimentalEncodingApi::class)
fun decodeJWT(token: String): JSONObject? {
    return try {
        val payload = token.split(".")[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedPayload = String(decodedBytes)
        JSONObject(decodedPayload)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun isTokenExpired(token: String): Boolean {
    val payload = decodeJWT(token) ?: return true
    val exp = payload.optLong("exp", 0L)
    Log.d("Token Exp", payload.toString())
    Log.d("Token Exp", exp.toString())
    val currentTime = System.currentTimeMillis() / 1000
    Log.d("Current Time", currentTime.toString())
    return currentTime > exp
}

@RequiresApi(Build.VERSION_CODES.O)
fun openDatePicker(
    context: Context,
    onDateSetListener: DatePickerDialog.OnDateSetListener,
    date: LocalDate
    ){
    Locale.setDefault(Locale.ENGLISH)
    val datePickerDialog = DatePickerDialog(
        context,
        onDateSetListener,
        date.year,
        date.monthValue,
        date.dayOfMonth
    )

    datePickerDialog.show()
}