package com.example.colorphone.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "post_database")
data class PostCacheEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ids")
    var ids: Int?,
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "body")
    var body: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "userId")
    var userId: Int
)