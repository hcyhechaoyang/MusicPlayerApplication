package com.xiaoyang.musicplayerapplication.ui.activity

import com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyang.musicplayerapplication.data.model.UserResponse
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
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 通过 ViewBinding 加载布局
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkLoginStatus()
        binding.profileImage.setOnClickListener {
            val intent = Intent(this@MusicActivity, UserCenterActivity::class.java)
            // 启动 UserCenterActivity
            startActivity(intent)
        }
        // 创建 ApiService 实例
        apiService = ApiService.create()
        // 初始化音乐数据
        musicRepository = MusicRepository(apiService)
        // 获取歌曲列表
        Log.d("MusicActivity", "开始请求歌曲数据")
        loadSongs()
        binding.search.setOnClickListener {
            val searchName = binding.searchBox.text.toString()

            if (searchName.isNotEmpty()) {
                // 当搜索框不为空时，开始搜索歌曲
                searchSongs(searchName)
            }
        }
    }

    // 歌曲搜索
    private fun searchSongs(query: String) {
        Log.d("MusicActivity", "开始进行歌曲搜索：$query")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 调用 MusicRepository 中的搜索方法，这里假设有一个 fetchSongsBySearch 方法
                val searchResults = musicRepository.fetchNameSongs(query)
                Log.d("MusicActivity", "成功获取搜索结果：${searchResults.size} 首歌")

                withContext(Dispatchers.Main) {
                    if (searchResults.isNotEmpty()) {
                        // 设置搜索结果的适配器
                        songAdapter = SongAdapter(searchResults) { song ->
                            // 点击事件，传递音频 URL、封面图片 URL、歌名和歌手名进行播放
                            val intent = Intent(this@MusicActivity, PlayerActivity::class.java)

                            // 将音频 URL、封面图片 URL、歌名和歌手传递给 PlayerActivity
                            intent.putExtra("audio_url", song.audioUrl)
                            intent.putExtra("cover_url", song.coverUrl)  // 传递封面图片 URL
                            intent.putExtra("song_title", song.mname)    // 传递歌名
                            intent.putExtra("artist_name", song.artistName)  // 传递歌手名字
                            intent.putExtra("song_id", song.id)           // 传递歌曲 id

                            // 启动 PlayerActivity
                            startActivity(intent)
                        }

                        binding.recyclerView.layoutManager = LinearLayoutManager(this@MusicActivity)
                        binding.recyclerView.adapter = songAdapter
                        Log.d("MusicActivity", "RecyclerView 适配器已设置")
                    } else {
                        Log.d("MusicActivity", "未找到相关歌曲")
                        Toast.makeText(this@MusicActivity, "未找到相关歌曲", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MusicActivity", "搜索歌曲失败: ${e.message}")
            }
        }
    }

    // 歌曲列表初始化
    private fun loadSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val songs = musicRepository.fetchSongs() // 获取所有歌曲
                Log.d("MusicActivity", "成功获取歌曲数据: ${songs.size} 首歌")

                withContext(Dispatchers.Main) {
                    // 设置适配器并刷新 RecyclerView
                    songAdapter = SongAdapter(songs) { song ->
                        // 点击事件，传递音频 URL、封面图片 URL、歌名和歌手名进行播放
                        val intent = Intent(this@MusicActivity, PlayerActivity::class.java)
                        intent.putExtra("audio_url", song.audioUrl)
                        intent.putExtra("cover_url", song.coverUrl) // 传递封面图片 URL
                        intent.putExtra("song_title", song.mname)   // 传递歌名
                        intent.putExtra("artist_name", song.artistName) // 传递歌手名字
                        intent.putExtra("song_id", song.id)           // 传递歌曲 id
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

    // 检查登录状态
    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false) // 获取登录状态
        Log.d("是否登录", isLoggedIn.toString())
        if (!isLoggedIn) {
            Log.d("LoginStatus", "用户未登录，跳转到登录界面")
            // 跳转到登录界面
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // 结束当前页面
        } else {
            Log.d("LoginStatus", "用户已登录，继续当前操作")
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchBox.text.clear()
        loadSongs()
    }
}
