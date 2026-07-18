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
                val terms = cleanQuery.lowercase().split("\\s+".toRegex()).filter { it.isNotEmpty() }
                if (terms.isEmpty()) {
                    list
                } else {
                    list.mapNotNull { tool ->
                        val nameLower = tool.name.lowercase()
                        val devLower = tool.developer.lowercase()
                        val compLower = tool.company.lowercase()
                        val descLower = tool.description.lowercase()
                        val catLower = tool.category.lowercase()
                        val statusLower = tool.status.lowercase()
                        val tagsLower = tool.tags.map { it.lowercase() }
                        val nameWords = nameLower.split("\\s+".toRegex())

                        // Check if all search terms match at least one metadata field in the tool (supporting typo-tolerance on terms)
                        val matchesAllTerms = terms.all { term ->
                            val matchesField = nameLower.contains(term) ||
                                    devLower.contains(term) ||
                                    compLower.contains(term) ||
                                    descLower.contains(term) ||
                                    catLower.contains(term) ||
                                    statusLower.contains(term) ||
                                    tagsLower.any { it.contains(term) }

                            if (matchesField) {
                                true
                            } else {
                                // Typo tolerance check for this term on name words
                                term.length > 2 && nameWords.any { word ->
                                    val dist = levenshteinDistance(term, word)
                                    dist <= when {
                                        term.length > 5 -> 2
                                        term.length > 3 -> 1
                                        else -> 0
                                    }
                                }
                            }
                        }

                        if (matchesAllTerms) {
                            var score = 0

                            // 1. Direct/Sub-string Name Match Scoring on full query
                            if (nameLower.equals(cleanQuery, ignoreCase = true)) {
                                score += 150
                            } else if (nameLower.startsWith(cleanQuery, ignoreCase = true)) {
                                score += 80
                            } else if (nameLower.contains(cleanQuery, ignoreCase = true)) {
                                score += 50
                            }

                            // 2. Score individual terms matching fields
                            terms.forEach { term ->
                                val hasTypoMatch = term.length > 2 && nameWords.any { word ->
                                    levenshteinDistance(term, word) <= when {
                                        term.length > 5 -> 2
                                        term.length > 3 -> 1
                                        else -> 0
                                    }
                                }

                                if (nameLower.contains(term)) score += 40
                                if (catLower.contains(term)) score += 35
                                if (statusLower.contains(term)) score += 30
                                if (tagsLower.any { it.contains(term) }) score += 25
                                if (descLower.contains(term)) score += 15
                                if (devLower.contains(term) || compLower.contains(term)) score += 20
                                if (hasTypoMatch) score += 30
                            }

                            // 3. Status Boost
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
                            if (flagshipAIList.any { nameLower.contains(it) || tool.id.contains(it) }) {
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
