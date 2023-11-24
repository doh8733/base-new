package com.example.colorphone.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postCacheEntity: PostCacheEntity):Long

    @Query("SELECT * FROM post_database")
    suspend fun getPostNews() :List<PostCacheEntity>
}