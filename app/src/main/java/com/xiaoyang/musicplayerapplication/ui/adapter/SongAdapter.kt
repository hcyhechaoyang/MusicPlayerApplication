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
                "绑定数据: 音乐名称：${song.mname} - 作者：${song.artistName}-封面：${song.coverUrl}-音乐Url：${song.audioUrl}"
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
        }
    }
}
