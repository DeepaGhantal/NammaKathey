package com.example.nammakathey.repository

import android.util.Log
import com.example.nammakathey.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val config = generationConfig {
        temperature = 0.8f
        topK = 40
        topP = 0.95f
    }

    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
    )

    private fun getModel(name: String) = GenerativeModel(
        modelName = name,
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config,
        safetySettings = safetySettings
    )

    suspend fun generateStory(heroName: String, districtName: String, language: String): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isBlank() || apiKey == "null" || apiKey.length < 10) {
            Log.e("GeminiService", "INVALID API KEY: '${apiKey}'.")
            return@withContext "ERROR_API_KEY_MISSING"
        }

        val prompt = if (language == "kn") {
            "ನೀವು ಒಬ್ಬ ಕಥೆಗಾರರು. $districtName ಜಿಲ್ಲೆಯ ವೀರರಾದ $heroName ಅವರ ಬಗ್ಗೆ 5 ಪ್ಯಾರಾಗ್ರಾಫ್‌ಗಳ ಸ್ಪೂರ್ತಿದಾಯಕ ಕಥೆಯನ್ನು ಬರೆಯಿರಿ. ಕೇವಲ ಕಥೆಯನ್ನು ಮಾತ್ರ ನೀಡಿ."
        } else {
            "You are a storyteller. Write an inspiring 5-paragraph story about $heroName from $districtName district. Return ONLY the story text."
        }

        val modelsToTry = listOf("gemini-1.5-flash", "gemini-1.5-pro", "gemini-pro")
        var lastError: String? = null
        
        for (modelName in modelsToTry) {
            try {
                Log.d("GeminiService", "Attempting $modelName...")
                val model = getModel(modelName)
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            } catch (e: Exception) {
                lastError = e.message
                Log.e("GeminiService", "$modelName failed: ${e.message}")
                if (e.message?.contains("403") == true || e.message?.contains("API_KEY_INVALID") == true) {
                    return@withContext "ERROR_API_KEY_UNAUTHORIZED"
                }
            }
        }
        
        return@withContext "ERROR_FAILED_ALL_MODELS: $lastError"
    }

    suspend fun generateAskAiResponse(query: String, language: String): String? = withContext(Dispatchers.IO) {
        try {
            val model = getModel("gemini-1.5-flash")
            val response = model.generateContent(query)
            response.text ?: "I couldn't generate a response."
        } catch (e: Exception) {
            Log.e("GeminiService", "Ask AI Error: ${e.message}")
            try {
                getModel("gemini-pro").generateContent(query).text
            } catch (e2: Exception) {
                null
            }
        }
    }
}
