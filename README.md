# 🎵 基于Kotlin音乐播放器应用程序

这是一个基于 **Kotlin** 和 **Android Studio**
构建的音乐播放器应用程序。用户可以注册、登录、浏览音乐列表，并通过内置播放器播放音乐。该应用还支持基于Sherpa-ONNX
的语音关键词检测功能，提升了用户体验的智能化水平。

---

## 🚀 功能亮点

- **用户注册与登录**：提供基本的用户认证功能。
- **音乐浏览与播放**：展示音乐列表，支持播放、暂停、切换歌曲。
- **语音关键词检测**：通过语音控制操作音乐功能（如快速搜索或播放歌曲）。
- **模块化设计**：采用分层架构（MVVM），便于维护和扩展。
- **多架构支持**：支持多种设备 CPU 架构（ARM 和 x86）。

---

## 🗂 项目目录结构

```plaintext
.
├── AndroidManifest.xml            # 应用主配置文件
│
├── assets                         # 应用资源文件
│   └── sherpa-onnx-kws-zipformer-wenetspeech-3.3M-2024-01-01
│       ├── 模型文件
│       │   ├── configuration.json
│       │   ├── decoder-epoch-*.onnx
│       │   ├── encoder-epoch-*.onnx
│       │   ├── joiner-epoch-*.onnx
│       │   └── tokens.txt
│       ├── 关键词文件
│       │   ├── keywords.txt
│       │   └── keywords_raw.txt
│       └── 测试数据
│           ├── 若干音频文件 (.wav)
│           └── test_keywords.txt
│
├── java                           # 应用逻辑实现
│   └── com.xiaoyang.musicplayerapplication
│       ├── data                   # 数据层
│       │   ├── model              # 数据模型
│       │   │   ├── Song.kt
│       │   │   └── UserResponse.kt
│       │   └── repository         # 数据存取与业务逻辑
│       │       └── MusicRepository.kt
│       │
│       ├── network                # 网络通信模块
│       │   ├── ApiService.kt
│       │   └── RetrofitClient.kt
│       │
│       ├── service                # 服务与核心逻辑
│       │   ├── FeatureConfig.kt
│       │   ├── KeywordSpotter.kt
│       │   ├── KeywordSpotterService.kt  # 提供后台关键词检测服务
│       │   ├── OnlineRecognizer.kt
│       │   └── OnlineStream.kt
│       │
│       └── ui                     # 用户界面
│           ├── activity           # 页面活动类
│           │   ├── BaseActivity.kt
│           │   ├── LoginActivity.kt
│           │   ├── MusicActivity.kt
│           │   ├── PlayerActivity.kt
│           │   └── RegisterActivity.kt
│           │
│           └── adapter            # 数据适配器
│               └── SongAdapter.kt
│
├── JniLibs                        # 动态链接库
│   ├── arm64-v8a
│   ├── armeabi-v7a
│   ├── x86
│   └── x86_64
│       ├── libonnxruntime.so
│       └── libsherpa-onnx-jni.so
│
└── res                            # 资源文件
    ├── drawable                   # 图片资源
    │   ├── ic_launcher.png
    │   ├── ic_play.png
    │   └── background_gradient.xml
    ├── layout                     # 页面布局
    │   ├── activity_main.xml
    │   ├── activity_music.xml
    │   └── item_song.xml
    ├── values                     # 值资源
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── xml                        # 配置文件
        ├── backup_rules.xml
        └── network_security_config.xml
```

---

## ⚙️ 技术栈

- **语言**：Kotlin
- **架构模式**：MVVM
- **语音检测**：Sherpa-ONNX
- **网络通信**：Retrofit
- **UI 组件**：RecyclerView、Material Design
- **后台服务**：Android Service 用于语音检测

---

## 🛠️ 项目运行步骤

1. **克隆项目**：

   ```bash
   git clone https://github.com/hcyhechaoyang/MusicPlayerApplication.git
   cd MusicPlayerApplication-master
   ```

2. **打开项目**：

   - 使用最新版的 Android Studio 打开该项目。

3. **安装依赖**：

   - Gradle 同步项目，确保下载所有必要的依赖库。

4. **运行项目**：

   - 连接 Android 设备或启动模拟器。
   - 点击 Android Studio 中的“运行”按钮。

---

## 📷 应用截图

**（可在此处添加功能截图，以直观展示应用效果）**

---

## 🔗 API 接口文档

- **GET /songs**：获取歌曲列表
- **POST /register**：用户注册
- **POST /login**：用户登录

---

### 🌟 作者

- **He Chao Yang**
- **联系邮箱**：keiraeetat@outlook.com
