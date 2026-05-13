package com.example.nammakathey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakathey.model.District
import com.example.nammakathey.model.Hero
import com.example.nammakathey.repository.BadgeManager
import com.example.nammakathey.repository.GeminiHelper
import com.example.nammakathey.repository.GeminiService
import com.example.nammakathey.repository.PreferenceManager
import com.example.nammakathey.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val badgeManager: BadgeManager,
    private val geminiService: GeminiService,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _districts = MutableStateFlow<List<District>>(emptyList())
    val districts: StateFlow<List<District>> = _districts.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<District?>(null)
    val selectedDistrict: StateFlow<District?> = _selectedDistrict.asStateFlow()

    private val _selectedHero = MutableStateFlow<Hero?>(null)
    val selectedHero: StateFlow<Hero?> = _selectedHero.asStateFlow()

    private val _language = MutableStateFlow(preferenceManager.getLanguage())
    val language: StateFlow<String> = _language.asStateFlow()

    // AI Story States
    private val _aiStory = MutableStateFlow<String?>(null)
    val aiStory: StateFlow<String?> = _aiStory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    // Ask AI States
    private val _askAiResponse = MutableStateFlow<String?>(null)
    val askAiResponse: StateFlow<String?> = _askAiResponse.asStateFlow()

    private val _isAskAiLoading = MutableStateFlow(false)
    val isAskAiLoading: StateFlow<Boolean> = _isAskAiLoading.asStateFlow()

    init {
        loadDistricts()
    }

    private fun loadDistricts() {
        viewModelScope.launch {
            _districts.value = repository.getDistricts()
        }
    }

    fun selectDistrict(districtId: String) {
        viewModelScope.launch {
            _selectedDistrict.value = repository.getDistrictById(districtId)
        }
    }

    fun selectHero(hero: Hero) {
        _selectedHero.value = hero
        _aiStory.value = null
        _aiError.value = null
        _askAiResponse.value = null
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        preferenceManager.setLanguage(lang)
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == "kn") "en" else "kn"
        setLanguage(newLang)
    }

    fun isFirstRun() = preferenceManager.isFirstRun()

    fun completeOnboarding(lang: String) {
        setLanguage(lang)
        preferenceManager.setFirstRunCompleted()
    }

    fun generateAiStory() {
        val hero = _selectedHero.value ?: return
        val heroName = if (_language.value == "kn") hero.name.kn else hero.name.en
        
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiError.value = null
            _aiStory.value = null
            
            try {
                val result = GeminiHelper.generateStory(heroName)
                
                // Check if the result indicates an error
                if (result.contains("Error") || result == "Failed to load AI Story" || result == "No story generated") {
                    _aiError.value = result
                } else {
                    _aiStory.value = result
                }
            } catch (e: Exception) {
                _aiError.value = "AI failed: ${e.message}"
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun askAi(query: String) {
        viewModelScope.launch {
            _isAskAiLoading.value = true
            val result = geminiService.generateAskAiResponse(query, _language.value)
            _askAiResponse.value = result ?: "Sorry, I couldn't find an answer. Please try again."
            _isAskAiLoading.value = false
        }
    }

    fun completeQuiz(score: Int) {
        val hero = _selectedHero.value ?: return
        val districtId = _selectedDistrict.value?.districtId ?: ""
        
        val tier = when (score) {
            3 -> "GOLD"
            2 -> "SILVER"
            else -> null
        }
        
        if (tier != null) {
            badgeManager.saveBadge(hero.name.en, districtId, tier)
        }
    }
    
    fun getEarnedBadges() = badgeManager.getEarnedBadges()

    fun hasBadge(heroNameEn: String): String? {
        return badgeManager.getEarnedBadges().find { it["hero"] == heroNameEn }?.get("tier")
    }

    fun getExploredDistrictsCount(): Int {
        return badgeManager.getEarnedBadges().map { it["district"] }.distinct().size
    }

    fun getHeroImageByName(heroNameEn: String): String? {
        return _districts.value.flatMap { it.heroes }.find { it.name.en == heroNameEn }?.imageName
    }
}
