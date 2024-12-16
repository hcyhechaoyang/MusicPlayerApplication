package com.xiaoyang.musicplayerapplication.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.xiaoyang.musicplayerapplication.data.model.UserResponse
import com.xiaoyang.musicplayerapplication.databinding.ActivityUserCenterBinding

class UserCenterActivity : BaseActivity() {
    lateinit var binding: ActivityUserCenterBinding
    lateinit var user: UserResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //获取保存的用户名
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "未登录")

        // 显示用户名
        binding.userNameTextView.text = username

        Log.d("UserCenter", "当前用户名：$username")

        // 修改密码按钮点击事件
        binding.changePasswordButton.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

    }

    /**
     * 退出登录函数
     */
    fun logout() {
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