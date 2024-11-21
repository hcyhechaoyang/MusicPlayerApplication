package com.xiaoyang.musicplayerapplication.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.k2fsa.sherpa.onnx.KeywordSpotter
import com.k2fsa.sherpa.onnx.KeywordSpotterConfig
import com.k2fsa.sherpa.onnx.OnlineStream
import com.k2fsa.sherpa.onnx.getFeatureConfig
import com.k2fsa.sherpa.onnx.getKeywordsFile
import com.k2fsa.sherpa.onnx.getKwsModelConfig
import kotlin.concurrent.thread

private const val TAG = "KeywordSpotterService"

class KeywordSpotterService : Service() {

    // 关键词检测相关变量
    private lateinit var kws: KeywordSpotter
    private lateinit var stream: OnlineStream
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null

    @Volatile
    private var isRecording = false

    // 定义回调接口
    interface OnKeywordDetectedListener {
        fun onKeywordDetected(keyword: String)
    }

    private var keywordListener: OnKeywordDetectedListener? = null

    // 提供设置监听器的方法
    fun setOnKeywordDetectedListener(listener: OnKeywordDetectedListener?) {
        keywordListener = listener
    }

    // 自定义 Binder，用于与 PlayerActivity 交互
    inner class LocalBinder : Binder() {
        fun getService(): KeywordSpotterService = this@KeywordSpotterService
    }

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "服务创建中，初始化关键词检测模型")
        initModel()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "服务绑定")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "服务解绑")
        stopRecording()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        Log.i(TAG, "服务已销毁")
    }

    /**
     * 初始化关键词检测模型
     */
    private fun initModel() {
        val config = KeywordSpotterConfig(
            featConfig = getFeatureConfig(sampleRate = 16000, featureDim = 80),
            modelConfig = getKwsModelConfig(type = 0)!!,
            keywordsFile = getKeywordsFile(type = 0)
        )
        kws = KeywordSpotter(application.assets, config)
        stream = kws.createStream()
        Log.i(TAG, "关键词检测模型初始化完成")
    }

    /**
     * 开始录音并处理音频数据
     */
    fun startRecording() {
        if (isRecording) {
            Log.i(TAG, "录音已在进行中")
            return
        }

        val ret = initMicrophone()
        if (!ret) {
            Log.e(TAG, "麦克风初始化失败")
            return
        }

        isRecording = true
        audioRecord!!.startRecording()
        recordingThread = thread {
            processSamples()
        }
        Log.i(TAG, "录音已启动")
    }

    /**
     * 停止录音
     */
    fun stopRecording() {
        if (!isRecording) return

        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        stream.release()
        recordingThread?.interrupt()
        Log.i(TAG, "录音已停止")
    }

    /**
     * 初始化麦克风
     */
    private fun initMicrophone(): Boolean {
        val bufferSize = AudioRecord.getMinBufferSize(
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "没有录音权限")
            return false
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            16000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        return audioRecord?.state == AudioRecord.STATE_INITIALIZED
    }

    /**
     * 处理音频样本并检测关键词
     */
    private fun processSamples() {
        val interval = 0.1 // 每次读取 100 毫秒音频
        val bufferSize = (interval * 16000).toInt()
        val buffer = ShortArray(bufferSize)

        while (isRecording) {
            val ret = audioRecord?.read(buffer, 0, buffer.size)
            if (ret != null && ret > 0) {
                val samples = FloatArray(ret) { buffer[it] / 32768.0f }
                stream.acceptWaveform(samples, sampleRate = 16000)
                while (kws.isReady(stream)) {
                    kws.decode(stream)
                }

                val keyword = kws.getResult(stream).keyword
                if (keyword.isNotBlank()) {
                    handleKeyword(keyword)
                }
            }
        }
    }

    /**
     * 处理检测到的关键词
     */
    private fun handleKeyword(keyword: String) {
        Log.i(TAG, "检测到关键词：$keyword")
        // 通过回调通知监听器
        keywordListener?.onKeywordDetected(keyword)
    }
}
