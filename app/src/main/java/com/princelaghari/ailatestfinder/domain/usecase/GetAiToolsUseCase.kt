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
                // Multi-field search logic: search across multiple fields simultaneously using .contains(..., ignoreCase = true)
                list.mapNotNull { tool ->
                    val matchesName = tool.name.contains(cleanQuery, ignoreCase = true)
                    val matchesDeveloper = tool.developer.contains(cleanQuery, ignoreCase = true)
                    val matchesCompany = tool.company.contains(cleanQuery, ignoreCase = true)
                    val matchesDescription = tool.description.contains(cleanQuery, ignoreCase = true)
                    val matchesCategory = tool.category.contains(cleanQuery, ignoreCase = true)
                    val matchesTags = tool.tags.any { it.contains(cleanQuery, ignoreCase = true) }

                    if (matchesName || matchesDeveloper || matchesCompany || matchesDescription || matchesCategory || matchesTags) {
                        // Compute a robust relevance-ranking score
                        var score = 0
                        if (tool.name.equals(cleanQuery, ignoreCase = true)) {
                            score += 150
                        } else if (tool.name.startsWith(cleanQuery, ignoreCase = true)) {
                            score += 80
                        } else if (matchesName) {
                            score += 50
                        }

                        if (matchesCategory) {
                            score += 60
                        }
                        if (matchesTags) {
                            score += 40
                        }
                        if (matchesDescription) {
                            score += 15
                        }
                        if (matchesDeveloper || matchesCompany) {
                            score += 20
                        }

                        // Flagship AI Priority Bonus (+100 points)
                        val flagshipAIList = listOf(
                            "chatgpt", "gemini", "claude", "grok", "perplexity", "deepseek",
                            "veo", "sora", "midjourney", "notebooklm", "imagen"
                        )
                        if (flagshipAIList.any { tool.name.contains(it, ignoreCase = true) || tool.id.contains(it, ignoreCase = true) }) {
                            score += 100
                        }

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
}