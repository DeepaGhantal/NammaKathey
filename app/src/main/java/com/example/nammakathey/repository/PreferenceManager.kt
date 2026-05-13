package com.example.nammakathey.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("namma_kathey_settings", Context.MODE_PRIVATE)

    fun isFirstRun(): Boolean {
        return prefs.getBoolean("first_run", true)
    }

    fun setFirstRunCompleted() {
        prefs.edit().putBoolean("first_run", false).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "kn") ?: "kn"
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
    }
}
