package com.xiaoyang.musicplayerapplication.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xiaoyang.musicplayerapplication.R
import com.xiaoyang.musicplayerapplication.databinding.ItemSongBinding
import com.xiaoyang.musicplayerapplication.data.model.Song

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
            "com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter",
            "绑定数据: ${song.m_name} - ${song.artist_name}"
        )
        holder.bind(song)
    }

    // 返回列表的大小
    override fun getItemCount(): Int = songList.size

    // ViewHolder 内部类
    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // 绑定数据
        fun bind(song: Song) {
            Log.d(
                "com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter",
                "绑定数据: ${song.m_name} - ${song.artist_name}-${song.cover_url}-${song.audio_url}"
            )

            // 设置歌曲名称和艺术家
            binding.songTitle.text = "歌名：" + song.m_name
            binding.artistName.text = "歌手：" + song.artist_name

            // 使用 Glide 加载图片，并设置默认图片和错误图片
            Glide.with(binding.coverImage.context)
                .load(song.cover_url)  // 使用网络 URL 加载图片
                .error(R.drawable.default_image)  // 加载失败时显示默认图片
                .fallback(R.drawable.default_image)  // URL 为 null 时显示默认图片
                .into(binding.coverImage)

            // 点击事件
            binding.root.setOnClickListener {
                Log.d(
                    "com.xiaoyang.musicplayerapplication.ui.adapter.SongAdapter",
                    "点击了歌曲: ${song.m_name} - ${song.artist_name}"
                )
                onClick(song)
            }
        }
    }
}

