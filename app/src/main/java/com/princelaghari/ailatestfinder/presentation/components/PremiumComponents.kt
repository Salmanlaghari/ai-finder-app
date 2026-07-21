package com.princelaghari.ailatestfinder.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
import com.princelaghari.ailatestfinder.presentation.theme.*

/**
 * Custom helper to open URLs cleanly in Chrome Custom Tabs, with safe external browser fallback.
 */
fun openUrlWithChromeCustomTabs(context: Context, url: String) {
    if (url.isEmpty()) return
    try {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(android.graphics.Color.parseColor("#08070C"))
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
 * Fully functional Premium Settings Dialog to customize user preferences.
 * Toggles compact lists, resets local databases, and selects default external search engines.
 */
@Composable
fun SettingsDialog(
    isCompactMode: Boolean,
    onToggleCompactMode: (Boolean) -> Unit,
    selectedEngine: String,
    onSelectEngine: (String) -> Unit,
    onClearHistory: () -> Unit,
    onClearFavorites: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(AmberAccent, VioletAccent)),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "DASHBOARD PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberAccent,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Compact Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Compact Card Mode", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "High-density dashboard-grid formatting", color = TextDimColor, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isCompactMode,
                        onCheckedChange = onToggleCompactMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CardColor,
                            checkedTrackColor = AmberAccent,
                            uncheckedThumbColor = TextDimColor,
                            uncheckedTrackColor = Card2Color
                        )
                    )
                }

                HorizontalDivider(color = Card2Color, modifier = Modifier.padding(vertical = 14.dp))

                // External Search Engine selector
                Text(text = "External Search Portal Provider", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Google", "DuckDuckGo", "Perplexity").forEach { engine ->
                        val isSelected = engine == selectedEngine
                        Box(
                            modifier = Modifier
                                .background(if (isSelected) AmberAccent.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(10.dp))
                                .border(0.5.dp, if (isSelected) AmberAccent else TextDimColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable { onSelectEngine(engine) }
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

                HorizontalDivider(color = Card2Color, modifier = Modifier.padding(vertical = 14.dp))

                // Data actions
                Text(text = "Database & Cache Management", color = TextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.buttonColors(containerColor = Card2Color),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Clear History", color = TextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onClearFavorites,
                        colors = ButtonDefaults.buttonColors(containerColor = Card2Color),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Clear Saved", color = TextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "CLOSE PANEL", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

/**
 * Fully functional Premium About Dialog.
 * Showcases application identity, curation stack, and dynamic support widget.
 */
@Composable
fun AboutDialog(
    onBuyMeCoffee: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(AmberAccent, VioletAccent)),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Premium3DLogo(size = 68)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ai Latest Finder",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberAccent,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Dashboard Edition v1.5.0",
                    fontSize = 12.sp,
                    color = TextDimColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A flagship cyber-grade dashboard application curated by Prince Laghari, compiling over 1000 premium artificial intelligence platforms under a secure, offline-first Clean Architecture sync system.",
                    fontSize = 13.sp,
                    color = TextColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Admin & Owner: Prince Laghari\nTech: Jetpack Compose, MVVM, Room, Firestore, AdMob",
                    fontSize = 11.sp,
                    color = VioletAccent,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buy Me Coffee widget
                Button(
                    onClick = onBuyMeCoffee,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDD00)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(text = "☕ Buy Me a Coffee", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                TextButton(onClick = onDismiss) {
                    Text(text = "DISMISS", color = TextDimColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Self-contained local scrollable Privacy Policy dialog to satisfy App Store compliance.
 */
@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(AmberAccent, VioletAccent)),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "PRIVACY POLICY & DATA RIGHTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberAccent,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .height(220.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Your privacy is paramount to us at Ai Latest Finder.\n\n" +
                                "1. DATA COLLECTION\n" +
                                "This application is built with privacy-by-design. We do not host user registration accounts, nor do we track your geographic coordinates or identity. All data, including favorited tools and recently viewed history lists, is strictly cached locally on your device's sandbox partition utilizing Room database structures.\n\n" +
                                "2. NETWORK COMMUNICATIONS\n" +
                                "The app initiates direct connections with Firebase Firestore to retrieve real-time catalog revisions. This data synchronization is 100% secure, read-only, and anonymous.\n\n" +
                                "3. ADVERTISING AND TRACKING\n" +
                                "We utilize Google Mobile Ads (AdMob) SDK to display policy-compliant banners. AdMob may utilize anonymous advertising identifiers to serve tailored materials. You may request to disable advertising tracking directly inside your primary Android device settings.\n\n" +
                                "4. SECURITY\n" +
                                "All external web routing performed inside our custom Lite Browser uses isolated contexts to safeguard credentials and prevent local script injections.\n\n" +
                                "By utilizing this app, you fully consent to these terms. For any concerns, contact our Administrator Prince Laghari.",
                        fontSize = 12.sp,
                        color = TextColor.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "I AGREE", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Elegant modal dialog allowing users to perform an optional external query search
 * across major international search providers inside Chrome Custom Tabs.
 */
@Composable
fun ExternalSearchDialog(
    query: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cleanQuery = query.trim()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(AmberAccent, VioletAccent)),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EXTERNAL SEARCH ENGINES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmberAccent,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (cleanQuery.isEmpty()) "Search External Web Providers" else "Search Web for \"$cleanQuery\"",
                    fontSize = 15.sp,
                    color = TextColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                val providers = listOf(
                    Triple("Google", "https://www.google.com/search?q=", Color(0xFF4285F4)),
                    Triple("Bing", "https://www.bing.com/search?q=", Color(0xFF00A4EF)),
                    Triple("DuckDuckGo", "https://duckduckgo.com/?q=", Color(0xFFDE5833)),
                    Triple("Brave", "https://search.brave.com/search?q=", Color(0xFFFB542B)),
                    Triple("Perplexity", "https://www.perplexity.ai/search?q=", AmberAccent),
                    Triple("Kagi", "https://kagi.com/search?q=", Color(0xFFFF6600)),
                    Triple("You.com", "https://you.com/search?q=", Color(0xFF00D1FF)),
                    Triple("Yahoo", "https://search.yahoo.com/search?p=", Color(0xFF6001D2)),
                    Triple("Yandex", "https://yandex.com/search/?text=", Color(0xFFFFCC00)),
                    Triple("Ecosia", "https://www.ecosia.org/search?q=", Color(0xFF00B050))
                )

                // Layout search buttons in elegant 2-column grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = providers.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { (name, baseUrl, brandColor) ->
                                Button(
                                    onClick = {
                                        val encodedQuery = Uri.encode(cleanQuery)
                                        openUrlWithChromeCustomTabs(context, "$baseUrl$encodedQuery")
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Card2Color),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(0.5.dp, brandColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                ) {
                                    Text(
                                        text = name,
                                        color = TextColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onDismiss) {
                    Text(text = "CANCEL", color = TextDimColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * A highly polished, 3D-styled cyber-themed AI Emblem.
 * Designed with layered neon-cyan and glowing violet gradients to render an elite premium finish.
 */
@Composable
fun Premium3DLogo(
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(size.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Shadow/Glow layer 1 (Outer Deep Cyber Glow)
        Box(
            modifier = Modifier
                .size((size * 0.95).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3FF0FF).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Beveled Outer Cyan/Purple Ring (Layer 2) - Gives thickness and 3D depth
        Box(
            modifier = Modifier
                .size((size * 0.9).dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E1035), // Deep purple
                            Color(0xFFE9D5FF), // Bright Highlight
                            Color(0xFF3FF0FF), // Cyber Cyan
                            Color(0xFF0F3238)  // Deep shadow cyan
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFE9D5FF).copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )

        // Inner Matte Dark Core (Layer 3) - Sinks into the ring
        Box(
            modifier = Modifier
                .size((size * 0.76).dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BgColor,
                            CardColor
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF3FF0FF).copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )

        // Concentric Inner Tech Bevel (Layer 4)
        Box(
            modifier = Modifier
                .size((size * 0.64).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE9D5FF).copy(alpha = 0.1f),
                            Color(0xFF0F3238).copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3FF0FF),
                            Color(0xFFE9D5FF),
                            Color(0xFF1E1035)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Central 3D AI Text or Golden Star Symbol (Layer 5)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AI",
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE9D5FF), // Bright highlight at top
                            Color(0xFF3FF0FF), // Solid Cyber Cyan
                            Color(0xFF5B21B6)  // Deep shadow purple
                        )
                    ),
                    fontSize = (size * 0.26).sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
            )
            Text(
                text = "FINDER",
                style = TextStyle(
                    color = Color(0xFF3FF0FF).copy(alpha = 0.7f),
                    fontSize = (size * 0.08).sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

/**
 * A breathtaking premium dark-and-gold shimmering splash screen.
 * Displays during instant startup preloading to completely eliminate any blank/black screens.
 */
@Composable
fun PremiumSplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing 3D gold-metallic shape emblem
        Premium3DLogo(size = 110)

        Spacer(modifier = Modifier.height(32.dp))

        // Large title
        Text(
            text = "Ai Latest Finder",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AmberAccent,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Subtitle Shimmer
        ShimmerBrandingText()

        Spacer(modifier = Modifier.height(48.dp))

        // Circular progress loader with smooth metallic gold styling
        CircularProgressIndicator(
            color = AmberAccent,
            strokeWidth = 3.dp,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Initializing Dashboard...",
            color = TextDimColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
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
                CardColor,
                Card2Color,
                CardColor
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
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 0.5.dp,
                color = AmberAccent.copy(alpha = 0.1f),
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
 * HighlightedText renders text and highlights query matches dynamically in Amber Gold.
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
            pushStyle(SpanStyle(color = AmberAccent, fontWeight = FontWeight.Bold))
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
            AmberAccent,       // Radiant Amber Gold
            Color(0xFFFFF6D1), // Soft white-gold
            AmberAccent,
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
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
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
    onExternalSearchClicked: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search 1000+ AI tools..."
) {
    val focusManager = LocalFocusManager.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val borderWidth by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = borderWidth.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AmberAccent.copy(alpha = borderAlpha),
                        VioletAccent.copy(alpha = borderAlpha)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(CardColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = AmberAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholderText,
                        color = TextDimColor,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    textStyle = TextStyle(
                        color = TextColor,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(AmberAccent),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            IconButton(
                onClick = onExternalSearchClicked,
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Card2Color, CircleShape)
                        .border(0.5.dp, AmberAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = AmberAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            }
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Search",
                    tint = TextDimColor,
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
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories, key = { it }) { category ->
            val isSelected = category == selectedCategory
            val backgroundBrush = if (isSelected) {
                Brush.linearGradient(listOf(AmberAccent, MetallicGold))
            } else {
                Brush.linearGradient(listOf(CardColor, CardColor))
            }
            val textColor = if (isSelected) Color(0xFF1A1400) else TextDimColor

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onCategorySelected(category) }
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else Card2Color,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .background(brush = backgroundBrush)
                    .padding(horizontal = 16.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Text(
                            text = "✦ ",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = category,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Custom card for AI tools, designed using a sleek glassmorphic container,
 * image loader with fade-in crossfade, action triggers, and subtle entry fade-in animation.
 */
@Composable
fun TextChatAnimation() {
    val transition = rememberInfiniteTransition(label = "textChat")

    @Composable
    fun AnimatingLine(delayMillis: Int) {
        val widthFraction by transition.animateFloat(
            initialValue = 0f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2400
                    0f at delayMillis with EaseInOutSine
                    0.6f at delayMillis + 1080 with EaseInOutSine
                    0.6f at delayMillis + 1680 with EaseInOutSine
                    0f at delayMillis + 2400 with EaseInOutSine
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "widthFraction"
        )
        val opacity by transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2400
                    0.3f at delayMillis with EaseInOutSine
                    1f at delayMillis + 1080 with EaseInOutSine
                    1f at delayMillis + 1680 with EaseInOutSine
                    0.3f at delayMillis + 2400 with EaseInOutSine
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "opacity"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction.coerceAtLeast(0.01f))
                .height(5.dp)
                .alpha(opacity)
                .background(Color.White, RoundedCornerShape(3.dp))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF3A2C66), Color(0xFF1A1440)))),
        verticalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatingLine(delayMillis = 0)
        AnimatingLine(delayMillis = 250)
        AnimatingLine(delayMillis = 500)
    }
}

@Composable
fun ReasoningAnimation() {
    val transition = rememberInfiniteTransition(label = "reasoning")

    @Composable
    fun AnimatingRing(delayMillis: Int) {
        val ringSize by transition.animateFloat(
            initialValue = 14f,
            targetValue = 60f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2200
                    14f at delayMillis with EaseOutQuad
                    60f at delayMillis + 1500 with EaseOutQuad
                    60f at 2200
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "ringSize"
        )
        val opacity by transition.animateFloat(
            initialValue = 0.9f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2200
                    0.9f at delayMillis with EaseOutQuad
                    0f at delayMillis + 1500 with EaseOutQuad
                    0f at 2200
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "opacity"
        )

        Box(
            modifier = Modifier
                .size(ringSize.dp)
                .alpha(opacity)
                .border(2.dp, Color.White, CircleShape)
        )
    }

    val coreScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF8A5A1F), Color(0xFF3D2408)))),
        contentAlignment = Alignment.Center
    ) {
        AnimatingRing(delayMillis = 0)
        AnimatingRing(delayMillis = 700)
        AnimatingRing(delayMillis = 1400)

        // Core Dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .scale(coreScale)
                .background(Color.White, CircleShape)
                .border(0.5.dp, Color.White, CircleShape)
        )
    }
}

@Composable
fun VideoAnimation() {
    val transition = rememberInfiniteTransition(label = "video")

    val scanOffset by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanOffset"
    )

    val scanOpacity by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                0f at 0
                0f at 240
                1f at 240 with EaseInOutQuad
                1f at 1200 with EaseInOutQuad
                0f at 1440
                0f at 2400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scanOpacity"
    )

    val playScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playScale"
    )

    val playOpacity by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playOpacity"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF1F6A5A), Color(0xFF0A2E26)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "▶",
            color = Color(0xFFFFFFFF),
            fontSize = 18.sp,
            modifier = Modifier
                .scale(playScale)
                .alpha(playOpacity)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(scanOffset)
                .wrapContentHeight(Alignment.Bottom)
                .height(3.dp)
                .alpha(scanOpacity)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun CodingAnimation() {
    val transition = rememberInfiniteTransition(label = "coding")

    val bracketY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bracketY"
    )

    val bracketYDelay by transition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 0 with EaseInOutSine
                -3f at 1000 with EaseInOutSine
                0f at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "bracketYDelay"
    )

    val cursorOpacity by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0
                1f at 499
                0f at 500
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorOpacity"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF1F5A52), Color(0xFF0A2622)))),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "<",
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            modifier = Modifier.offset(y = bracketY.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(18.dp)
                .alpha(cursorOpacity)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "/>",
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            modifier = Modifier.offset(y = bracketYDelay.dp)
        )
    }
}

