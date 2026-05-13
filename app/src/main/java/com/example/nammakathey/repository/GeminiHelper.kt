package com.example.nammakathey.repository

import android.util.Log
import com.example.nammakathey.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

object GeminiHelper {

    suspend fun generateStory(heroName: String): String {
        // Log the key length for debugging
        Log.d("Gemini", "API Key length: ${BuildConfig.GEMINI_API_KEY.length}")

        if (BuildConfig.GEMINI_API_KEY.isBlank() || BuildConfig.GEMINI_API_KEY == "null") {
            return "Error: Gemini API Key is missing. Please check your local.properties file and Rebuild Project."
        }

        /**
         * Using "gemini-2.5-flash" as seen in your Google AI Studio screenshots.
         * If you still see a 404 for "gemini-1.5-flash", please REBUILD your project
         * to ensure this change is deployed to your device.
         */
        val config = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config
        )

        val prompt = """
            You are a storyteller for children.
            Write a short inspiring story about $heroName from Karnataka.
            Rules:
            - Simple English
            - Maximum 100 words
            - Child friendly
            - Inspiring
        """.trimIndent()

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "AI returned an empty response."
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Unknown technical error"
            Log.e("Gemini", "Technical Error: $errorMsg", e)
            
            // Map the error for better debugging on the device screen
            when {
                errorMsg.contains("404") || errorMsg.contains("not found") -> 
                    "TECHNICAL ERROR: Model not found. If the error mentions 'gemini-1.5-flash', you MUST Clean and Rebuild your project."
                errorMsg.contains("MissingFieldException") -> 
                    "TECHNICAL ERROR: Server response mismatch. This happens when the model name is incorrect or the API key doesn't have access to this specific model."
                else -> "TECHNICAL ERROR: $errorMsg"
            }
        }
    }
}
