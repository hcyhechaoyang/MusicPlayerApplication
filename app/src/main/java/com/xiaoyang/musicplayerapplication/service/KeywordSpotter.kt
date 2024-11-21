// Copyright (c)  2024  Xiaomi Corporation
package com.k2fsa.sherpa.onnx

import android.content.res.AssetManager

// 关键词识别器的配置
data class KeywordSpotterConfig(
    var featConfig: FeatureConfig = FeatureConfig(),
    var modelConfig: OnlineModelConfig,
    var maxActivePaths: Int = 4, // 最大激活路径数
    var keywordsFile: String = "keywords.txt", // 关键词文件
    var keywordsScore: Float = 1.5f, // 关键词分数
    var keywordsThreshold: Float = 0.25f, // 关键词阈值
    var numTrailingBlanks: Int = 2, // 尾部空白数量
)

// 关键词识别结果
data class KeywordSpotterResult(
    val keyword: String, // 检测到的关键词
    val tokens: Array<String>, // 对应的令牌
    val timestamps: FloatArray, // 时间戳
    // TODO(fangjun): 添加更多字段
)

// 关键词识别器
class KeywordSpotter(
    assetManager: AssetManager? = null,
    val config: KeywordSpotterConfig,
) {
    private var ptr: Long // 原生指针

    init {
        ptr = if (assetManager != null) {
            newFromAsset(assetManager, config) // 从资源中加载
        } else {
            newFromFile(config) // 从文件中加载
        }
    }

    // 析构函数，释放原生资源
    protected fun finalize() {
        if (ptr != 0L) {
            delete(ptr)
            ptr = 0
        }
    }

    fun release() = finalize() // 释放资源

    // 创建解码流
    fun createStream(keywords: String = ""): OnlineStream {
        val p = createStream(ptr, keywords)
        return OnlineStream(p)
    }

    // 对流进行解码
    fun decode(stream: OnlineStream) = decode(ptr, stream.ptr)

    // 检查流是否准备就绪
    fun isReady(stream: OnlineStream) = isReady(ptr, stream.ptr)

    // 获取解码结果
    fun getResult(stream: OnlineStream): KeywordSpotterResult {
        val objArray = getResult(ptr, stream.ptr)

        val keyword = objArray[0] as String
        val tokens = objArray[1] as Array<String>
        val timestamps = objArray[2] as FloatArray

        return KeywordSpotterResult(keyword = keyword, tokens = tokens, timestamps = timestamps)
    }

    // JNI接口
    private external fun delete(ptr: Long)

    private external fun newFromAsset(
        assetManager: AssetManager,
        config: KeywordSpotterConfig,
    ): Long

    private external fun newFromFile(
        config: KeywordSpotterConfig,
    ): Long

    private external fun createStream(ptr: Long, keywords: String): Long
    private external fun isReady(ptr: Long, streamPtr: Long): Boolean
    private external fun decode(ptr: Long, streamPtr: Long)
    private external fun getResult(ptr: Long, streamPtr: Long): Array<Any>

    companion object {
        init {
            System.loadLibrary("sherpa-onnx-jni") // 加载JNI库
        }
    }
}

/*
请参见
https://k2-fsa.github.io/sherpa/onnx/kws/pretrained_models/index.html
获取预训练模型的列表。

这里只列出了一部分，请根据需要修改以下代码
以添加自己的模型。（添加新模型非常简单，只需参考以下代码）

@param type
0 - sherpa-onnx-kws-zipformer-wenetspeech-3.3M-2024-01-01（中文）
    https://www.modelscope.cn/models/pkufool/sherpa-onnx-kws-zipformer-wenetspeech-3.3M-2024-01-01/summary

1 - sherpa-onnx-kws-zipformer-gigaspeech-3.3M-2024-01-01（英文）
    https://www.modelscope.cn/models/pkufool/sherpa-onnx-kws-zipformer-gigaspeech-3.3M-2024-01-01/summary

 */
fun getKwsModelConfig(type: Int): OnlineModelConfig? {
    when (type) {
        0 -> {
            val modelDir = "sherpa-onnx-kws-zipformer-wenetspeech-3.3M-2024-01-01"
            return OnlineModelConfig(
                transducer = OnlineTransducerModelConfig(
                    encoder = "$modelDir/encoder-epoch-12-avg-2-chunk-16-left-64.onnx",
                    decoder = "$modelDir/decoder-epoch-12-avg-2-chunk-16-left-64.onnx",
                    joiner = "$modelDir/joiner-epoch-12-avg-2-chunk-16-left-64.onnx",
                ),
                tokens = "$modelDir/tokens.txt",
                modelType = "zipformer2", // 模型类型
            )
        }

        1 -> {
            val modelDir = "sherpa-onnx-kws-zipformer-gigaspeech-3.3M-2024-01-01"
            return OnlineModelConfig(
                transducer = OnlineTransducerModelConfig(
                    encoder = "$modelDir/encoder-epoch-12-avg-2-chunk-16-left-64.onnx",
                    decoder = "$modelDir/decoder-epoch-12-avg-2-chunk-16-left-64.onnx",
                    joiner = "$modelDir/joiner-epoch-12-avg-2-chunk-16-left-64.onnx",
                ),
                tokens = "$modelDir/tokens.txt",
                modelType = "zipformer2", // 模型类型
            )
        }
    }
    return null
}

/*
 * 获取每个模型的默认关键词。
 * 注意：类型和模型目录应与getModelConfig函数中的保持一致。
 */
fun getKeywordsFile(type: Int): String {
    when (type) {
        0 -> {
            val modelDir = "sherpa-onnx-kws-zipformer-wenetspeech-3.3M-2024-01-01"
            return "$modelDir/keywords.txt"
        }

        1 -> {
            val modelDir = "sherpa-onnx-kws-zipformer-gigaspeech-3.3M-2024-01-01"
            return "$modelDir/keywords.txt"
        }
    }
    return ""
}
