package com.xiaoyang.musicplayerapplication.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xiaoyang.musicplayerapplication.R
import com.xiaoyang.musicplayerapplication.databinding.ItemSongBinding
import com.xiaoyang.musicplayerapplication.data.model.Song
import com.xiaoyang.musicplayerapplication.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongAdapter(
    private val songList: List<Song>,    // 音乐列表
    private val onClick: (Song) -> Unit  // 点击事件的回调
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    // 创建 ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    // 绑定数据到视图
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        Log.d(
            "SongAdapter",
            "绑定数据: ${song.mname} - ${song.artistName}"
        )
        holder.bind(song)
    }

    // 返回列表的大小
    override fun getItemCount(): Int = songList.size

    // ViewHolder 内部类
    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 绑定数据
        @SuppressLint("SetTextI18n")
        fun bind(song: Song) {
            Log.d(
                "SongAdapter",
                "绑定数据: ${song.mname} - ${song.artistName}-${song.coverUrl}-${song.audioUrl}"
            )

            // 设置歌曲名称和艺术家
            binding.songTitle.text = "歌名：" + song.mname
            binding.artistName.text = "歌手：" + song.artistName

            // 使用 Glide 加载图片，并设置默认图片和错误图片
            Glide.with(binding.coverImage.context)
                .load(song.coverUrl)  // 使用网络 URL 加载图片
                .error(R.drawable.default_image)  // 加载失败时显示默认图片
                .fallback(R.drawable.default_image)  // URL 为 null 时显示默认图片
                .into(binding.coverImage)

            // 点击事件
            binding.root.setOnClickListener {
                Log.d(
                    "SongAdapter",
                    "点击了歌曲: ${song.mname} - ${song.artistName}"
                )
                onClick(song)
            }

            // 收藏按钮点击事件
            binding.favoriteButton.setOnClickListener {
                Log.d(
                    "SongAdapter",
                    "点击了收藏按钮: ${song.mname} - ${song.artistName}"
                )

                // 模拟获取当前用户名（你可以从登录信息中获取）
                val sharedPreferences = binding.root.context.getSharedPreferences("UserPrefs", 0)
                val username = sharedPreferences.getString("username", "")

                // 确保用户名存在
                if (username != null && username.isNotEmpty()) {
                    // 调用后端 API 来收藏歌曲
                    ApiService.create().addToCollection(username, song.id)
                        .enqueue(object : Callback<Boolean> {
                            override fun onResponse(
                                call: Call<Boolean>,
                                response: Response<Boolean>
                            ) {
                                if (response.isSuccessful && response.body() == true) {
                                    // 收藏成功后更新按钮图标
                                    binding.favoriteButton.setImageResource(R.drawable.collection2)  // 已收藏图标
                                } else {
                                    Log.e("SongAdapter", "收藏失败")
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Log.e("SongAdapter", "网络请求失败", t)
                            }
                        })
                } else {
                    Log.e("SongAdapter", "用户名为空，无法收藏")
                }
            }
        }
    }
}
