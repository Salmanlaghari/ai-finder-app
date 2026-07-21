package com.princelaghari.ailatestfinder.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.usecase.GetAiToolsUseCase
import com.princelaghari.ailatestfinder.presentation.ads.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val application: Application,
    private val getAiToolsUseCase: GetAiToolsUseCase,
    private val adManager: AdManager
) : ViewModel() {

    private val prefs = application.getSharedPreferences("ai_latest_finder_prefs", Context.MODE_PRIVATE)
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSortOption = MutableStateFlow("Popular")
    val selectedSortOption: StateFlow<String> = _selectedSortOption.asStateFlow()

    private val _visibleItemLimit = MutableStateFlow(50)
    val visibleItemLimit: StateFlow<Int> = _visibleItemLimit.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isAppLoading = MutableStateFlow(true)
    val isAppLoading: StateFlow<Boolean> = _isAppLoading.asStateFlow()

    // Interactive States: Favorites & Recently Viewed
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _recentlyViewedIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyViewedIds: StateFlow<List<String>> = _recentlyViewedIds.asStateFlow()

    private val _isCompactMode = MutableStateFlow(false)
    val isCompactMode: StateFlow<Boolean> = _isCompactMode.asStateFlow()

    private val _defaultSearchEngine = MutableStateFlow("Google")
    val defaultSearchEngine: StateFlow<String> = _defaultSearchEngine.asStateFlow()

    // Upgraded Premium Browser Search Query & Curated Results
    private val _browserSearchQuery = MutableStateFlow("")
    val browserSearchQuery: StateFlow<String> = _browserSearchQuery.asStateFlow()

    private val _syncTrigger = MutableStateFlow(0)

    // Network Callbacks
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
            _syncTrigger.value = _syncTrigger.value + 1
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = isCurrentlyConnected()
        }
    }

    init {
        // Initialize AdManager SDK
        try {
            adManager.initialize(application)
        } catch (e: Throwable) {
            // Safe bypass
        }

        // Load Persistent Favorites and History
        _favoriteIds.value = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        _recentlyViewedIds.value = prefs.getString("recently_viewed", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        // Load Persistent Preferences
        _isCompactMode.value = prefs.getBoolean("compact_mode", false)
        _defaultSearchEngine.value = prefs.getString("search_engine", "Google") ?: "Google"

        // Initial network check
        _isNetworkAvailable.value = isCurrentlyConnected()
        try {
            val builder = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            connectivityManager?.registerNetworkCallback(builder.build(), networkCallback)
        } catch (e: Exception) {
            _isNetworkAvailable.value = true
        }

        // Smoothly hide loading screen after brief preloading delay to allow full setup
        viewModelScope.launch {
            delay(1500)
            _isAppLoading.value = false
        }
    }

    @OptIn(FlowPreview::class)
    private val debouncedSearchQuery = _searchQuery
        .debounce(250)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val aiTools: StateFlow<List<AiTool>> = combine(
        debouncedSearchQuery,
        _selectedCategory,
        _syncTrigger
    ) { query, category, _ ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        getAiToolsUseCase(query = query, category = category)
    }.flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Expose filteredList to synchronize with UI state instantly, applying sorting options and pagination limits
    val filteredList: StateFlow<List<AiTool>> = combine(
        aiTools,
        _selectedSortOption,
        _visibleItemLimit
    ) { rawList, sortOption, limit ->
        val sorted = when (sortOption) {
            "Trending" -> rawList.sortedByDescending { it.status == "Trending" }
            "Newest" -> rawList.sortedByDescending { it.launchYear }
            "Popular" -> rawList.sortedByDescending { it.status == "Popular" }
            "A-Z" -> rawList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            else -> rawList
        }
        sorted.take(limit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Instantly suggests AI tool names as suggestions based on typed input.
     * Automatically hides suggestions once an exact match is typed or selected to keep results visible.
     */
    val suggestions: StateFlow<List<String>> = _searchQuery.map { query ->
        val trimmed = query.trim()
        if (trimmed.length < 2) emptyList()
        else {
            val lcQuery = trimmed.lowercase()
            val masterList = listOf("ChatGPT", "Claude 3.5 Sonnet", "Gemini", "Grok", "DeepSeek", "Perplexity", "Mistral", "Qwen", "Midjourney", "Google Veo", "OpenAI Sora", "Luma Dream Machine", "Suno AI", "GitHub Copilot", "Cursor AI")

            // If the search query exactly matches any of the suggestions, hide the suggestions list
            val exactMatch = masterList.any { it.equals(trimmed, ignoreCase = true) }
            if (exactMatch) {
                emptyList()
            } else {
                masterList.filter { it.lowercase().contains(lcQuery) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * Combines browserSearchQuery and aiTools dataset to filter strictly AI-related platforms.
     */
    val browserResults: StateFlow<List<AiTool>> = combine(
        _browserSearchQuery,
        aiTools
    ) { query, tools ->
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            // Default curated premium results shown initially
            listOf(
                AiTool(
                    id = "b-gemini", name = "Google Gemini 3", category = "Text AI",
                    description = "Official announcement, multi-modal features & documentation.",
                    imageUrl = "", toolUrl = "https://gemini.google.com", pricing = "Free",
                    platforms = listOf("Web"), developer = "Google", company = "Google",
                    status = "Trending", launchYear = "2024", tags = listOf("google", "gemini"),
                    alternatives = emptyList()
                ),
                AiTool(
                    id = "b-claude", name = "Claude Opus 4.8", category = "Text AI",
                    description = "Anthropic core model card, advanced reasoning benchmarks & system cards.",
                    imageUrl = "", toolUrl = "https://claude.ai", pricing = "Free",
                    platforms = listOf("Web"), developer = "Anthropic", company = "Anthropic",
                    status = "Trending", launchYear = "2024", tags = listOf("anthropic", "claude"),
                    alternatives = emptyList()
                ),
                AiTool(
                    id = "b-midjourney", name = "Midjourney v7", category = "Image AI",
                    description = "New style reference guide, prompt formats, and visual parameter tuning.",
                    imageUrl = "", toolUrl = "https://midjourney.com", pricing = "Paid",
                    platforms = listOf("Web"), developer = "Midjourney Lab", company = "Midjourney",
                    status = "Trending", launchYear = "2024", tags = listOf("midjourney", "art"),
                    alternatives = emptyList()
                ),
                AiTool(
                    id = "b-deepseek", name = "DeepSeek R1", category = "Text AI",
                    description = "Reasoning-focused open weights model deployment and API guides.",
                    imageUrl = "", toolUrl = "https://deepseek.com", pricing = "Free",
                    platforms = listOf("Web"), developer = "DeepSeek", company = "DeepSeek",
                    status = "Trending", launchYear = "2025", tags = listOf("deepseek", "r1"),
                    alternatives = emptyList()
                ),
                AiTool(
                    id = "b-sora", name = "OpenAI Sora v2", category = "Video AI",
                    description = "Cinematic high-fidelity video generation documentation and prompts.",
                    imageUrl = "", toolUrl = "https://openai.com/sora", pricing = "Paid",
                    platforms = listOf("Web"), developer = "OpenAI", company = "OpenAI",
                    status = "Trending", launchYear = "2024", tags = listOf("openai", "sora"),
                    alternatives = emptyList()
                ),
                AiTool(
                    id = "b-suno", name = "Suno AI Music v4", category = "Music AI",
                    description = "High-fidelity generation guides, custom prompt lyrics and audio presets.",
                    imageUrl = "", toolUrl = "https://suno.com", pricing = "Free",
                    platforms = listOf("Web"), developer = "Suno", company = "Suno",
                    status = "Trending", launchYear = "2024", tags = listOf("suno", "music"),
                    alternatives = emptyList()
                )
            )
        } else {
            // Apply filtering logic to find matching AI tools / models / docs from the global 1,020 dataset
            tools.filter { tool ->
                tool.name.lowercase().contains(trimmed) ||
                tool.description.lowercase().contains(trimmed) ||
                tool.category.lowercase().contains(trimmed) ||
                tool.tags.any { it.lowercase().contains(trimmed) }
            }
        }
    }.flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        // Reset limit on search
        _visibleItemLimit.value = 50
    }

    fun onBrowserSearchQueryChanged(newQuery: String) {
        _browserSearchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        // Reset limit on category switch
        _visibleItemLimit.value = 50
    }

    fun onSortOptionSelected(sortOption: String) {
        _selectedSortOption.value = sortOption
    }

    fun loadMoreItems() {
        _visibleItemLimit.value = _visibleItemLimit.value + 50
    }

    fun toggleFavorite(toolId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(toolId)) {
            current.remove(toolId)
        } else {
            current.add(toolId)
        }
        _favoriteIds.value = current
        prefs.edit().putStringSet("favorites", current).apply()
    }

    fun addToRecentlyViewed(toolId: String) {
        val current = _recentlyViewedIds.value.toMutableList()
        current.remove(toolId) // Avoid duplicates in history list
        current.add(0, toolId) // Insert at top of history
        if (current.size > 10) {
            current.removeAt(current.size - 1) // Keep top 10 items
        }
        _recentlyViewedIds.value = current
        prefs.edit().putString("recently_viewed", current.joinToString(",")).apply()
    }

    fun toggleCompactMode(enabled: Boolean) {
        _isCompactMode.value = enabled
        prefs.edit().putBoolean("compact_mode", enabled).apply()
    }

    fun selectSearchEngine(engine: String) {
        _defaultSearchEngine.value = engine
        prefs.edit().putString("search_engine", engine).apply()
    }

    fun clearAllHistory() {
        _recentlyViewedIds.value = emptyList()
        prefs.edit().remove("recently_viewed").apply()
    }

    fun clearAllFavorites() {
        _favoriteIds.value = emptySet()
        prefs.edit().remove("favorites").apply()
    }

    fun retryConnection() {
        _isRefreshing.value = true
        val connected = isCurrentlyConnected()
        _isNetworkAvailable.value = connected
        if (connected) {
            _syncTrigger.value = _syncTrigger.value + 1
        }
        viewModelScope.launch {
            delay(800)
            _isRefreshing.value = false
        }
    }

    fun getBannerAdUnitId(): String {
        return adManager.getBannerAdUnitId()
    }

    private fun isCurrentlyConnected(): Boolean {
        val manager = connectivityManager ?: return true
        val activeNetwork = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Safe ignore
        }
    }
}
