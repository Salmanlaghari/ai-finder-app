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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.princelaghari.ailatestfinder.presentation.components.*
import com.princelaghari.ailatestfinder.presentation.theme.AiLatestFinderTheme
import com.princelaghari.ailatestfinder.presentation.theme.MetallicGold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContent {
            AiLatestFinderTheme {
                val isOnline by viewModel.isNetworkAvailable.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A0A)
                ) {
                    if (isOnline) {
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
    val aiTools by viewModel.aiTools.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

        // Search Bar with neon pulse border
        PulsingSearchBox(
            query = searchQuery,
            onQueryChanged = { viewModel.onSearchQueryChanged(it) }
        )

        // Shimmering Branding Line directly below Search View
        ShimmerBrandingText()

        // Horizontal Category Chips
        CategoryChips(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.onCategorySelected(it) }
        )

        // Main Tools List or Empty State
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (aiTools.isEmpty()) {
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(aiTools) { index, tool ->
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
                                AiToolCard(tool = tool)
                            }
                        }
                    }

                    // Add premium support widget inside bottom list area
                    item {
                        BuyMeACoffeeWidget()
                    }
                }
            }
        }

        // AdMob Banner integration at bottom of Screen
        AdBanner()
    }
}
