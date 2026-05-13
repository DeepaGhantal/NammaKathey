package com.example.nammakathey.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("namma_kathey_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveBadge(heroName: String, districtId: String, tier: String) {
        val badges = getEarnedBadges().toMutableList()
        val newBadge = mapOf(
            "hero" to heroName,
            "district" to districtId,
            "tier" to tier,
            "date" to System.currentTimeMillis().toString()
        )
        badges.add(newBadge)
        prefs.edit().putString("badges", gson.toJson(badges)).apply()
    }

    fun getEarnedBadges(): List<Map<String, String>> {
        val json = prefs.getString("badges", null) ?: return emptyList()
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun hasBadge(heroName: String): Boolean {
        return getEarnedBadges().any { it["hero"] == heroName }
    }
}
