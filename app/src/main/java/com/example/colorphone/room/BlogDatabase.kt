package com.example.colorphone.room

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [PostCacheEntity::class], version = 1, exportSchema = false)
abstract class BlogDatabase :RoomDatabase() {
    abstract fun blogDao():BlogDao
    companion object{
        val DATABASE_NAME ="blog_db"
    }
}