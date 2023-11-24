package com.example.colorphone.repository

import android.util.Log
import com.example.colorphone.model.Post
import com.example.colorphone.retrofit.ApiRetrofit
import com.example.colorphone.retrofit.NetWorkMapper
import com.example.colorphone.room.BlogDao
import com.example.colorphone.room.PostCacheMapper
import com.example.colorphone.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeRepository(
    private val blogDao: BlogDao,
    private val retrofit: ApiRetrofit,
    private val netWorkMapper: NetWorkMapper,
    private val postCacheMapper: PostCacheMapper
) {
    suspend fun getPostNew(): Flow<DataState<List<Post>>> = flow {
        emit(DataState.Loading)
        try {
            val networkPost = retrofit.getPost()
            val posMapper = netWorkMapper.mapFromList(networkPost)
            Log.e("dddd", "getPostNew: ${networkPost}")
            for (i in posMapper){
                blogDao.insertPost(postCacheMapper.mapToEntity(i))
            }
            val cachePost = blogDao.getPostNews()
            emit(DataState.Success(postCacheMapper.mapFromList(cachePost)))
            Log.e("ahhabb", "getPostNew: ${cachePost} thanh cong roiah")

        } catch (e: Exception) {
            emit(DataState.Error(e))
            Log.e("ahha", "getPostNew: ${e.message} that bai roi")
        }
    }
}