package com.example.colorphone.retrofit

import retrofit2.http.GET

interface ApiRetrofit {
    @GET("posts")
    suspend fun getPost():List<PostNewsItem>
}