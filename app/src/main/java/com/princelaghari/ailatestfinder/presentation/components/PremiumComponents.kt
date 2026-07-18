package com.princelaghari.ailatestfinder.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.presentation.theme.MetallicGold
import com.princelaghari.ailatestfinder.presentation.theme.PaleGold

/**
 * Custom helper to open URLs cleanly in Chrome Custom Tabs, with safe external browser fallback.
 */
fun openUrlWithChromeCustomTabs(context: Context, url: String) {
    if (url.isEmpty()) return
    try {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(android.graphics.Color.parseColor("#0A0A0A"))
        builder.setShowTitle(true)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Hardware-accelerated shimmer modifier for premium loading/skeleton states.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2.5f * size.width.toFloat(),
        targetValue = 2.5f * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF0F0F0F),
                Color(0xFF262217), // Rich golden-tinted metallic shimmer highlight
                Color(0xFF0F0F0F)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

/**
 * Shimmer skeleton placeholder for loading/syncing cards.
 */
@Composable
fun AiToolCardSkeleton() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 0.5.dp,
                color = MetallicGold.copy(alpha = 0.08f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

/**
 * HighlightedText renders text and highlights query matches dynamically in Metallic Gold.
 */
@Composable
fun HighlightedText(
    text: String,
    query: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    if (query.isEmpty() || !text.lowercase().contains(query.lowercase())) {
        Text(text = text, style = style, modifier = modifier, maxLines = maxLines, overflow = overflow)
        return
    }

    val annotatedString = buildAnnotatedString {
        var start = 0
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        while (true) {
            val index = lowerText.indexOf(lowerQuery, start)
            if (index == -1) {
                append(text.substring(start))
                break
            }
            append(text.substring(start, index))
            pushStyle(SpanStyle(color = MetallicGold, fontWeight = FontWeight.Bold))
            append(text.substring(index, index + query.length))
            pop()
            start = index + query.length
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}

/**
 * ShimmerBrandingText displays "Created by Prince Laghari" right underneath the search or greeting panel,
 * featuring an elegant, soft metallic gold gradient shimmer that sweeps gently across the text.
 */
@Composable
fun ShimmerBrandingText(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8A7322), // Deep gold
            MetallicGold,      // Radiant Gold
            Color(0xFFFFF6D1), // Soft white-gold
            MetallicGold,
            Color(0xFF8A7322)
        ),
        start = androidx.compose.ui.geometry.Offset(xOffset, 0f),
        end = androidx.compose.ui.geometry.Offset(xOffset + 150f, 150f)
    )

    Text(
        text = "Created by Prince Laghari",
        style = TextStyle(
            brush = shimmerBrush,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp,
            fontFamily = FontFamily.SansSerif
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textAlign = TextAlign.Center
    )
}

/**
 * PulsingSearchBox is a sleek dark input field featuring a pulsing neon-gold outer border
 * that expands and contracts smoothly.
 */
@Composable
fun PulsingSearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val borderWidth by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = borderWidth.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MetallicGold.copy(alpha = borderAlpha),
                        Color(0xFFFFDF00).copy(alpha = borderAlpha)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .background(Color(0xFF141414), shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MetallicGold.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search Premium AI Tools...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(MetallicGold),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Search",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChanged("") }
                )
            }
        }
    }
}

/**
 * CategoryChips renders standard and custom categories as metallic gold chips
 * that react smoothly to selection.
 */
