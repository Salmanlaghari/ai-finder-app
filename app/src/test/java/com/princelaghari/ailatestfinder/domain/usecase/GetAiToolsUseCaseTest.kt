package com.princelaghari.ailatestfinder.domain.usecase

import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAiToolsUseCaseTest {

    private val testTools = listOf(
        AiTool("1", "ChatGPT", "Text AI", "Conversational AI model", "", ""),
        AiTool("2", "Midjourney", "Image AI", "Artistic image generation", "", ""),
        AiTool("3", "Claude", "Text AI", "Next-gen assistant", "", "")
    )

    private val fakeRepository = object : AiToolRepository {
        override fun getAiTools(): Flow<List<AiTool>> {
            return flowOf(testTools)
        }
    }

    private val useCase = GetAiToolsUseCase(fakeRepository)

    @Test
    fun `when category is All and query is empty, returns all tools`() = runBlocking {
        val result = useCase(query = "", category = "All").first()
        assertEquals(3, result.size)
    }

    @Test
    fun `when category is Text AI, returns only matching category`() = runBlocking {
        val result = useCase(query = "", category = "Text AI").first()
        assertEquals(2, result.size)
        assertTrue(result.all { it.category == "Text AI" })
    }

    @Test
    fun `when search query is specified, filters by name or description case insensitively`() = runBlocking {
        val result = useCase(query = "gpt", category = "All").first()
        assertEquals(1, result.size)
        assertEquals("ChatGPT", result[0].name)
    }

    @Test
    fun `when search query does not match, returns empty list`() = runBlocking {
        val result = useCase(query = "Sora", category = "All").first()
        assertTrue(result.isEmpty())
    }
}