@Composable
fun ImageGenAnimation() {
    val transition = rememberInfiniteTransition(label = "imageGen")

    @Composable
    fun AnimatingSparkle(delayFraction: Float, startXPercent: Float, bottomPercent: Float) {
        val animProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3000
                    0f at 0 with EaseInOutSine
                    1f at 3000
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "animProgress"
        )

        val actualProgress = remember(animProgress) {
            (animProgress + delayFraction) % 1.0f
        }

        val floatY = actualProgress * -55f
        val scale = 1f - (actualProgress * 0.6f)
        val opacity = if (actualProgress < 0.2f) {
            actualProgress / 0.2f
        } else {
            1f - ((actualProgress - 0.2f) / 0.8f)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.BottomStart)
                .offset(
                    x = (96 * startXPercent).dp,
                    y = (-110 * bottomPercent + floatY).dp
                )
                .size(4.dp)
                .scale(scale)
                .alpha(opacity)
                .background(Color.White, CircleShape)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF7A3FB0), Color(0xFF331966)))),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatingSparkle(delayFraction = 0f, startXPercent = 0.30f, bottomPercent = 0.10f)
        AnimatingSparkle(delayFraction = 0.26f, startXPercent = 0.55f, bottomPercent = 0.20f)
        AnimatingSparkle(delayFraction = 0.53f, startXPercent = 0.70f, bottomPercent = 0.05f)
        AnimatingSparkle(delayFraction = 0.73f, startXPercent = 0.45f, bottomPercent = 0.35f)
    }
}

