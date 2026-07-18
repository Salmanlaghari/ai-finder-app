package com.princelaghari.ailatestfinder.data.local.dao

import androidx.room.*
import com.princelaghari.ailatestfinder.data.local.entity.AiToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiToolDao {
    @Query("SELECT * FROM ai_tools")
    fun getAllAiToolsFlow(): Flow<List<AiToolEntity>>

    @Query("SELECT * FROM ai_tools")
    suspend fun getAllAiTools(): List<AiToolEntity>

    @Query("SELECT * FROM ai_tools WHERE id = :id LIMIT 1")
    suspend fun getAiToolById(id: String): AiToolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tools: List<AiToolEntity>)

    @Query("DELETE FROM ai_tools")
    suspend fun deleteAll()
}
