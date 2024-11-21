# 🎵 音乐播放器应用程序

这是一个使用 **Kotlin** 和 **Android Studio** 构建的简单音乐播放器应用程序。用户可以注册、登录、浏览音乐列表，并通过播放器播放音乐。

------

## 🚀 功能

- 用户认证：支持用户注册和登录。
- 浏览音乐：以交互式界面展示可用歌曲。
- 音乐播放器：支持播放、暂停、切换歌曲。
- 基于 **MVVM** 架构，代码清晰、易维护。

------

## 🗂 项目目录结构

```plaintext
.
├── data
│   ├── model
│   │   ├── Song.kt          # 音乐数据模型
│   │   ├── UserResponse.kt  # 用户相关 API 响应模型
│   │
│   └── repository
│       └── MusicRepository.kt  # 处理数据获取和业务逻辑
│
├── network
│   ├── ApiService.kt       # 定义 API 接口
│   ├── RetrofitClient.kt   # 配置并提供 Retrofit 客户端
│
└── ui
    ├── activity
    │   ├── LoginActivity.kt    # 用户登录界面
    │   ├── RegisterActivity.kt # 用户注册界面
    │   ├── MusicActivity.kt    # 展示音乐列表
    │   ├── PlayerActivity.kt   # 音乐播放器界面
    │
    └── adapter
        └── SongAdapter.kt      # RecyclerView 的适配器，负责展示歌曲列表
```

------

## ⚙️ 技术栈

- **语言**：Kotlin
- **框架**：Android SDK
- **网络通信**：Retrofit
- **架构模式**：MVVM
- **UI 组件**：RecyclerView、Material Design

------

## 🛠️ 项目运行步骤

1. **克隆项目**：

   ```bash
   git 
   cd 
   ```

2. **打开项目**：

    - 使用最新版的 Android Studio 打开该项目。

3. **安装依赖**：

    - 使用 Gradle 同步项目，下载所有依赖库。

4. **运行项目**：

    - 连接一台 Android 设备或启动模拟器。
    - 点击 Android Studio 中的“运行”按钮即可。

------

## 📷 应用截图

------

## 🔗 API 接口文档

- **GET /songs** - 获取歌曲列表
- **POST /register** - 用户注册
- **POST /login** - 用户登录

------

### 🌟 作者

- **hechaoyang**

