package com.example.youmanage.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(context: Context) {
    private val dataStore = context.themeDataStore
    private val THEME_KEY = booleanPreferencesKey("dark_mode_enabled")

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: false // Mặc định là Light Mode
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = enabled
        }
    }
}