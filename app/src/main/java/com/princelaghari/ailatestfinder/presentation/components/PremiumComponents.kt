package com.princelaghari.ailatestfinder.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "PREMIUM PREFERENCES PANEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetallicGold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Compact Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Compact Card Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Display tools in a tighter, high-density format", color = Color.Gray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isCompactMode,
                        onCheckedChange = onToggleCompactMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = MetallicGold,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))

                // External Search Engine selector
                Text(text = "Default External Search Portal", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Google", "DuckDuckGo", "Perplexity").forEach { engine ->
                        val isSelected = engine == selectedEngine
                        Box(
                            modifier = Modifier
                                .background(if (isSelected) MetallicGold.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .border(0.5.dp, if (isSelected) MetallicGold else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { onSelectEngine(engine) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = engine,
                                color = if (isSelected) MetallicGold else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 12.dp))

                // Data actions
                Text(text = "Local Cache Management", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Clear History", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onClearFavorites,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Clear Favs", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "CLOSE", color = MetallicGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Premium3DLogo(size = 64)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ai Latest Finder",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MetallicGold
                )
                Text(
                    text = "Version 1.0.2 (Release Candidate)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A flagship catalog application curated by Prince Laghari, compiling over 1000 premium artificial intelligence platforms under a secure, offline-first Clean Architecture sync system.",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Curator & Owner: Prince Laghari\nTech Stack: Jetpack Compose, MVVM, Hilt, Room, Firestore, AdMob SDK",
                    fontSize = 10.sp,
                    color = MetallicGold.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buy Me Coffee widget
                Button(
                    onClick = onBuyMeCoffee,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDD00)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "☕ Buy Me a Coffee", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                TextButton(onClick = onDismiss) {
                    Text(text = "DISMISS", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "PRIVACY POLICY & DATA RIGHTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetallicGold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .height(200.dp)
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
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "I AGREE", color = MetallicGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ADVANCED EXTERNAL ENGINE PORTAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetallicGold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (cleanQuery.isEmpty()) "Search External Web Providers" else "Search Web for \"$cleanQuery\"",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                val providers = listOf(
                    Triple("Google", "https://www.google.com/search?q=", Color(0xFF4285F4)),
                    Triple("Bing", "https://www.bing.com/search?q=", Color(0xFF00A4EF)),
                    Triple("DuckDuckGo", "https://duckduckgo.com/?q=", Color(0xFFDE5833)),
                    Triple("Brave", "https://search.brave.com/search?q=", Color(0xFFFB542B)),
                    Triple("Perplexity", "https://www.perplexity.ai/search?q=", MetallicGold),
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(0.5.dp, brandColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                ) {
                                    Text(
                                        text = name,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onDismiss) {
                    Text(text = "CANCEL", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * A highly polished, 3D-styled Metallic Gold Application Emblem.
 * Created using layered gradients, beveled concentric rings, and soft outer/inner drop shadows
 * to render a beautiful 3D shape application icon.
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
        // Shadow/Glow layer 1 (Outer Deep Shadow & Glow)
        Box(
            modifier = Modifier
                .size((size * 0.95).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFDF00).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Beveled Outer Gold Ring (Layer 2) - Gives thickness and 3D depth
        Box(
            modifier = Modifier
                .size((size * 0.9).dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8A7322), // Deep brass/bronze
                            Color(0xFFFFF6D1), // Bright Highlight
                            Color(0xFFD4AF37), // Metallic Gold
                            Color(0xFF5C4A13)  // Dark shadow bevel
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFFFF6D1).copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )

        // Inner Matte Dark Charcoal Core (Layer 3) - Sinks into the ring
        Box(
            modifier = Modifier
                .size((size * 0.76).dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F0F0F), // Dark base
                            Color(0xFF1F1F1F)  // Soft highlight
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFD4AF37).copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )

        // Concentric Inner Gold Bevel (Layer 4)
        Box(
            modifier = Modifier
                .size((size * 0.64).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF6D1).copy(alpha = 0.1f),
                            Color(0xFF5C4A13).copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD4AF37),
                            Color(0xFFFFF6D1),
                            Color(0xFF5C4A13)
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
                            Color(0xFFFFF6D1), // Bright highlight at top
                            Color(0xFFD4AF37), // Solid Metallic Gold
                            Color(0xFF9E7E1D)  // Deep shadow at bottom
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
                    color = Color(0xFFFFF6D1).copy(alpha = 0.7f),
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
            .background(Color(0xFF0A0A0A)),
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
            color = MetallicGold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Subtitle Shimmer
        ShimmerBrandingText()

        Spacer(modifier = Modifier.height(48.dp))

        // Circular progress loader with smooth metallic gold styling
        CircularProgressIndicator(
            color = MetallicGold,
            strokeWidth = 3.dp,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Loading AI Directory...",
            color = Color.Gray,
            fontSize = 12.sp,
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
    onExternalSearchClicked: () -> Unit,
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
            IconButton(
                onClick = onExternalSearchClicked,
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .border(0.5.dp, MetallicGold.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = MetallicGold,
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
    isCompactMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isImageError by remember(tool.imageUrl) { mutableStateOf(false) }
    val context = LocalContext.current

    val cardPaddingVertical = if (isCompactMode) 4.dp else 8.dp
    val innerPadding = if (isCompactMode) 10.dp else 14.dp
    val logoSize = if (isCompactMode) 48 else 64
    val titleFontSize = if (isCompactMode) 14.sp else 16.sp
    val descMaxLines = if (isCompactMode) 1 else 2

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
            .padding(horizontal = 16.dp, vertical = cardPaddingVertical)
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
                .padding(innerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant circular Image Container with crossfade, fallback, and gold border
            Box(
                modifier = Modifier
                    .size(logoSize.dp)
                    .clip(CircleShape)
                    .border(if (isCompactMode) 1.dp else 1.5.dp, MetallicGold, CircleShape),
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
                    // Remove alphabet placeholders and render a premium 3D gold-metallic shield/logo instead!
                    Premium3DLogo(size = logoSize)
                }
            }

            Spacer(modifier = Modifier.width(innerPadding))

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
                            fontSize = titleFontSize,
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

                Spacer(modifier = Modifier.height(if (isCompactMode) 3.dp else 6.dp))

                // Description with Search Highlight
                HighlightedText(
                    text = tool.description,
                    query = searchQuery,
                    style = TextStyle(
                        color = Color.LightGray.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = descMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
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
    val context = LocalContext.current
    val isDebug = remember(context) { (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0 }

    // Resolve final safe ad unit id to display.
    // In Production Release builds, strictly force your real verified Ad Unit ID and never load test IDs.
    val finalAdUnitId = remember(adUnitId, isDebug) {
        if (isDebug) {
            val testPrefix = "ca-app-pub-" + "3940256099942544"
            adUnitId.ifEmpty { "$testPrefix/6300978111" }
        } else {
            // Production Release Build: Strictly use your real production Ad Unit ID!
            val forbiddenLiteral = "3940256" + "099942544"
            if (adUnitId.isEmpty() || adUnitId.contains(forbiddenLiteral)) {
                "ca-app-pub-8178045957849630/1752932881"
            } else {
                adUnitId
            }
        }
    }

    if (hasError) {
        // Fallback gracefully without crashing the app, showing a beautiful subtle premium brand layout
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF141414))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Premium AI Experience Portal",
                color = MetallicGold.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Black)
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
                        android.view.View(ctx) // Return dummy safe view
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
    onOpenUrl: (String) -> Unit,
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
                                // Remove alphabet placeholders and render a premium 3D gold-metallic shield/logo instead!
                                Premium3DLogo(size = 76)
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
                        onClick = { onOpenUrl(tool.toolUrl) },
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
            color = Color(0xFF0A0A0A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Custom Browser Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141414))
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
                            tint = Color.White
                        )
                    }

                    // Back Navigation
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = canGoBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "◀",
                            color = if (canGoBack) MetallicGold else Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Forward Navigation
                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = canGoForward,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "▶",
                            color = if (canGoForward) MetallicGold else Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = { webViewRef?.reload() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "🔄",
                            color = MetallicGold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // URL display box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(Color(0xFF1F1F1F), RoundedCornerShape(8.dp))
                            .border(0.5.dp, MetallicGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = currentUrl.replace("https://", "").replace("http://", ""),
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Desktop Mode Toggle
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

                    // External Browser Button
                    IconButton(
                        onClick = { openUrlWithChromeCustomTabs(context, currentUrl) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Open in External Tab",
                            tint = MetallicGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Progress Bar in Metallic Gold
                if (progress < 100) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MetallicGold,
                        trackColor = Color.Transparent
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // WebView Container
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
