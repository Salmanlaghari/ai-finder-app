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
     * Executes the usecase to fetch and filter AI tools.
     * All search and categorization filters are processed here dynamically.
     */
    operator fun invoke(query: String = "", category: String = "All"): Flow<List<AiTool>> {
        return repository.getAiTools().map { list ->
            list.filter { tool ->
                // Apply Category filter
                val matchesCategory = category == "All" || tool.category.equals(category, ignoreCase = true)

                // Apply Search/Query filter (matches name or description, case-insensitive)
                val matchesQuery = query.isEmpty() ||
                        tool.name.contains(query, ignoreCase = true) ||
                        tool.description.contains(query, ignoreCase = true)

                matchesCategory && matchesQuery
            }
        }
    }
}
