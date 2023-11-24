package com.example.colorphone.room


import com.example.colorphone.model.Post
import com.example.colorphone.util.EntityMapper
import javax.inject.Inject

class PostCacheMapper @Inject constructor() : EntityMapper<PostCacheEntity, Post> {
    override fun mapFromEntity(entity: PostCacheEntity): Post {
        entity.apply {
            return Post(ids,body, id, title, userId)
        }
    }

    override fun mapToEntity(domainModel: Post): PostCacheEntity {
        domainModel.apply {
            return PostCacheEntity(ids, id, body, title, userId)
        }
    }

    override fun mapFromList(list: List<PostCacheEntity>): List<Post> {
        return list.map {
            mapFromEntity(it)
        }    }

}