@Composable
fun CategoryChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All", "Trending", "New", "Popular", "Text AI", "Image AI", "Video AI",
        "Audio AI", "Music AI", "Coding AI", "Agents", "Business", "Marketing",
        "Research", "Medical", "Finance", "Legal", "Education", "PDF",
        "Productivity", "Design", "3D", "Gaming", "Open Source"
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories, key = { it }) { category ->
            val isSelected = category == selectedCategory
            val backgroundBrush = if (isSelected) {
                Brush.linearGradient(listOf(MetallicGold, Color(0xFF9E7E1D)))
            } else {
                Brush.linearGradient(listOf(Color(0xFF1F1F1F), Color(0xFF141414)))
            }
            val textColor = if (isSelected) Color(0xFF0A0A0A) else PaleGold

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .clickable { onCategorySelected(category) }
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = MetallicGold.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .background(brush = backgroundBrush)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Custom card for AI tools, designed using a sleek glassmorphic container,
 * image loader with fade-in crossfade, action triggers, and subtle entry fade-in animation.
 */
@Composable
fun AiToolCard(
    tool: AiTool,
    onCardClicked: (AiTool) -> Unit,
    searchQuery: String = "",
    modifier: Modifier = Modifier
) {
    var isImageError by remember(tool.imageUrl) { mutableStateOf(false) }
    val context = LocalContext.current

    // Optimize image loading recompositions by remembering the ImageRequest instance
    val imageRequest = remember(tool.imageUrl) {
        ImageRequest.Builder(context)
            .data(tool.imageUrl)
            .crossfade(true)
            .crossfade(300)
            .build()
    }

    // Subtle premium card design
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 0.5.dp,
                color = MetallicGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onCardClicked(tool) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant circular Image Container with crossfade, fallback, and gold border
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MetallicGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!isImageError && tool.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = "${tool.name} logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onError = { isImageError = true }
                    )
                } else {
                    // Premium custom letter emblem fallback
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF222222)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tool.name.take(1).uppercase(),
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info & Direct Action
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title with Search Highlight
                    HighlightedText(
                        text = tool.name,
                        query = searchQuery,
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.2.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                    )
                    // Category Badge in Metallic Gold
                    Box(
                        modifier = Modifier
                            .background(MetallicGold.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .border(0.5.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tool.category,
                            color = MetallicGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Description with Search Highlight
                HighlightedText(
                    text = tool.description,
                    query = searchQuery,
                    style = TextStyle(
                        color = Color.LightGray.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


/**
 * AdBanner integrates Google AdMob SDK directly utilizing Ad Unit ID dynamically.
 */
@Composable
fun AdBanner(adUnitId: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

/**
 * NetworkErrorScreen renders a premium "Internet Connection Required" retry panel when offline.
 */
@Composable
fun NetworkErrorScreen(
    isRefreshing: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High premium styled offline indicator
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF1E1705), shape = RoundedCornerShape(36.dp))
                .border(1.5.dp, MetallicGold, shape = RoundedCornerShape(36.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚠️",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Internet Connection Required",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ai Latest Finder needs an active network connection to sync real-time tools with Firestore.",
            color = Color.Gray,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isRefreshing) {
            CircularProgressIndicator(
                color = MetallicGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MetallicGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "RETRY SYNC",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

/**
 * Premium detailed modal popup of selected AI tools. Shows full launch metadata, developers,
 * companies, platforms, pricing, alternatives, and actions (share, favorite, copy, Chrome custom tabs website opener).
 */
@Composable
fun AiDetailOverlay(
    tool: AiTool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isImageError by remember(tool.imageUrl) { mutableStateOf(false) }

    // Optimize image loading recompositions by remembering the ImageRequest instance
    val imageRequest = remember(tool.imageUrl) {
        ImageRequest.Builder(context)
            .data(tool.imageUrl)
            .crossfade(true)
            .build()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE050505)) // Beautiful glassmorphic dim background overlay
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .border(1.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {} // Prevent dismiss on self clicks
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI DISCOVERY DETAILED PORTAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetallicGold,
                            letterSpacing = 1.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onToggleFavorite) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite Toggle",
                                    tint = if (isFavorite) Color.Red else MetallicGold
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close overlay",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tool identity (Logo, Name, Developer, Company)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(2.dp, MetallicGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isImageError && tool.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageRequest,
                                    contentDescription = "${tool.name} logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    onError = { isImageError = true }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF222222)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tool.name.take(1).uppercase(),
                                        color = MetallicGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = tool.name,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Developer: ${tool.developer}",
                                color = Color.LightGray.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "Company: ${tool.company}",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Row showing Status, Pricing, Launch Year
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Pricing Badge
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "PRICING", color = Color.Gray, fontSize = 9.sp)
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(MetallicGold.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(0.5.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(text = tool.pricing, color = MetallicGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Launch Year
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LAUNCH YEAR", color = Color.Gray, fontSize = 9.sp)
                            Text(text = tool.launchYear, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        // Status Badge
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "STATUS", color = Color.Gray, fontSize = 9.sp)
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(Color(0xFF0F2C10), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(text = tool.status, color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Platforms
                    Text(text = "COMPATIBLE PLATFORMS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tool.platforms.forEach { platform ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF222222), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = platform, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Description
                    Text(text = "DESCRIPTION", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = tool.description,
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Official Website custom tab button
                    Button(
                        onClick = { openUrlWithChromeCustomTabs(context, tool.toolUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "OPEN OFFICIAL WEBSITE",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Alternatives / Related AI
                    if (tool.alternatives.isNotEmpty()) {
                        Text(text = "SIMILAR PLATFORMS / ALTERNATIVES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tool.alternatives.forEach { altName ->
                                Box(
                                    modifier = Modifier
                                        .background(MetallicGold.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, MetallicGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = altName, color = PaleGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // Similar AI / Tags Section
                    if (tool.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "SIMILAR AI / TAGS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tool.tags, key = { it }) { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF222222), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, MetallicGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = tag, color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
