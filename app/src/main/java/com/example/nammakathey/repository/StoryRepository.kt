package com.example.nammakathey.repository

import android.content.Context
import com.example.nammakathey.model.District
import com.example.nammakathey.model.StoryWrapper
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var storyWrapper: StoryWrapper? = null

    suspend fun getDistricts(): List<District> = withContext(Dispatchers.IO) {
        if (storyWrapper == null) {
            val jsonString = context.assets.open("stories.json").bufferedReader().use { it.readText() }
            storyWrapper = Gson().fromJson(jsonString, StoryWrapper::class.java)
        }
        storyWrapper?.districts ?: emptyList()
    }

    suspend fun getDistrictById(id: String): District? {
        return getDistricts().find { it.districtId == id }
    }
}