@Composable
fun GenericSparkleAnimation() {
    ImageGenAnimation()
}

@Composable
fun CategoryAnimationPanel(category: String, tags: List<String> = emptyList()) {
    val normCategory = category.lowercase().trim()
    val normTags = tags.map { it.lowercase().trim() }

    when {
        normCategory.contains("text") || normCategory.contains("research") || normCategory.contains("chat") ||
        normTags.any { it.contains("chat") || it.contains("text") || it.contains("research") || it.contains("writing") } -> {
            TextChatAnimation()
        }
        normCategory.contains("reasoning") || normCategory.contains("intellect") ||
        normTags.any { it.contains("reasoning") || it.contains("thinking") } -> {
            ReasoningAnimation()
        }
        normCategory.contains("video") || normCategory.contains("motion") ||
        normTags.any { it.contains("video") || it.contains("motion") || it.contains("cinema") } -> {
            VideoAnimation()
        }
        normCategory.contains("code") || normCategory.contains("coding") || normCategory.contains("developer") ||
        normTags.any { it.contains("code") || it.contains("coding") || it.contains("programming") || it.contains("developer") } -> {
            CodingAnimation()
        }
        normCategory.contains("image") || normCategory.contains("design") || normCategory.contains("art") ||
        normTags.any { it.contains("image") || it.contains("design") || it.contains("art") || it.contains("illustration") || it.contains("creative") } -> {
            ImageGenAnimation()
        }
        else -> {
            GenericSparkleAnimation()
        }
    }
}

