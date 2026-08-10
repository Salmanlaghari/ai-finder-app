package com.princelaghari.ailatestfinder.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.presentation.components.*
import com.princelaghari.ailatestfinder.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request 120Hz Refresh Rate on supported devices for Buttery-Smooth 120 FPS scrolling experience
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                // Method 1: Display Mode Configuration using preferredDisplayModeId
                window.windowManager.defaultDisplay?.let { display ->
                    val supportedModes = display.supportedModes
                    val maxMode = supportedModes.maxByOrNull { it.refreshRate }
                    if (maxMode != null && maxMode.refreshRate >= 90f) {
                        val params = window.attributes
                        params.preferredDisplayModeId = maxMode.modeId
                        window.attributes = params
                    }
                }
            } else {
                // Method 2: Legacy Display Mode attributes
                val params = window.attributes
                params.preferredRefreshRate = 120f
                window.attributes = params
            }
        } catch (e: Exception) {
            // Safe fallback on unsupported emulators or older devices
        }

        setContent {
            AiLatestFinderTheme {
                val isAppLoading by viewModel.isAppLoading.collectAsState()
                val isOnline by viewModel.isNetworkAvailable.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgColor
                ) {
                    if (isAppLoading) {
                        PremiumSplashScreen()
                    } else if (isOnline) {
                        MainScreen(viewModel)
                    } else {
                        NetworkErrorScreen(
                            isRefreshing = isRefreshing,
                            onRetry = { viewModel.retryConnection() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val aiTools by viewModel.filteredList.collectAsState()
    val rawAiTools by viewModel.aiTools.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val recentlyViewedIds by viewModel.recentlyViewedIds.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()

    // Dynamic Live Stats from real/curated database sources
    val totalToolsCount by viewModel.totalToolsCount.collectAsState()
    val categoriesCount by viewModel.categoriesCount.collectAsState()
    val addedTodayCount by viewModel.addedTodayCount.collectAsState()

    // Upgraded Premium Browser States
    val browserSearchQuery by viewModel.browserSearchQuery.collectAsState()
    val browserResults by viewModel.browserResults.collectAsState()

    // Bottom Navigation View Mode: Home, Browser, Saved, Profile
    var activeViewMode by remember { mutableStateOf("Home") }

    // Dynamic Navigation Transitions
    var showExplorerList by remember { mutableStateOf(false) }
    var explorerListHeader by remember { mutableStateOf("AI Tools Explorer") }

    // Dialog state controllers
    var selectedToolForDetail by remember { mutableStateOf<AiTool?>(null) }
    var activeBrowserUrl by remember { mutableStateOf<String?>(null) }
    var showExternalSearchPanel by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    val isCompactMode by viewModel.isCompactMode.collectAsState()
    val defaultSearchEngine by viewModel.defaultSearchEngine.collectAsState()

    // Scroll pagination triggers
    val listState = rememberLazyListState()
    val visibleItemLimit by viewModel.visibleItemLimit.collectAsState()
    val shouldLoadMore = remember(visibleItemLimit) {
        derivedStateOf {
            val totalCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                totalCount >= visibleItemLimit &&
                lastVisibleItem.index >= totalCount - 4
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMoreItems()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Main View Port routing based on active bottom navigation mode
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave exact space for floating navigation bar
        ) {
            // Sticky AdBanner at the very top of the screen
            AdBanner(adUnitId = viewModel.getBannerAdUnitId())

            when (activeViewMode) {
                "Home" -> {
                    // Check if user clicked See All, category chips, or typed a query to show Full List explorer
                    if (!showExplorerList && searchQuery.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Header Greet Panel
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 26.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Welcome back 👋",
                                        fontSize = 12.5.sp,
                                        color = TextDimColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Explore AI Tools",
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextColor,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quick-launch Live Web Browser Shortcut Button
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(CardColor)
                                            .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                            .clickable { activeViewMode = "Browser" },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🌐",
                                            fontSize = 16.sp
                                        )
                                    }

                                    // Bell Button to open About Info Panel
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(CardColor)
                                            .border(1.dp, Card2Color, RoundedCornerShape(14.dp))
                                            .clickable { showAbout = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🔔",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }

                            // Stats Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Total Tools Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .border(1.dp, Card2Color, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = totalToolsCount.toString(),
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AmberAccent
                                        )
                                        Text(
                                            text = "AI Tools Listed",
                                            fontSize = 10.5.sp,
                                            color = TextDimColor,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                // Categories Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .border(1.dp, Card2Color, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = categoriesCount.toString(),
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = VioletAccent
                                        )
                                        Text(
                                            text = "Categories",
                                            fontSize = 10.5.sp,
                                            color = TextDimColor,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                // Added Today Card
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .border(1.dp, Card2Color, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = addedTodayCount.toString(),
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextColor
                                        )
                                        Text(
                                            text = "Added Today",
                                            fontSize = 10.5.sp,
                                            color = TextDimColor,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Search input Section
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 20.dp)
                            ) {
                                PulsingSearchBox(
                                    query = searchQuery,
                                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                                    onExternalSearchClicked = { showExternalSearchPanel = true }
                                )
                            }

                            // Horizontal Category filters
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 18.dp)
                            ) {
                                CategoryChips(
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { category ->
                                        viewModel.onCategorySelected(category)
                                        explorerListHeader = if (category == "All") "AI Tools Explorer" else "$category Directory"
                                        showExplorerList = true
                                    }
                                )
                            }

                            // Shimmering branding row
                            ShimmerBrandingText(modifier = Modifier.padding(vertical = 4.dp))

                            // Featured This Week Carousel
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Featured This Week",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextColor
                                    )
                                    Text(
                                        text = "See All",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberAccent,
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onCategorySelected("All")
                                                viewModel.onSortOptionSelected("Trending")
                                                explorerListHeader = "Featured This Week"
                                                showExplorerList = true
                                            }
                                            .padding(8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val featuredList = listOf(
                                        Triple("Claude Opus", "Advanced reasoning & long context", Color(0xFF3B2D6B)),
                                        Triple("Runway Gen-4", "Cinematic AI video generation", Color(0xFF6B4A1F))
                                    )
                                    items(featuredList) { (name, desc, bgCol) ->
                                        Box(
                                            modifier = Modifier
                                                .width(220.dp)
                                                .height(130.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(
                                                            bgCol,
                                                            bgCol.copy(alpha = 0.3f),
                                                            CardColor
                                                        )
                                                    )
                                                )
                                                .border(1.dp, Card2Color, RoundedCornerShape(20.dp))
                                                .clickable {
                                                    val tool = rawAiTools.find { it.name.contains(name.split(" ")[0]) }
                                                    if (tool != null) {
                                                        viewModel.addToRecentlyViewed(tool.id)
                                                        selectedToolForDetail = tool
                                                    } else {
                                                        activeBrowserUrl = if (name.contains("Claude")) "https://claude.ai" else "https://runwayml.com"
                                                    }
                                                }
                                                .padding(18.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (name.contains("Claude")) "Editor's Pick" else "New",
                                                    color = Color.White,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.align(Alignment.BottomStart)
                                            ) {
                                                Text(
                                                    text = name,
                                                    color = Color.White,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                                Text(
                                                    text = desc,
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Top Rated Tools 2x2 Grid
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 26.dp, bottom = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Top Rated Tools",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextColor
                                    )
                                    Text(
                                        text = "See All",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberAccent,
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onCategorySelected("All")
                                                viewModel.onSortOptionSelected("Popular")
                                                explorerListHeader = "Top Rated Tools"
                                                showExplorerList = true
                                            }
                                            .padding(8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                val ratingTiles = listOf(
                                    Triple("Gemini 1.5", "Multimodal AI", "G"),
                                    Triple("Perplexity", "AI Search", "P"),
                                    Triple("Phind", "Dev Search", "Ph"),
                                    Triple("DALL-E 3", "Image AI", "D3")
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    ratingTiles.chunked(2).forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowItems.forEach { (name, sub, iconChar) ->
                                                val (avatarColor, badgeCol) = when (iconChar) {
                                                    "G" -> Pair(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF60A5FA))
                                                    "P" -> Pair(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFFA78BFA))
                                                    "Ph" -> Pair(Color(0xFF2DD4BF).copy(alpha = 0.15f), Color(0xFF2DD4BF))
                                                    else -> Pair(AmberAccent.copy(alpha = 0.15f), AmberAccent)
                                                }

                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                                    shape = RoundedCornerShape(18.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .border(1.dp, Card2Color, RoundedCornerShape(18.dp))
                                                        .clickable {
                                                            val matchedTool = rawAiTools.find { it.name.contains(name.split(" ")[0]) }
                                                            if (matchedTool != null) {
                                                                viewModel.addToRecentlyViewed(matchedTool.id)
                                                                selectedToolForDetail = matchedTool
                                                            } else {
                                                                activeBrowserUrl = when (iconChar) {
                                                                    "G" -> "https://gemini.google.com"
                                                                    "P" -> "https://perplexity.ai"
                                                                    "Ph" -> "https://phind.com"
                                                                    else -> "https://openai.com/dall-e-3"
                                                                }
                                                            }
                                                        }
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(42.dp)
                                                                .background(avatarColor, RoundedCornerShape(12.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = iconChar,
                                                                color = badgeCol,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 16.sp
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = name,
                                                            color = TextColor,
                                                            fontSize = 13.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = sub,
                                                            color = TextDimColor,
                                                            fontSize = 10.5.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.padding(top = 3.dp)
                                                        )
                                                        Text(
                                                            text = "★★★★★ " + if (iconChar == "G") "4.8" else if (iconChar == "P") "4.7" else if (iconChar == "Ph") "4.3" else "4.9",
                                                            color = AmberAccent,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(top = 8.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Curation Admin label
                            Text(
                                text = "Prince Laghari • Admin",
                                color = AmberAccent.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // FULL LIST EXPLORER WITH BACK NAVIGATION
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                                    .padding(top = 26.dp, bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Beautiful Golden Back Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CardColor)
                                        .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable {
                                            showExplorerList = false
                                            viewModel.onSearchQueryChanged("")
                                            viewModel.onCategorySelected("All")
                                            viewModel.onSortOptionSelected("Popular")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "←",
                                        color = AmberAccent,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = if (searchQuery.isNotEmpty()) "Search Matches" else explorerListHeader,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextColor,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Sort: $selectedSortOption",
                                    fontSize = 11.5.sp,
                                    color = AmberAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .border(0.5.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            val nextSort = when (selectedSortOption) {
                                                "Popular" -> "Trending"
                                                "Trending" -> "Newest"
                                                "Newest" -> "A-Z"
                                                else -> "Popular"
                                            }
                                            viewModel.onSortOptionSelected(nextSort)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 22.dp)
                            ) {
                                PulsingSearchBox(
                                    query = searchQuery,
                                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                                    onExternalSearchClicked = { showExternalSearchPanel = true }
                                )
                            }

                            // Suggestions Dropdown Overlay
                            if (suggestions.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 22.dp, vertical = 4.dp)
                                        .border(1.dp, AmberAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "SUGGESTIONS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                        suggestions.forEach { name ->
                                            Text(
                                                text = name,
                                                color = TextColor,
                                                fontSize = 13.sp,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.onSearchQueryChanged(name)
                                                        focusManager.clearFocus()
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                itemsIndexed(
                                    items = aiTools,
                                    key = { _, tool -> tool.id }
                                ) { _, tool ->
                                    AiToolCard(
                                        tool = tool,
                                        searchQuery = searchQuery,
                                        isCompactMode = isCompactMode,
                                        onCardClicked = { clicked ->
                                            viewModel.addToRecentlyViewed(clicked.id)
                                            selectedToolForDetail = clicked
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "Browser" -> {
                    // UPGRADED PREMIUM AI BROWSER VIEW MODE
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp)
                    ) {
                        // Premium Header Layer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 26.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFA78BFA), Color(0xFF3FF0FF))),
                                        RoundedCornerShape(11.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🌐", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Premium AI Browser",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextColor
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(listOf(AmberAccent, Color(0xFFFFE08A))),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    color = Color(0xFF241A03),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Search box
                        PulsingSearchBox(
                            query = browserSearchQuery,
                            onQueryChanged = { viewModel.onBrowserSearchQueryChanged(it) },
                            onExternalSearchClicked = { showExternalSearchPanel = true },
                            placeholderText = "Search AI tools, models, news..."
                        )

                        // Gold filter note under search
                        Text(
                            text = "⚡ Sirf AI-related results dikhaye jaate hain — non-AI content filter ho jata hai",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 14.dp)
                        )

                        // Scrollable browser search matches
                        if (browserResults.isEmpty()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No AI results matched your search",
                                    color = TextDimColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 12.dp)
                            ) {
                                itemsIndexed(
                                    items = browserResults,
                                    key = { _, tool -> "browser-${tool.id}" }
                                ) { _, tool ->
                                    BrowserResultCard(
                                        tool = tool,
                                        onCardClicked = { clicked ->
                                            if (clicked.toolUrl.isNotEmpty()) {
                                                activeBrowserUrl = clicked.toolUrl
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "Saved" -> {
                    val savedList = aiTools.filter { favoriteIds.contains(it.id) }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp)
                    ) {
                        Text(
                            text = "Saved Shortlist",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextColor,
                            modifier = Modifier.padding(top = 26.dp, bottom = 14.dp)
                        )

                        if (savedList.isEmpty()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Your Saved Shortlist is Empty",
                                        color = TextDimColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Tap heart on AI cards to save them here",
                                        color = TextDimColor.copy(alpha = 0.6f),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                itemsIndexed(
                                    items = savedList,
                                    key = { _, tool -> "saved-${tool.id}" }
                                ) { _, tool ->
                                    AiToolCard(
                                        tool = tool,
                                        isCompactMode = isCompactMode,
                                        onCardClicked = { clicked ->
                                            viewModel.addToRecentlyViewed(clicked.id)
                                            selectedToolForDetail = clicked
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                "Profile" -> {
                    // DEDICATED FULL VIEW PREMIUM PROFILE SCREEN
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 26.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Premium Dashboard",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextColor
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Premium3DLogo(size = 40)
                        }

                        // 120 FPS Status Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    shadowElevation = 3f
                                    clip = true
                                }
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFF3FF0FF).copy(alpha = 0.15f),
                                            Color(0xFF8B5C2F).copy(alpha = 0.05f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF3FF0FF).copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "120",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF3FF0FF)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "FPS Ultra-Smooth Mode",
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Buttery-smooth animations and UI is ACTIVE",
                                        color = TextDimColor,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buy Me a Coffee Card (Easypaisa)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1408)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = AmberAccent.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "☕", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Buy Me a Coffee",
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Agar app pasand aayi to support kar sakte hain — Easypaisa ke zariye direct payment send karein.",
                                    color = TextDimColor,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Easypaisa box
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.sweepGradient(listOf(AmberAccent.copy(alpha = 0.4f), Color.Transparent)),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "Easypaisa Number", fontSize = 10.sp, color = TextDimColor)
                                        Text(
                                            text = "0310 3138887",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextColor,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Easypaisa Number", "03103138887")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Easypaisa Number Copied! ☕", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent.copy(alpha = 0.15f)),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "Copy",
                                            color = AmberAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contact / Hire-Me Section
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Card2Color, RoundedCornerShape(16.dp))
                                .clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:laghariprince2@gmail.com")
                                            putExtra(Intent.EXTRA_SUBJECT, "App Development Inquiry - Prince Laghari")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Contact: laghariprince2@gmail.com", Toast.LENGTH_LONG).show()
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Apni app banwani hai?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Contact: ", color = TextDimColor, fontSize = 11.5.sp)
                                    Text(
                                        text = "laghariprince2@gmail.com",
                                        color = AmberAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Settings Preferences integrated
                        Text(
                            text = "APP CONFIGURATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberAccent,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle Compact Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Compact Card Mode", color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "High-density dashboard list spacing", color = TextDimColor, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isCompactMode,
                                onCheckedChange = { viewModel.toggleCompactMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CardColor,
                                    checkedTrackColor = AmberAccent,
                                    uncheckedThumbColor = TextDimColor,
                                    uncheckedTrackColor = Card2Color
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // External Search Engine selector
                        Text(text = "External Search Portal Provider", color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Google", "DuckDuckGo", "Perplexity").forEach { engine ->
                                val isSelected = engine == defaultSearchEngine
                                Box(
                                    modifier = Modifier
                                        .background(if (isSelected) AmberAccent.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(10.dp))
                                        .border(0.5.dp, if (isSelected) AmberAccent else TextDimColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.selectSearchEngine(engine) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = engine,
                                        color = if (isSelected) AmberAccent else TextDimColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Data Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.clearAllHistory()
                                    Toast.makeText(context, "History cleared successfully", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Card2Color),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Clear History", color = TextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    viewModel.clearAllFavorites()
                                    Toast.makeText(context, "Favorites cleared successfully", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Card2Color),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Clear Saved", color = TextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // About trigger button
                        Button(
                            onClick = { showAbout = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CardColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Card2Color, RoundedCornerShape(10.dp)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "About & Curation Info ✦", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }

        // Floating Bottom Navigation (Home, Browser, Saved, Profile)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFA08070C)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 18.dp)
                .fillMaxWidth()
                .height(64.dp)
                .border(1.dp, Card2Color, RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    Triple("Home", "🏠", "Home"),
                    Triple("Browser", "🌐", "Browser"),
                    Triple("Saved", "❤️", "Saved"),
                    Triple("Profile", "👤", "Profile")
                )

                navItems.forEach { (label, icon, mode) ->
                    val isSelected = activeViewMode == mode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                activeViewMode = mode
                                // Reset explorer navigation states when switching top-level destinations
                                if (mode != "Home") {
                                    showExplorerList = false
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .background(AmberAccent.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = icon, fontSize = 14.sp)
                                    Text(
                                        text = label,
                                        color = AmberAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        } else {
                            Text(text = icon, fontSize = 16.sp)
                            Text(
                                text = label,
                                color = TextDimColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Overlay dialogs
        selectedToolForDetail?.let { tool ->
            AiDetailOverlay(
                tool = tool,
                isFavorite = favoriteIds.contains(tool.id),
                onToggleFavorite = { viewModel.toggleFavorite(tool.id) },
                onOpenUrl = { url ->
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        viewModel.showInterstitial(activity) {
                            activeBrowserUrl = url
                        }
                    } else {
                        activeBrowserUrl = url
                    }
                },
                onDismiss = { selectedToolForDetail = null }
            )
        }

        activeBrowserUrl?.let { url ->
            LiteBrowserDialog(
                initialUrl = url,
                onDismiss = { activeBrowserUrl = null }
            )
        }

        if (showExternalSearchPanel) {
            ExternalSearchDialog(
                query = searchQuery,
                onDismiss = { showExternalSearchPanel = false }
            )
        }

        if (showAbout) {
            AboutDialog(
                onBuyMeCoffee = {
                    activeViewMode = "Profile"
                    showAbout = false
                },
                onDismiss = { showAbout = false }
            )
        }
    }
}
