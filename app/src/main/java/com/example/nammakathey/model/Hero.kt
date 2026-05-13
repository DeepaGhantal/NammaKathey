package com.example.nammakathey.model

data class Hero(
    val name: LocalizedText,
    val description: LocalizedText,
    val imageName: String? = null, // The name of the drawable resource (e.g., "chennamma")
    val type: HeroType,
    val era: String,
    val storyPages: List<StoryPage>,
    val quiz: List<QuizQuestion>,
    val statueCoords: StatueCoords? = null
)