/**
 * Clean, Hardware-Accelerated Browser Result Card matching the exact styling requested.
 */
@Composable
fun BrowserResultCard(
    tool: AiTool,
    onCardClicked: (AiTool) -> Unit,
    modifier: Modifier = Modifier
) {
    val char = tool.name.firstOrNull()?.toString() ?: "AI"
    val avatarBrush = remember(tool.id) {
        when {
            tool.name.contains("Google") || tool.name.contains("Gemini") -> {
                Brush.linearGradient(listOf(Color(0xFF4285F4), Color(0xFF34A853), Color(0xFFFBBC05), Color(0xFFEA4335)))
            }
            tool.name.contains("Claude") || tool.name.contains("Anthropic") -> {
                Brush.linearGradient(listOf(Color(0xFFD97757), Color(0xFF8B4A2F)))
            }
            tool.name.contains("Midjourney") -> {
                Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF5B21B6)))
            }
            tool.name.contains("DeepSeek") -> {
                Brush.linearGradient(listOf(Color(0xFF3FF0FF), Color(0xFF8B5CF6)))
            }
            else -> {
                Brush.linearGradient(listOf(AmberAccent, VioletAccent))
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .graphicsLayer {
                shadowElevation = 4f
                clip = true
            }
            .border(1.dp, Card2Color, RoundedCornerShape(16.dp))
            .clickable { onCardClicked(tool) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Avatar Box with beautiful gradient
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(avatarBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tool.name,
                        color = TextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Tiny PRO tag
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(listOf(AmberAccent, Color(0xFFFFE08A))),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 1.5f.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = Color(0xFF241A03),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Text(
                    text = tool.description,
                    color = TextDimColor,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right-side Cyan AI badge
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFF3FF0FF), Color(0xFF7EF9FF))),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AI",
                    color = Color(0xFF04262A),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun AiToolCard(
    tool: AiTool,
    onCardClicked: (AiTool) -> Unit,
    searchQuery: String = "",
    isCompactMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cardPaddingVertical = if (isCompactMode) 4.dp else 8.dp
    val innerPadding = if (isCompactMode) 12.dp else 16.dp
    val logoSize = if (isCompactMode) 30 else 32
    val descMaxLines = if (isCompactMode) 1 else 2

    Card(
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = cardPaddingVertical)
            .graphicsLayer {
                shadowElevation = 2f
                clip = true
            }
            .border(
                width = 1.dp,
                color = Card2Color,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onCardClicked(tool) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Content Panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(innerPadding)
            ) {
                // Top Row: Icon, Title, and Category Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(logoSize.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        AmberAccent,
                                        VioletAccent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tool.name.take(1),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    HighlightedText(
                        text = tool.name,
                        query = searchQuery,
                        style = TextStyle(
                            color = TextColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .background(AmberAccent.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tool.category,
                            color = AmberAccent,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Golden Stars Rating Row
                val ratingScore = remember(tool.id) {
                    val code = tool.id.hashCode() % 10
                    val base = 4.0 + (code / 10.0)
                    String.format("%.1f", base)
                }
                Text(
                    text = "★★★★★ $ratingScore",
                    color = AmberAccent,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tool Description
                HighlightedText(
                    text = tool.description,
                    query = searchQuery,
                    style = TextStyle(
                        color = TextDimColor,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    maxLines = descMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right side 96px animated photo panel
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 20.dp, bottomEnd = 20.dp))
            ) {
                CategoryAnimationPanel(category = tool.category, tags = tool.tags)
            }
        }
    }
}


/**
 * AdBanner integrates Google AdMob SDK directly utilizing Ad Unit ID dynamically.
 * Features an absolute, robust crash-safety wrapper to prevent any app crashes in environments
 * lacking Google Play Services or AdMob dependencies.
 */
@Composable
fun AdBanner(adUnitId: String, modifier: Modifier = Modifier) {
    var hasError by remember { mutableStateOf(false) }

    // Dynamically trust the exact Ad Unit ID programmatically loaded from strings.xml
    val finalAdUnitId = remember(adUnitId) {
        adUnitId.ifEmpty { "ca-app-pub-3940256099942544/6300978111" }
    }

    if (hasError) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(CardColor)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Premium AI Experience Portal",
                color = AmberAccent.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(BgColor)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    try {
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            this.adUnitId = finalAdUnitId
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AdBanner", "Failed to construct AdView: ${e.localizedMessage}", e)
                        hasError = true
                        android.view.View(ctx)
                    }
                },
                update = { _ -> }
            )
        }
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
            .background(BgColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(AmberAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(36.dp))
                .border(1.5.dp, AmberAccent, shape = RoundedCornerShape(36.dp)),
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
            color = TextColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ai Latest Finder needs an active network connection to sync real-time tools with Firestore.",
            color = TextDimColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isRefreshing) {
            CircularProgressIndicator(
                color = AmberAccent,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
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
    onOpenUrl: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isImageError by remember(tool.imageUrl) { mutableStateOf(false) }

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
                .background(Color(0xEE050505))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
                    .border(1.dp, Card2Color, RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI DISCOVERY DETAILED PORTAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AmberAccent,
                            letterSpacing = 1.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onToggleFavorite) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite Toggle",
                                    tint = if (isFavorite) Color.Red else AmberAccent
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close overlay",
                                    tint = TextDimColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.5.dp, AmberAccent, RoundedCornerShape(16.dp)),
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
                                Premium3DLogo(size = 76)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = tool.name,
                                color = TextColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Developer: ${tool.developer}",
                                color = TextDimColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "Company: ${tool.company}",
                                color = TextDimColor.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "PRICING", color = TextDimColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(AmberAccent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(0.5.dp, AmberAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(text = tool.pricing, color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "LAUNCH YEAR", color = TextDimColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = tool.launchYear, color = TextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "STATUS", color = TextDimColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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

                    Text(text = "COMPATIBLE PLATFORMS", color = TextDimColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tool.platforms.forEach { platform ->
                            Box(
                                modifier = Modifier
                                    .background(Card2Color, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = platform, color = TextColor, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(text = "DESCRIPTION", color = TextDimColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = tool.description,
                        color = TextColor.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onOpenUrl(tool.toolUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
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

                    if (tool.alternatives.isNotEmpty()) {
                        Text(text = "SIMILAR PLATFORMS / ALTERNATIVES", color = TextDimColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tool.alternatives.forEach { altName ->
                                Box(
                                    modifier = Modifier
                                        .background(AmberAccent.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .border(0.5.dp, AmberAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = altName, color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    if (tool.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "SIMILAR AI / TAGS", color = TextDimColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tool.tags, key = { it }) { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(Card2Color, RoundedCornerShape(8.dp))
                                        .border(0.5.dp, AmberAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(text = tag, color = TextColor, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * High premium-integrated secure web-portal dialog overlay.
 * Opens websites inside the application in an elegant dark web wrapper.
 */
@Composable
fun LiteBrowserDialog(
    initialUrl: String,
    onDismiss: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var progress by remember { mutableStateOf(0) }
    var isDesktopMode by remember { mutableStateOf(false) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BgColor
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardColor)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Browser",
                            tint = TextColor
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = canGoBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "◀",
                            color = if (canGoBack) AmberAccent else TextDimColor,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = canGoForward,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "▶",
                            color = if (canGoForward) AmberAccent else TextDimColor,
                            fontSize = 14.sp
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.reload() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "🔄",
                            color = AmberAccent,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(Card2Color, RoundedCornerShape(8.dp))
                            .border(0.5.dp, AmberAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = currentUrl.replace("https://", "").replace("http://", ""),
                            color = TextColor.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            isDesktopMode = !isDesktopMode
                            webViewRef?.let { webView ->
                                val settings = webView.settings
                                if (isDesktopMode) {
                                    settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                } else {
                                    settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                    settings.useWideViewPort = false
                                    settings.loadWithOverviewMode = false
                                }
                                webView.reload()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = if (isDesktopMode) "📱" else "🖥️",
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = { openUrlWithChromeCustomTabs(context, currentUrl) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Open in External Tab",
                            tint = AmberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (progress < 100) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = AmberAccent,
                        trackColor = Color.Transparent
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                        url?.let { currentUrl = it }
                                        view?.let {
                                            canGoBack = it.canGoBack()
                                            canGoForward = it.canGoForward()
                                        }
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        url?.let { currentUrl = it }
                                        view?.let {
                                            canGoBack = it.canGoBack()
                                            canGoForward = it.canGoForward()
                                        }
                                    }

                                    override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                        val uri = request?.url ?: return false
                                        val urlStr = uri.toString()
                                        if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
                                            return false
                                        }
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle invalid schema
                                        }
                                        return true
                                    }
                                }
                                webChromeClient = object : android.webkit.WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        progress = newProgress
                                    }
                                }
                                setDownloadListener { url, _, _, _, _ ->
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        ctx.startActivity(intent)
                                        Toast.makeText(ctx, "Handing download over to system handler", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(ctx, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                loadUrl(initialUrl)
                                webViewRef = this
                            }
                        },
                        update = { _ -> }
                    )
                }
            }
        }
    }
}
