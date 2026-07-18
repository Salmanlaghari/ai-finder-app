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
        AiTool(
            id = "1",
            name = "ChatGPT",
            category = "Text AI",
            description = "Conversational AI model",
            status = "Trending",
            tags = listOf("free chatbot", "chatbot")
        ),
        AiTool(
            id = "2",
            name = "Midjourney",
            category = "Image AI",
            description = "Artistic image generation",
            status = "Popular",
            tags = listOf("logo maker", "best image ai")
        ),
        AiTool(
            id = "3",
            name = "Claude",
            category = "Text AI",
            description = "Next-gen assistant",
            status = "Trending",
            tags = listOf("chatbot")
        )
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
    fun `when status like Trending is selected, returns matching status`() = runBlocking {
        val result = useCase(query = "", category = "Trending").first()
        assertEquals(2, result.size)
        assertTrue(result.all { it.status == "Trending" })
    }

    @Test
    fun `when semantic search is queried, sorts results by highest matching score`() = runBlocking {
        // Querying "ChatGPT" should return ChatGPT first since it gets highest exact-name match score
        val result = useCase(query = "ChatGPT", category = "All").first()
        assertTrue(result.isNotEmpty())
        assertEquals("ChatGPT", result[0].name)
    }

    @Test
    fun `when search query is case-insensitive, filters properly`() = runBlocking {
        val result = useCase(query = "CHATBOT", category = "All").first()
        assertEquals(2, result.size)
    }
}
