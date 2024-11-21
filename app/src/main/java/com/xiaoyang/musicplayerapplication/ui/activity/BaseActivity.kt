package com.xiaoyang.musicplayerapplication.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

abstract class BaseActivity : AppCompatActivity(), SensorEventListener {
    lateinit var sm: SensorManager
    lateinit var senor: Sensor
    private var lastBrightness: Float = -1f  // 上一次的亮度值
    private var lastTime: Long = 0  // 上一次更新时间

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化传感器
        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        senor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        // 检查并请求录音权限
        checkAndRequestPermissions()
    }

    override fun onStart() {
        super.onStart()
        // 注册传感器监听
        sm.registerListener(this, senor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onStop() {
        super.onStop()
        // 注销传感器监听
        sm.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        // 获取光传感器的值
        val brightness = event.values[0].toInt()
        Log.d("亮度", brightness.toString())

        // 判断亮度变化是否需要更新
        if (System.currentTimeMillis() - lastTime > 5) {  // 每100ms更新一次
            val newBrightness = calculateBrightness(brightness)
            if (newBrightness != lastBrightness) {
                changeAppBrightness(this, newBrightness)
                lastBrightness = newBrightness
                lastTime = System.currentTimeMillis()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // 计算适合的亮度值
    private fun calculateBrightness(sensorValue: Int): Float {
        // 对光度值进行处理，确保不超过0-1的范围
        val brightness = sensorValue.toFloat() / 255f
        return max(0.1f, min(0.5f, brightness))  // 确保亮度不会太低或太高
    }

    // 修改应用亮度
    private fun changeAppBrightness(context: Context, brightness: Float) {
        val window = (context as Activity).window
        val lp = window.attributes
        lp.screenBrightness = brightness
        window.attributes = lp
    }

    // 检查并请求录音权限
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果未授予权限，请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
        } else {
            Log.i("BaseActivity", "录音权限已授予")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("BaseActivity", "录音权限已授予")
            } else {
                Toast.makeText(this, "录音权限未授予，部分功能可能无法使用", Toast.LENGTH_SHORT)
                    .show()
                // 提供跳转到设置的选项
                showPermissionSettings()
            }
        }
    }

    // 提供跳转到权限设置页面的选项
    private fun showPermissionSettings() {
        Toast.makeText(this, "请前往设置手动开启录音权限", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }
}
