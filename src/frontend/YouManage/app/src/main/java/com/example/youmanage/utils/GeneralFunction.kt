package com.example.youmanage.utils

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.absoluteValue
import kotlin.random.Random

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

fun randomVibrantLightColor(): Color {
    var red: Int
    var green: Int
    var blue: Int

    do {
        red = Random.nextInt(128, 256)
        green = Random.nextInt(128, 256)
        blue = Random.nextInt(128, 256)
    } while ((red - green).absoluteValue < 50 && (green - blue).absoluteValue < 50 && (blue - red).absoluteValue < 50)

    return Color(red, green, blue)
}

// Process Access Token
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
fun formatToRelativeTime(isoString: String): String {
    val parsedTime = Instant.parse(isoString)
    val now = Instant.now()
    val seconds = ChronoUnit.SECONDS.between(parsedTime, now)
    val minutes = ChronoUnit.MINUTES.between(parsedTime, now)
    val hours = ChronoUnit.HOURS.between(parsedTime, now)
    val days = ChronoUnit.DAYS.between(parsedTime, now)

    return when {
        seconds <= 1L -> "Just now"
        seconds < 60 -> "$seconds seconds ago"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
    }
}

