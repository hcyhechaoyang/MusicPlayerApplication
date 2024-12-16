package com.xiaoyang.musicplayerapplication.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.xiaoyang.musicplayerapplication.R
import com.xiaoyang.musicplayerapplication.databinding.ActivityUserCenterBinding

class UserCenterActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserCenterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}