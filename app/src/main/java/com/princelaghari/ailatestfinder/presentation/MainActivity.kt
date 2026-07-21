package com.princelaghari.ailatestfinder.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
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
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.presentation.components.*
import com.princelaghari.ailatestfinder.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    // Bottom Navigation View Mode (Home, Categories, Saved, Profile)
    var activeViewMode by remember { mutableStateOf("Home") }

    // Dialog state controllers
    var selectedToolForDetail by remember { mutableStateOf<AiTool?>(null) }
    var activeBrowserUrl by remember { mutableStateOf<String?>(null) }
    var showExternalSearchPanel by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

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
        // Main Scrollable Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Leave exact space for floating navigation bar
        ) {
            // Dashboard Layout
            if (activeViewMode == "Home" && searchQuery.isEmpty()) {
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
                                    text = "1020",
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
                                    text = "24",
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
                                    text = "12",
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
                            onCategorySelected = { viewModel.onCategorySelected(it) }
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
                                modifier = Modifier.clickable { activeViewMode = "Categories" }
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
                                modifier = Modifier.clickable { activeViewMode = "Categories" }
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

                    // Buy Me Coffee Integration inside Home Feed
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Card2Color),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 10.dp)
                            .border(1.dp, AmberAccent.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Support Prince Laghari ☕",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColor
                                )
                                Text(
                                    text = "Keep the premium AI directory active & updated",
                                    fontSize = 10.5.sp,
                                    color = TextDimColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    openUrlWithChromeCustomTabs(context, "https://buymeacoffee.com/princelaghari")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Support",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                // List View (triggers when Category selected, Search query active, Saved tab, or History tab)
                val activeList = remember(aiTools, activeViewMode, favoriteIds, recentlyViewedIds) {
                    when (activeViewMode) {
                        "Saved" -> aiTools.filter { favoriteIds.contains(it.id) }
                        "History" -> {
                            val historyList = mutableListOf<AiTool>()
                            recentlyViewedIds.forEach { id ->
                                aiTools.find { it.id == id }?.let { historyList.add(it) }
                            }
                            historyList
                        }
                        else -> aiTools
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                            .padding(top = 26.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (activeViewMode) {
                                "Saved" -> "Saved Shortlist"
                                "History" -> "Recently Viewed"
                                else -> "AI Tools Explorer"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextColor
                        )
                        if (activeViewMode == "Categories") {
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

                    // Suggestions Overlay Dropdown
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

                    if (activeViewMode == "Categories") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp)
                        ) {
                            CategoryChips(
                                selectedCategory = selectedCategory,
                                onCategorySelected = { viewModel.onCategorySelected(it) }
                            )
                        }
                    }
                }

                // Listing Scroll panel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                ) {
                    if (activeList.isEmpty() && searchQuery.isEmpty()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = AmberAccent,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Connecting to Firestore...",
                                    color = AmberAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(5) {
                                    AiToolCardSkeleton()
                                }
                            }
                        }
                    } else if (activeList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No tools matched your filters",
                                    color = TextDimColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Try clearing search words or adding items",
                                    color = TextDimColor.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = activeList,
                                key = { _, tool -> tool.id }
                            ) { index, tool ->
                                Box(modifier = Modifier.fillMaxWidth()) {
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
            }
        }

        // Floating Bottom Navigation (Home, Categories, Saved, Profile)
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
                    Triple("Categories", "▦", "Categories"),
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
                                if (mode == "Profile") {
                                    showSettings = true
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

        // Sticky AdBanner integration directly layered
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AdBanner(adUnitId = viewModel.getBannerAdUnitId())
        }

        // Overlay dialogs
        selectedToolForDetail?.let { tool ->
            AiDetailOverlay(
                tool = tool,
                isFavorite = favoriteIds.contains(tool.id),
                onToggleFavorite = { viewModel.toggleFavorite(tool.id) },
                onOpenUrl = { url -> activeBrowserUrl = url },
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

        if (showSettings) {
            SettingsDialog(
                isCompactMode = isCompactMode,
                onToggleCompactMode = { viewModel.toggleCompactMode(it) },
                selectedEngine = defaultSearchEngine,
                onSelectEngine = { viewModel.selectSearchEngine(it) },
                onClearHistory = {
                    viewModel.clearAllHistory()
                    Toast.makeText(context, "History cleared successfully", Toast.LENGTH_SHORT).show()
                },
                onClearFavorites = {
                    viewModel.clearAllFavorites()
                    Toast.makeText(context, "Favorites cleared successfully", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showSettings = false }
            )
        }

        if (showAbout) {
            AboutDialog(
                onBuyMeCoffee = {
                    openUrlWithChromeCustomTabs(context, "https://buymeacoffee.com/princelaghari")
                },
                onDismiss = { showAbout = false }
            )
        }

        if (showPrivacyPolicy) {
            PrivacyPolicyDialog(
                onDismiss = { showPrivacyPolicy = false }
            )
        }
    }
}
