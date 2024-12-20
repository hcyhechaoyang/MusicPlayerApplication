package com.xiaoyang.musicplayerapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.xiaoyang.musicplayerapplication.data.model.UserResponse
import com.xiaoyang.musicplayerapplication.databinding.ActivityMainBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import com.xiaoyang.musicplayerapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化Retrofit API服务
        apiService = RetrofitClient.instance.create(ApiService::class.java)

        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerHint.setOnClickListener {
            // 跳转到RegisterActivity
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun loginUser(username: String, password: String) {
        apiService.loginUser(username, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                when (response.code()) {
                    200 -> {
                        // 登录成功
                        val user = response.body()
                        if (user != null && user.password == password) {
                            saveLoginInfo(user.username, user.password)
                            Toast.makeText(
                                this@LoginActivity,
                                "登录成功，欢迎用户: ${user.username}！",
                                Toast.LENGTH_SHORT
                            ).show()
                            // 跳转到MusicActivity
                            val intent = Intent(this@LoginActivity, MusicActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    // 用户名不存在
                    404 -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "用户名不存在，请先注册",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // 密码错误
                    401 -> {
                        Toast.makeText(this@LoginActivity, "密码错误，请重试", Toast.LENGTH_SHORT)
                            .show()
                    }
                    // 其他错误
                    else -> {
                        Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("网络错误", t.message.toString())
                Toast.makeText(this@LoginActivity, "请求失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun saveLoginInfo(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("isLoggedIn", true)  // 存储登录状态
        editor.apply()

        Log.d("LoginInfo", "保存登录信息：用户名 = $username，密码 = $password")
    }



}

