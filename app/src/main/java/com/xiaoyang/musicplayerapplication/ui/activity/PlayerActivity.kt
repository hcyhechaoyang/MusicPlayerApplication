package com.xiaoyang.musicplayerapplication.ui.activity

import android.annotation.SuppressLint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.xiaoyang.musicplayerapplication.R
import com.xiaoyang.musicplayerapplication.data.repository.MusicRepository
import com.xiaoyang.musicplayerapplication.databinding.ActivityPlayerBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import com.xiaoyang.musicplayerapplication.service.KeywordSpotterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

class PlayerActivity : BaseActivity(), KeywordSpotterService.OnKeywordDetectedListener {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var musicRepository: MusicRepository
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentSongId: Int = 0 // 当前播放歌曲的 ID
    private val handler = Handler(Looper.getMainLooper())
    // 关键词检测服务绑定变量
    private var keywordSpotterService: KeywordSpotterService? = null
    private var isBound = false

    /**
     * 语音后台服务
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as KeywordSpotterService.LocalBinder
            keywordSpotterService = binder.getService()
            isBound = true

            // 注册监听器
            keywordSpotterService?.setOnKeywordDetectedListener(this@PlayerActivity)

            // 服务绑定后，启动关键词检测
            keywordSpotterService?.startRecording()
            Log.d("语音服务", "关键词检测服务已启动")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            keywordSpotterService = null
            isBound = false
            Log.d("语音服务", "关键词检测服务已断开")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //进入个人中心
        binding.profileImage.setOnClickListener {
            val intent = Intent(this@PlayerActivity, UserCenterActivity::class.java)
            onDestroy()
            // 启动 PlayerActivity
            startActivity(intent)
        }
        checkLoginStatus()

        // 初始化 MusicRepository
        musicRepository = MusicRepository(ApiService.create())
        // 初始化 MediaPlayer
        mediaPlayer = MediaPlayer()

        // 获取传递的数据
        val audioUrl = intent.getStringExtra("audio_url")
        val coverUrl = intent.getStringExtra("cover_url")
        val songTitleText = intent.getStringExtra("song_title")
        val artistNameText = intent.getStringExtra("artist_name")
        currentSongId = intent.getIntExtra("song_id", -1)  // 获取歌曲的 id

        Log.d("歌曲信息", "初始歌曲ID: $currentSongId")
        Log.d(
            "歌曲信息",
            "传递的歌曲信息 - 音频URL: $audioUrl, 封面URL: $coverUrl, 歌名: $songTitleText, 歌手: $artistNameText"
        )

        // 显示歌曲信息
        binding.songTitle.text = songTitleText
        binding.artistName.text = artistNameText


        // 加载封面图片
        Glide.with(this).load(coverUrl).into(binding.songCover)

        // 播放/暂停按钮点击事件
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                audioUrl?.let { url ->
                    startMusic(url)
                }
            }
        }

        // 上一首按钮点击事件
        binding.previousButton.setOnClickListener {
            Log.d("PlayerActivity", "点击上一首按钮")
            playPreviousSong()
        }

        // 下一首按钮点击事件
        binding.nextButton.setOnClickListener {
            Log.d("PlayerActivity", "点击下一首按钮")
            playNextSong()
        }

        // 更新进度条
        mediaPlayer?.setOnPreparedListener {
            binding.seekBar.max = mediaPlayer?.duration ?: 0
            mediaPlayer?.start()
            isPlaying = true
            binding.playPauseButton.setImageResource(R.drawable.pause)
            binding.totalTime.text = formatTime(mediaPlayer!!.duration)

            binding.seekBar.max = mediaPlayer!!.duration
            updateSeekBar()
            Log.d("PlayerActivity", "音乐已开始播放")
        }

        // 播放进度条更新监听
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.currentTime.text = formatTime(progress)
                    Log.d("PlayerActivity", "用户拖动进度条到位置: $progress")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("PlayerActivity", "开始拖动进度条")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("PlayerActivity", "停止拖动进度条")
            }
        })
        // 监听播放完成
        mediaPlayer!!.setOnCompletionListener {
            handler.removeCallbacksAndMessages(null) // 停止更新
            binding.seekBar.progress = 0
            binding.currentTime.text = formatTime(0)
        }
        // 获取当前歌曲
        fetchSongById(currentSongId)
        //绑定关键词检测服务
        startKeywordSpotterService()

        /**
         * 收藏功能
         */
        // 初始化按钮图标
        val sharedPreferences = binding.root.context.getSharedPreferences("UserPrefs", 0)
        val username = sharedPreferences.getString("username", "")
        if (!username.isNullOrEmpty()) {
            Log.d("收藏", "用户名: $username 当前歌曲ID:$currentSongId")
            ApiService.create().judgelike(username, currentSongId)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful && response.body().toString() == "true") {
                            // 如果已收藏，设置为已收藏图标
                            Log.d("收藏", "歌曲已收藏，设置为已收藏图标")
                            binding.favoriteButton.setImageResource(R.drawable.collection2)
                        } else {
                            // 如果未收藏，设置为未收藏图标
                            Log.d("收藏", "歌曲未收藏，设置为未收藏图标")
                            binding.favoriteButton.setImageResource(R.drawable.collection)
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Log.e("收藏", "网络请求失败", t)
                    }
                })
        } else {
            Log.e("收藏", "用户名为空，无法判断是否已收藏")
        }

        // 收藏按钮点击事件
        binding.favoriteButton.setOnClickListener {
            Log.d("收藏", "点击了收藏按钮: $songTitleText - $artistNameText")

            // 确保用户名存在
            if (!username.isNullOrEmpty()) {
                Log.d("收藏", "用户名: $username")

                // 先判断当前按钮的状态
                val isCurrentlyFavorited = binding.favoriteButton.drawable.constantState ==
                        ContextCompat.getDrawable(
                            binding.root.context,
                            R.drawable.collection2
                        )?.constantState

                if (isCurrentlyFavorited) {
                    Log.d("收藏", "当前状态：已收藏，点击后准备取消收藏")

                    // 如果是已收藏状态，点击后取消收藏
                    ApiService.create().removeFromCollection(username, currentSongId)
                        .enqueue(object : Callback<Boolean> {
                            override fun onResponse(
                                call: Call<Boolean>,
                                response: Response<Boolean>
                            ) {
                                if (response.body().toString() == "true") {
                                    Log.d("收藏", "取消收藏成功")
                                    Toast.makeText(
                                        this@PlayerActivity,
                                        "已取消收藏",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // 取消收藏成功后更新按钮图标
                                    binding.favoriteButton.setImageResource(R.drawable.collection)  // 未收藏图标
                                } else {
                                    Log.e("收藏", "取消收藏失败")
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Log.e("收藏", "网络请求失败", t)
                            }
                        })
                } else {
                    Log.d("收藏", "当前状态：未收藏，点击后准备添加收藏")

                    // 如果是未收藏状态，点击后添加收藏
                    ApiService.create().addToCollection(username, currentSongId)
                        .enqueue(object : Callback<Boolean> {
                            override fun onResponse(
                                call: Call<Boolean>,
                                response: Response<Boolean>
                            ) {
                                if (response.body().toString() == "true") {
                                    Log.d("收藏", "收藏成功")
                                    Toast.makeText(
                                        this@PlayerActivity,
                                        "收藏成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // 收藏成功后更新按钮图标
                                    binding.favoriteButton.setImageResource(R.drawable.collection2)  // 已收藏图标
                                } else {
                                    Log.e("收藏", "收藏失败")
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Log.e("收藏", "网络请求失败", t)
                            }
                        })
                }
            } else {
                Log.e("收藏", "用户名为空，无法收藏")
            }
        }


    }


    /**
     * 唤醒功能语音检测
     */
    override fun onKeywordDetected(keyword: String) {
        Log.d("语音服务", "识别到唤醒词：$keyword")

        when (keyword) {
            "小欢暂停音乐" -> pauseMusic()
            "小欢播放音乐" -> resumeMusic()
            "小欢上一首" -> playPreviousSong()
            "小欢下一首" -> playNextSong()
            else -> Log.d("语音服务", "未定义的唤醒词：$keyword")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(progress: Int): String {
        val seconds = (progress / 1000) % 60
        val minutes = (progress / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)

    }

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

    private fun resumeMusic() {
        mediaPlayer?.start()
        isPlaying = true
        binding.playPauseButton.setImageResource(R.drawable.pause)
        Log.d("PlayerActivity", "音乐已恢复播放")
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.playPauseButton.setImageResource(R.drawable.ic_play)
        Log.d("PlayerActivity", "音乐已暂停")
    }

    private fun playPreviousSong() {
        Log.d("PlayerActivity", "切换到上一首，当前歌曲ID: $currentSongId")
        fetchSongById(currentSongId - 1)
    }

    private fun playNextSong() {
        Log.d("PlayerActivity", "切换到下一首，当前歌曲ID: $currentSongId")
        fetchSongById(currentSongId + 1)
    }

    private fun fetchSongById(songId: Int) {
        Log.d("PlayerActivity", "尝试获取歌曲ID: $songId")

        if (songId <= 0) {
            Log.d("PlayerActivity", "无效的歌曲ID: $songId")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val song = musicRepository.fetchSongById(songId)

                withContext(Dispatchers.Main) {
                    if (song != null && song.audioUrl.isNotEmpty()) {
                        binding.songTitle.text = song.mname
                        binding.artistName.text = song.artistName
                        Glide.with(this@PlayerActivity).load(song.coverUrl).into(binding.songCover)
                        Log.d(
                            "PlayerActivity",
                            "成功获取歌曲ID: ${song.id} 并开始播放: ${song.mname}"
                        )
                        startMusic(song.audioUrl)
                        currentSongId = song.id
                    } else {
                        Log.d("PlayerActivity", "未找到有效的歌曲，尝试获取下一个ID")
                        fetchSongById(songId + 1)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("PlayerActivity", "请求歌曲数据失败: ${e.message}")
                    Toast.makeText(this@PlayerActivity, "请求失败，请稍后再试", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun startMusic(audioUrl: String) {
        Log.d("PlayerActivity", "开始播放音乐 URL: $audioUrl")
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(audioUrl)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Log.e("PlayerActivity", "播放音乐时出错: ${e.message}", e)
            Toast.makeText(this, "播放音乐时出错", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSeekBar() {
        // 检查 mediaPlayer 是否为空
        val currentPosition = mediaPlayer?.currentPosition ?: 0

        // 检查 binding 是否初始化且视图仍然附着到窗口
        if (::binding.isInitialized && binding.root.isAttachedToWindow) {
            binding.seekBar.progress = currentPosition
            binding.currentTime.text = formatTime(currentPosition)
        } else {
            Log.d("PlayerActivity", "updateSeekBar：绑定未初始化或 view 已分离。")
            return // 直接退出，避免异常
        }

        // 如果音乐正在播放，延迟 1 秒后更新
        if (isPlaying && mediaPlayer != null) {
            binding.seekBar.postDelayed({ updateSeekBar() }, 1000)
        } else {
            Log.d("PlayerActivity", "updateSeekBar：mediaPlayer 为 null 或不正在播放。")
        }
    }


    //检测录音权限
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限已授予，启动关键词检测服务
            startKeywordSpotterService()
        } else {
            Toast.makeText(this, "录音权限未授予，无法启动关键词检测服务", Toast.LENGTH_SHORT).show()
        }
    }

    //绑定关键词检测服务函数
    private fun startKeywordSpotterService() {
        val serviceIntent = Intent(this, KeywordSpotterService::class.java)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        Log.d("PlayerActivity", "释放 MediaPlayer 资源")
        super.onDestroy()

        // 清除 Handler 回调
        handler.removeCallbacksAndMessages(null)

        // 释放 MediaPlayer 资源
        mediaPlayer?.release()
        mediaPlayer = null

        // 解绑关键词检测服务
        if (isBound) {
            Log.d("语言服务", "解绑关键词检测服务")
            keywordSpotterService?.setOnKeywordDetectedListener(null) // 清除监听器
            keywordSpotterService?.stopRecording()
            try {
                unbindService(serviceConnection)
                isBound = false
                Log.d("语言服务", "关键词检测服务解绑完成")
            } catch (e: IllegalArgumentException) {
                Log.e("语言服务", "服务未绑定或解绑时出错: ${e.message}")
            }
        }
    }

}
