package com.xiaoyang.musicplayerapplication.ui.activity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.xiaoyang.musicplayerapplication.databinding.ActivityChangePasswordBinding
import com.xiaoyang.musicplayerapplication.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseActivity() {
    lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取保存的用户名
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""

        binding.changePasswordButton.setOnClickListener {
            val newPassword = binding.newPasswordEditText.text.toString()  // 获取新密码
            val confirmPassword = binding.confirmPasswordEditText.text.toString()  // 获取确认密码

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 使用 Retrofit 发送请求修改密码
            Log.d("name", "用户名：" + username + "密码：" + newPassword)
            changePassword(username, newPassword)
        }
    }

    private fun changePassword(username: String, newPassword: String) {
        val intent = Intent(this, LoginActivity::class.java)
        // 使用 ApiService 实例创建 API 服务
        val apiService = ApiService.create()

        // 调用接口方法
        val call = apiService.changePassword(username, newPassword)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {

                if (response.isSuccessful) {
                    val isSuccess = response.body() ?: false
                    if (isSuccess) {

                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "密码修改成功",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "密码修改失败",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "请求失败", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "网络错误: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
