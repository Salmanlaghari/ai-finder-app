package com.princelaghari.ailatestfinder.domain.usecase

import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAiToolsUseCase @Inject constructor(
    private val repository: AiToolRepository
) {
    /**
     * Executes the usecase to fetch, filter, rank, and perform advanced natural language search on AI tools.
     * Integrates statuses ("Trending", "New", "Popular") and standard categories ("Text AI", etc.).
     */
    operator fun invoke(query: String = "", category: String = "All"): Flow<List<AiTool>> {
        return repository.getAiTools().map { list ->
            val cleanQuery = query.trim()
            if (cleanQuery.isEmpty()) {
                // If there's no search query, filter by category chip as usual
                list.filter { tool ->
                    when (category) {
                        "All" -> true
                        "Trending", "New", "Popular" -> tool.status.equals(category, ignoreCase = true)
                        else -> tool.category.equals(category, ignoreCase = true)
                    }
                }
            } else {
                // Multi-field intelligent search logic: search across name, company, description, category, tags/keywords, and aliases.
                list.mapNotNull { tool ->
                    val matchesName = tool.name.contains(cleanQuery, ignoreCase = true)
                    val matchesDeveloper = tool.developer.contains(cleanQuery, ignoreCase = true)
                    val matchesCompany = tool.company.contains(cleanQuery, ignoreCase = true)
                    val matchesDescription = tool.description.contains(cleanQuery, ignoreCase = true)
                    val matchesCategory = tool.category.contains(cleanQuery, ignoreCase = true)
                    val matchesTags = tool.tags.any { it.contains(cleanQuery, ignoreCase = true) }
                    val matchesAlternatives = tool.alternatives.any { it.contains(cleanQuery, ignoreCase = true) }

                    // Typo Tolerance: Levenshtein distance matching on name tokens
                    val queryTokens = cleanQuery.split("\\s+".toRegex()).filter { it.length > 2 }
                    val nameWords = tool.name.lowercase().split("\\s+".toRegex())
                    val hasTypoMatch = queryTokens.isNotEmpty() && queryTokens.any { token ->
                        nameWords.any { word ->
                            val dist = levenshteinDistance(token, word)
                            dist <= when {
                                token.length > 5 -> 2
                                token.length > 3 -> 1
                                else -> 0
                            }
                        }
                    }

                    if (matchesName || matchesDeveloper || matchesCompany || matchesDescription || matchesCategory || matchesTags || matchesAlternatives || hasTypoMatch) {
                        // Compute highly intelligent relevance-ranking score
                        var score = 0

                        // 1. Direct Name Match Scoring
                        if (tool.name.equals(cleanQuery, ignoreCase = true)) {
                            score += 150
                        } else if (tool.name.startsWith(cleanQuery, ignoreCase = true)) {
                            score += 80
                        } else if (matchesName) {
                            score += 50
                        }

                        // 2. Metadata Field Matching
                        if (matchesCategory) {
                            score += 40
                        }
                        if (matchesTags) {
                            score += 30
                        }
                        if (matchesAlternatives) {
                            score += 25
                        }
                        if (matchesDescription) {
                            score += 15
                        }
                        if (matchesDeveloper || matchesCompany) {
                            score += 20
                        }
                        if (hasTypoMatch) {
                            score += 30
                        }

                        // 3. Intelligent Status Rank: Popular and Trending AI rank higher!
                        if (tool.status.equals("Popular", ignoreCase = true)) {
                            score += 60
                        } else if (tool.status.equals("Trending", ignoreCase = true)) {
                            score += 40
                        }

                        // 4. Flagship AI Priority Bonus (+100 points) to place high-profile giants first
                        val flagshipAIList = listOf(
                            "chatgpt", "gemini", "claude", "grok", "perplexity", "deepseek",
                            "veo", "sora", "midjourney", "notebooklm", "imagen", "cursor", "bolt", "lovable",
                            "runway", "leonardo", "ideogram", "elevenlabs", "suno", "flux"
                        )
                        if (flagshipAIList.any { tool.name.contains(it, ignoreCase = true) || tool.id.contains(it, ignoreCase = true) }) {
                            score += 100
                        }

                        Pair(tool, score)
                    } else {
                        null
                    }
                }
                .sortedByDescending { it.second } // Sort by computed score descending (Highest Rank First)
                .map { it.first }
            }
        }
    }

    /**
     * Helper to compute Levenshtein distance between two strings for typo tolerance.
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // Deletion
                    dp[i][j - 1] + 1,      // Insertion
                    dp[i - 1][j - 1] + cost // Substitution
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}