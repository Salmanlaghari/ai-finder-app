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
     * Executes the usecase to fetch, filter, and perform natural language search on AI tools.
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

                // 2. Resolve Smart Natural Language queries (e.g., "best image ai", "free chatbot")
                val matchesQuery = if (query.isEmpty()) {
                    true
                } else {
                    val cleanQuery = query.trim().lowercase()

                    // Direct contains checks
                    val inName = tool.name.lowercase().contains(cleanQuery)
                    val inDescription = tool.description.lowercase().contains(cleanQuery)
                    val inDeveloper = tool.developer.lowercase().contains(cleanQuery)
                    val inCompany = tool.company.lowercase().contains(cleanQuery)
                    val inTags = tool.tags.any { it.lowercase().contains(cleanQuery) }

                    // Token match for natural language (e.g., matching "assistant" in "coding assistant")
                    val queryTokens = cleanQuery.split("\\s+".toRegex()).filter { it.length > 2 }
                    val tokenMatches = queryTokens.isNotEmpty() && queryTokens.all { token ->
                        tool.name.lowercase().contains(token) ||
                        tool.description.lowercase().contains(token) ||
                        tool.tags.any { it.lowercase().contains(token) }
                    }

                    inName || inDescription || inDeveloper || inCompany || inTags || tokenMatches
                }

                matchesCategory && matchesQuery
            }
        }
    }
}
