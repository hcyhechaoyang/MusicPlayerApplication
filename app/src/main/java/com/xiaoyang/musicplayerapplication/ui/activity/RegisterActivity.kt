package com.xiaoyang.musicplayerapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xiaoyang.musicplayerapplication.databinding.ActivityRegisterBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import com.xiaoyang.musicplayerapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化Retrofit API服务
        apiService = RetrofitClient.instance.create(ApiService::class.java)

        // 设置注册按钮点击事件
        binding.registerButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            // 检查输入是否合法
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
            } else {
                // 发送注册请求
                registerUser(username, password)
            }
        }
        binding.loginHint.setOnClickListener {
            // 跳转到LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, password: String) {
        apiService.registerUser(username, password).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.body().toString() == "true") {
                    Toast.makeText(
                        this@RegisterActivity,
                        "注册成功！请登录---{${response.body()}}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // 跳转到LoginActivity
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "用户名重复，请更换用户名！",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("网络错误", t.message.toString())
                Toast.makeText(this@RegisterActivity, "请求失败，请检查网络连接", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}