package com.example.nammakathey.model

data class QuizQuestion(
    val question: LocalizedText,
    val options: List<LocalizedText>,
    val correctIndex: Int
)
