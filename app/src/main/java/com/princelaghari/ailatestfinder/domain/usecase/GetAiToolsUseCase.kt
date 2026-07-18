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
     * Executes the usecase to fetch, filter, rank, and perform advanced natural language typo-tolerant search on AI tools.
     * Integrates statuses ("Trending", "New", "Popular") and standard categories ("Text AI", etc.).
     */
    operator fun invoke(query: String = "", category: String = "All"): Flow<List<AiTool>> {
        return repository.getAiTools().map { list ->
            if (query.isEmpty()) {
                // If there's no search query, filter by category chip as usual
                list.filter { tool ->
                    when (category) {
                        "All" -> true
                        "Trending", "New", "Popular" -> tool.status.equals(category, ignoreCase = true)
                        else -> tool.category.equals(category, ignoreCase = true)
                    }
                }
            } else {
                // Typo-tolerant relevance-ranked global search (ignoring category chips to search entire database)
                val cleanQuery = query.trim().lowercase()

                // Map common query keywords to core categories/tags dynamically for advanced semantic indexing
                val mappedCategory = when {
                    cleanQuery.contains("chat") -> "Text AI"
                    cleanQuery.contains("text") -> "Text AI"
                    cleanQuery.contains("image") || cleanQuery.contains("art") || cleanQuery.contains("draw") -> "Image AI"
                    cleanQuery.contains("video") || cleanQuery.contains("movie") || cleanQuery.contains("cinema") -> "Video AI"
                    cleanQuery.contains("music") || cleanQuery.contains("song") -> "Music AI"
                    cleanQuery.contains("voice") || cleanQuery.contains("audio") || cleanQuery.contains("speech") || cleanQuery.contains("sound") -> "Audio AI"
                    cleanQuery.contains("code") || cleanQuery.contains("program") || cleanQuery.contains("developer") -> "Coding AI"
                    cleanQuery.contains("agent") -> "Agents"
                    cleanQuery.contains("business") -> "Business"
                    cleanQuery.contains("market") -> "Marketing"
                    cleanQuery.contains("research") || cleanQuery.contains("academic") -> "Research"
                    cleanQuery.contains("med") || cleanQuery.contains("doctor") -> "Medical"
                    cleanQuery.contains("finance") || cleanQuery.contains("money") || cleanQuery.contains("stock") -> "Finance"
                    cleanQuery.contains("legal") || cleanQuery.contains("law") -> "Legal"
                    cleanQuery.contains("educat") || cleanQuery.contains("learn") || cleanQuery.contains("student") -> "Education"
                    cleanQuery.contains("pdf") -> "PDF"
                    cleanQuery.contains("productiv") || cleanQuery.contains("notion") -> "Productivity"
                    cleanQuery.contains("design") -> "Design"
                    cleanQuery.contains("3d") -> "3D"
                    cleanQuery.contains("game") || cleanQuery.contains("gaming") -> "Gaming"
                    cleanQuery.contains("open source") || cleanQuery.contains("free") -> "Open Source"
                    else -> ""
                }

                list.mapNotNull { tool ->
                    var score = 0

                    // 1. Exact Name match (Highest Rank)
                    if (tool.name.equals(cleanQuery, ignoreCase = true)) {
                        score += 150
                    }
                    // 2. Name starts with query
                    else if (tool.name.lowercase().startsWith(cleanQuery)) {
                        score += 80
                    }
                    // 3. Name contains query word
                    else if (tool.name.lowercase().contains(cleanQuery)) {
                        score += 50
                    }

                    // 4. Mapped Category matches
                    if (mappedCategory.isNotEmpty() && tool.category.equals(mappedCategory, ignoreCase = true)) {
                        score += 60
                    }

                    // 5. Tags contains match
                    if (tool.tags.any { it.equals(cleanQuery, ignoreCase = true) }) {
                        score += 40
                    } else if (tool.tags.any { it.lowercase().contains(cleanQuery) }) {
                        score += 25
                    }

                    // 6. Description contains match
                    if (tool.description.lowercase().contains(cleanQuery)) {
                        score += 15
                    }

                    // 7. Company/Developer matches
                    if (tool.company.lowercase().contains(cleanQuery) || tool.developer.lowercase().contains(cleanQuery)) {
                        score += 20
                    }

                    // 8. Typo Tolerance: Levenshtein distance matching on name tokens
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
                    if (hasTypoMatch) {
                        score += 35
                    }

                    if (score > 0) {
                        Pair(tool, score)
                    } else {
                        null
                    }
                }
                .sortedByDescending { it.second } // Sort by computed relevance score descending (Highest Rank First)
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
