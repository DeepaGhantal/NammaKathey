package com.example.nammakathey.model

data class District(
    val districtId: String,
    val districtName: LocalizedText,
    val famousFor: LocalizedText? = null,
    val imageName: String? = null,
    val heroes: List<Hero>
)
