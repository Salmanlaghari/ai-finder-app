package com.princelaghari.ailatestfinder.domain.repository

import com.princelaghari.ailatestfinder.domain.model.AiTool
import kotlinx.coroutines.flow.Flow

interface AiToolRepository {
    /**
     * Retrieves the list of AI tools from Firebase Firestore with a robust mock fallback.
     */
    fun getAiTools(): Flow<List<AiTool>>
}
