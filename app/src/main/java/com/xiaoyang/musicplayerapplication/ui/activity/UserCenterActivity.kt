package com.xiaoyang.musicplayerapplication.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyang.musicplayerapplication.data.model.UserResponse
import com.xiaoyang.musicplayerapplication.data.repository.MusicRepository
import com.xiaoyang.musicplayerapplication.databinding.ActivityUserCenterBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCenterActivity : BaseActivity() {

    lateinit var binding: ActivityUserCenterBinding
    private lateinit var songAdapter: SongAdapter
    private lateinit var apiService: ApiService
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取保存的用户名
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        username = sharedPreferences.getString("username", null)

        // 如果用户名为空，显示未登录
        if (username.isNullOrEmpty()) {
            binding.userNameTextView.text = "未登录"
        } else {
            binding.userNameTextView.text = username
        }

        Log.d("UserCenter", "当前用户名：$username")

        // 修改密码按钮点击事件
        binding.changePasswordButton.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // 退出登录按钮点击事件
        binding.logoutButton.setOnClickListener {
            logout()
        }

        // 初始化 ApiService 实例
        apiService = ApiService.create()
        // 获取收藏的歌曲列表
        fetchSongs()
    }

    private fun fetchSongs() {
        // 检查用户名是否为空
        if (username.isNullOrEmpty()) {
            Log.e("UserCenterActivity", "用户名为空，无法获取歌曲数据")
            return
        }

        Log.d("UserCenterActivity", "开始请求歌曲数据")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val songs = apiService.listlike(username!!)  // 使用 API 获取收藏列表
                Log.d("user的名：", username.toString())
                Log.d("UserCenterActivity", "成功获取歌曲数据: ${songs.size} 首歌")
                withContext(Dispatchers.Main) {
                    songAdapter = SongAdapter(songs) { song ->
                        // 点击事件，传递音频 URL 等数据
                        val intent = Intent(this@UserCenterActivity, PlayerActivity::class.java)
                        intent.putExtra("audio_url", song.audioUrl)
                        intent.putExtra("cover_url", song.coverUrl)
                        intent.putExtra("song_title", song.mname)
                        intent.putExtra("artist_name", song.artistName)
                        intent.putExtra("song_id", song.id)
                        startActivity(intent)
                    }
                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(this@UserCenterActivity)
                    binding.recyclerView.adapter = songAdapter
                }
            } catch (e: Exception) {
                Log.e("UserCenterActivity", "获取歌曲数据失败: ${e.message}")
            }
        }

    }

    // 退出登录函数
    private fun logout() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // 清除所有存储的数据
        editor.apply()
        Log.d("LoginInfo", "用户已退出登录，清除登录信息")
        // 跳转到登录界面，并清除栈中所有活动
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()  // 结束当前页面
    }

}
