package com.princelaghari.ailatestfinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.princelaghari.ailatestfinder.data.local.dao.AiToolDao
import com.princelaghari.ailatestfinder.data.local.entity.AiToolEntity

@Database(entities = [AiToolEntity::class], version = 1, exportSchema = false)
abstract class AiLatestFinderDatabase : RoomDatabase() {
    abstract fun aiToolDao(): AiToolDao
}
