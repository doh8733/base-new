package com.example.colorphone.di

import com.example.colorphone.repository.HomeRepository
import com.example.colorphone.retrofit.ApiRetrofit
import com.example.colorphone.retrofit.NetWorkMapper
import com.example.colorphone.room.BlogDao
import com.example.colorphone.room.PostCacheMapper

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideHomeRepository(
        blogDao: BlogDao,
        retrofit: ApiRetrofit,
        netWorkMapper: NetWorkMapper,
        postCacheMapper: PostCacheMapper
    ): HomeRepository {
        return HomeRepository(blogDao, retrofit, netWorkMapper, postCacheMapper)
    }
}