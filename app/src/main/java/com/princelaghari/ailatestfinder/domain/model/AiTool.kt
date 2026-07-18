package com.princelaghari.ailatestfinder.domain.model

data class AiTool(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val toolUrl: String = "",

    // Expanded Premium Fields
    val pricing: String = "Freemium",
    val platforms: List<String> = listOf("Web"),
    val developer: String = "AI Community",
    val company: String = "AI Corp",
    val status: String = "Verified",
    val launchYear: String = "2024",
    val tags: List<String> = emptyList(),
    val alternatives: List<String> = emptyList()
)
