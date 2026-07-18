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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.presentation.components.*
import com.princelaghari.ailatestfinder.presentation.theme.AiLatestFinderTheme
import com.princelaghari.ailatestfinder.presentation.theme.MetallicGold
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
                    color = Color(0xFF0A0A0A)
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val aiTools by viewModel.filteredList.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()

    // Left Navigation Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Overlay active selection state
    var selectedToolForDetail by remember { mutableStateOf<AiTool?>(null) }

    // Lazy list pagination trigger
    val listState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 4
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMoreItems()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF141414),
                drawerContentColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Brand Header in Drawer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Ai Latest Finder",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MetallicGold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Premium Portal v1.0",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                HorizontalDivider(color = MetallicGold.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))

                // Drawer Navigation Items
                val menuItems = listOf(
                    "Home" to "Explore latest AI tools",
                    "AI History" to "Your recently viewed models",
                    "Favorites" to "Your curated AI shortlist",
                    "Categories" to "Browse by domains",
                    "Settings" to "Configure interface preferences",
                    "About" to "Platform details & info",
                    "Privacy Policy" to "Your data protection rights"
                )

                menuItems.forEach { (title, subtitle) ->
                    NavigationDrawerItem(
                        label = {
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (title == "Home") MetallicGold else Color.White
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Footer branding in drawer
                Text(
                    text = "Created by Prince Laghari",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MetallicGold.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0A0A))
            ) {
                // Top Header with (☰) hamburger button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 4.dp, start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Drawer",
                            tint = MetallicGold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ai Latest Finder",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MetallicGold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Discover the World's Best AI Technologies",
                            fontSize = 11.sp,
                            color = Color.LightGray.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

            // Search Bar with neon pulse border
            Box(modifier = Modifier.fillMaxWidth()) {
                PulsingSearchBox(
                    query = searchQuery,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) }
                )
            }

            // Real-time typed suggestions Dropdown UI overlay
            if (suggestions.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, MetallicGold.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "INSTANT SEARCH SUGGESTIONS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetallicGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        suggestions.forEach { name ->
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onSearchQueryChanged(name) }
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Shimmering Branding Line directly below Search View
            ShimmerBrandingText()

            // Small premium administrative text right above Category Chips
            Text(
                text = "Prince Laghari • Admin",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MetallicGold.copy(alpha = 0.7f),
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Horizontal Category Chips (Hide if searching to display ONLY search results)
            if (searchQuery.isEmpty()) {
                CategoryChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelected(it) }
                )

                // Dynamic Sorting Selection Chips (Popular, Trending, Newest, A-Z) (Hide if searching)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortOptions = listOf("Popular", "Trending", "Newest", "A-Z")
                    items(sortOptions, key = { it }) { option ->
                        val isSelected = option == selectedSortOption
                        val background = if (isSelected) MetallicGold.copy(alpha = 0.15f) else Color.Transparent
                        val border = if (isSelected) MetallicGold else Color.Gray.copy(alpha = 0.3f)
                        val textCol = if (isSelected) MetallicGold else Color.Gray

                        Box(
                            modifier = Modifier
                                .background(background, RoundedCornerShape(8.dp))
                                .border(0.5.dp, border, RoundedCornerShape(8.dp))
                                .clickable { viewModel.onSortOptionSelected(option) }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Sort: $option",
                                color = textCol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Main Tools List or Empty State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (aiTools.isEmpty() && searchQuery.isEmpty()) {
                    // Show flagship-grade loading experience with progress indicator & shimmer list
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MetallicGold,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Loading AI Directory...",
                                color = MetallicGold.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Shimmering skeleton cards to represent loading placeholders
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(5) {
                                AiToolCardSkeleton()
                            }
                        }
                    }
                } else if (aiTools.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No tools matched your search",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Try clear search filters or explore categories",
                                color = Color.DarkGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        itemsIndexed(
                            items = aiTools,
                            key = { _, tool -> tool.id }
                        ) { index, tool ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Smooth entry animations for AI cards
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInVertically(
                                        initialOffsetY = { 50 * (index + 1) },
                                        animationSpec = tween(
                                            durationMillis = 400 + (index * 50).coerceAtMost(300),
                                            easing = EaseOutQuad
                                        )
                                    ) + fadeIn(animationSpec = tween(300)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AiToolCard(
                                        tool = tool,
                                        searchQuery = searchQuery, // Pass searchQuery to highlight matches
                                        onCardClicked = { selectedTool ->
                                            viewModel.addToRecentlyViewed(selectedTool.id)
                                            selectedToolForDetail = selectedTool
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // AdMob Banner integration at bottom of Screen
            AdBanner(adUnitId = viewModel.getBannerAdUnitId())
        }

        // Beautiful Detail Overlay Dialog portal overlay
        selectedToolForDetail?.let { tool ->
            AiDetailOverlay(
                tool = tool,
                isFavorite = favoriteIds.contains(tool.id),
                onToggleFavorite = { viewModel.toggleFavorite(tool.id) },
                onDismiss = { selectedToolForDetail = null }
            )
        }
    }
}
}
