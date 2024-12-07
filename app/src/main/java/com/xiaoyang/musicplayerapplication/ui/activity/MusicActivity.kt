package com.xiaoyang.musicplayerapplication.ui.activity

import com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.xiaoyang.musicplayerapplication.data.repository.MusicRepository
import com.xiaoyang.musicplayerapplication.databinding.ActivityMusicBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicActivity : BaseActivity() {

    private lateinit var binding: ActivityMusicBinding
    private lateinit var songAdapter: SongAdapter
    private lateinit var musicRepository: MusicRepository
    private lateinit var apiService: ApiService  // 声明 apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 通过 ViewBinding 加载布局
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 创建 ApiService 实例
        apiService = ApiService.create()

        // 初始化音乐数据
        musicRepository = MusicRepository(apiService)

        // 获取歌曲列表
        Log.d("MusicActivity", "开始请求歌曲数据")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val songs = musicRepository.fetchSongs()
                Log.d("MusicActivity", "成功获取歌曲数据: ${songs.size} 首歌")
                withContext(Dispatchers.Main) {
                    // 设置适配器
                    songAdapter = SongAdapter(songs) { song ->
                        // 点击事件，传递音频 URL、封面图片 URL、歌名和歌手名进行播放
                        val intent = Intent(this@MusicActivity, PlayerActivity::class.java)

                        // 将音频 URL、封面图片 URL、歌名和歌手传递给 PlayerActivity
                        intent.putExtra("audio_url", song.audio_url)
                        intent.putExtra("cover_url", song.cover_url)  // 传递封面图片 URL
                        intent.putExtra("song_title", song.m_name)    // 传递歌名
                        intent.putExtra("artist_name", song.artist_name)  // 传递歌手名字
                        intent.putExtra("song_id", song.id)           // 传递歌曲 id

                        // 启动 PlayerActivity
                        startActivity(intent)
                    }

                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MusicActivity)
                    binding.recyclerView.adapter = songAdapter
                    Log.d("MusicActivity", "RecyclerView 适配器已设置")
                }
            } catch (e: Exception) {
                Log.e("MusicActivity", "获取歌曲数据失败: ${e.message}")
            }
        }
    }
}
