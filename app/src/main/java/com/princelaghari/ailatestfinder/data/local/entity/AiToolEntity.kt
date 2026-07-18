package com.princelaghari.ailatestfinder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.princelaghari.ailatestfinder.domain.model.AiTool

@Entity(tableName = "ai_tools")
data class AiToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val toolUrl: String,
    val pricing: String,
    val platforms: String, // Comma-separated list
    val developer: String,
    val company: String,
    val status: String,
    val launchYear: String,
    val tags: String, // Comma-separated list
    val alternatives: String // Comma-separated list
) {
    fun toDomain(): AiTool {
        return AiTool(
            id = id,
            name = name,
            category = category,
            description = description,
            imageUrl = imageUrl,
            toolUrl = toolUrl,
            pricing = pricing,
            platforms = platforms.split(",").filter { it.isNotEmpty() },
            developer = developer,
            company = company,
            status = status,
            launchYear = launchYear,
            tags = tags.split(",").filter { it.isNotEmpty() },
            alternatives = alternatives.split(",").filter { it.isNotEmpty() }
        )
    }

    companion object {
        fun fromDomain(domain: AiTool): AiToolEntity {
            return AiToolEntity(
                id = domain.id,
                name = domain.name,
                category = domain.category,
                description = domain.description,
                imageUrl = domain.imageUrl,
                toolUrl = domain.toolUrl,
                pricing = domain.pricing,
                platforms = domain.platforms.joinToString(","),
                developer = domain.developer,
                company = domain.company,
                status = domain.status,
                launchYear = domain.launchYear,
                tags = domain.tags.joinToString(","),
                alternatives = domain.alternatives.joinToString(",")
            )
        }
    }
}
