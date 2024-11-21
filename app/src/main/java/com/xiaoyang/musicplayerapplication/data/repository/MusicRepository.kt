// MusicRepository.kt
package com.xiaoyang.musicplayerapplication.data.repository

import com.xiaoyang.musicplayerapplication.network.ApiService
import com.xiaoyang.musicplayerapplication.data.model.Song

class MusicRepository(private val apiService: ApiService) {

    suspend fun fetchSongs(): List<Song> {
        return apiService.getSongs()
    }

    fun fetchSongById(songId: Int): Song? {
        val response = apiService.getSongById(songId).execute()
        return if (response.isSuccessful) response.body() else null
    }

}
