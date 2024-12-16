package com.xiaoyang.musicplayerapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://39.104.54.87:8080/"

    // 创建 OkHttpClient，添加日志拦截器
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // 设置日志拦截器级别
            })
            .build()
    }

    // 创建 Retrofit 实例
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 设置 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // JSON 转换器
            .build()
    }
}
