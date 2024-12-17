package com.xiaoyang.musicplayerapplication.network

import com.xiaoyang.musicplayerapplication.data.model.Song
import com.xiaoyang.musicplayerapplication.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("user/register")
    fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("user/login")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @GET("songs")  // 后端接口是 /songs
    suspend fun getSongs(): List<Song>

    @GET("songs/getById")
    fun getSongById(@Query("id") id: Int): Call<Song>

    @FormUrlEncoded
    @POST("user/changePassword")
    fun changePassword(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("collection/add")
    fun addToCollection(
        @Field("username") username: String,
        @Field("musicId") musicId: Int
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("collection/judge")
    fun judgelike(
        @Field("username") username: String,
        @Field("musicId") musicId: Int
    ): Call<Boolean>

    @DELETE("collection/deletelike")
    fun removeFromCollection(
        @Query("username") username: String,
        @Query("musicId") musicId: Int
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("collection/list") // 根据后端的接口路径
    suspend fun listlike(
        @Field("username") username: String
    ): List<Song>




    companion object {
        // 通过 RetrofitClient 获取 Retrofit 实例，并创建 ApiService
        fun create(): ApiService {
            return RetrofitClient.instance.create(ApiService::class.java)
        }
    }

}
