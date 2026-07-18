package com.princelaghari.ailatestfinder.presentation

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.usecase.GetAiToolsUseCase
import com.princelaghari.ailatestfinder.presentation.ads.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getAiToolsUseCase: GetAiToolsUseCase,
    private val adManager: AdManager
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ai_latest_finder_prefs", Context.MODE_PRIVATE)
    private val connectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Interactive States: Favorites & Recently Viewed
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _recentlyViewedIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyViewedIds: StateFlow<List<String>> = _recentlyViewedIds.asStateFlow()

    // Network Callbacks
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = isCurrentlyConnected()
        }
    }

    init {
        // Initialize AdManager SDK
        adManager.initialize(application)

        // Load Persistent Favorites and History
        _favoriteIds.value = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        _recentlyViewedIds.value = prefs.getString("recently_viewed", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

        // Initial network check
        _isNetworkAvailable.value = isCurrentlyConnected()
        try {
            val builder = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        } catch (e: Exception) {
            _isNetworkAvailable.value = true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val aiTools: StateFlow<List<AiTool>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        getAiToolsUseCase(query = query, category = category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Expose filteredList to synchronize with UI state instantly
    val filteredList: StateFlow<List<AiTool>> = aiTools

    /**
     * Instantly suggests AI tool names as suggestions based on typed input.
     */
    val suggestions: StateFlow<List<String>> = _searchQuery.map { query ->
        if (query.trim().length < 2) emptyList()
        else {
            val lcQuery = query.lowercase().trim()
            val masterList = listOf("ChatGPT", "Claude 3.5 Sonnet", "Gemini", "Grok", "DeepSeek", "Perplexity", "Mistral", "Qwen", "Midjourney", "Google Veo", "OpenAI Sora", "Luma Dream Machine", "Suno AI", "GitHub Copilot", "Cursor AI")
            masterList.filter { it.lowercase().contains(lcQuery) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
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

    fun retryConnection() {
        _isRefreshing.value = true
        _isNetworkAvailable.value = isCurrentlyConnected()
        viewModelScope.launch {
            delay(800)
            _isRefreshing.value = false
        }
    }

    private fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Safe ignore
        }
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
