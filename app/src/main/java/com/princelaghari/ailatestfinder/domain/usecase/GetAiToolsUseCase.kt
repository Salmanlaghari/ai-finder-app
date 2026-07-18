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
     * Executes the usecase to fetch, filter, and perform advanced natural language typo-tolerant search on AI tools.
     * Integrates statuses ("Trending", "New", "Popular") and standard categories ("Text AI", etc.).
     */
    operator fun invoke(query: String = "", category: String = "All"): Flow<List<AiTool>> {
        return repository.getAiTools().map { list ->
            list.filter { tool ->
                // 1. Resolve Category Chips & Status filters
                val matchesCategory = when (category) {
                    "All" -> true
                    "Trending", "New", "Popular" -> tool.status.equals(category, ignoreCase = true)
                    else -> tool.category.equals(category, ignoreCase = true)
                }

                // 2. Resolve Smart Natural Language queries with Typo Tolerance
                val matchesQuery = if (query.isEmpty()) {
                    true
                } else {
                    val cleanQuery = query.trim().lowercase()

                    // Direct contains checks (case-insensitive partial matching)
                    val inName = tool.name.lowercase().contains(cleanQuery)
                    val inDescription = tool.description.lowercase().contains(cleanQuery)
                    val inDeveloper = tool.developer.lowercase().contains(cleanQuery)
                    val inCompany = tool.company.lowercase().contains(cleanQuery)
                    val inCategory = tool.category.lowercase().contains(cleanQuery)
                    val inTags = tool.tags.any { it.lowercase().contains(cleanQuery) }

                    // Token match for natural language (matching "assistant" in "coding assistant")
                    val queryTokens = cleanQuery.split("\\s+".toRegex()).filter { it.length > 2 }
                    val tokenMatches = queryTokens.isNotEmpty() && queryTokens.all { token ->
                        tool.name.lowercase().contains(token) ||
                        tool.description.lowercase().contains(token) ||
                        tool.tags.any { it.lowercase().contains(token) } ||
                        tool.company.lowercase().contains(token) ||
                        tool.developer.lowercase().contains(token) ||
                        tool.category.lowercase().contains(token)
                    }

                    // Typo Tolerance: Levenshtein distance check on tool name
                    val nameWords = tool.name.lowercase().split("\\s+".toRegex())
                    val nameWordTypoMatch = queryTokens.isNotEmpty() && queryTokens.any { token ->
                        nameWords.any { word ->
                            val dist = levenshteinDistance(token, word)
                            dist <= when {
                                token.length > 5 -> 2
                                token.length > 3 -> 1
                                else -> 0
                            }
                        }
                    }

                    inName || inDescription || inDeveloper || inCompany || inCategory || inTags || tokenMatches || nameWordTypoMatch
                }

                matchesCategory && matchesQuery
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
