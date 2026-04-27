# osskn媒体播放器 (OSSKN Media Player)

一款极致轻量的安卓媒体播放与文件管理应用，支持 Android 13-16。遵循 Material You 设计语言，采用极简 UI 风格。

## 签名信息（公开）

本项目使用公开签名配置，方便社区协作和二次分发：

| 项目 | 值 |
|------|-----|
| **Keystore 文件** | `osskn4w7.jks` |
| **别名 (Alias)** | `osskn4w7` |
| **密钥库密码 (Store Password)** | `osskn4w7` |
| **密钥密码 (Key Password)** | `osskn4w7` |
| **算法** | RSA 2048 |
| **有效期** | 10000 天 |

## 快速开始

### 编译 Debug APK
```bash
./gradlew assembleDebug
```

### 编译签名 Release APK
```bash
export STORE_PASSWORD=osskn4w7
export KEY_PASSWORD=osskn4w7
export KEY_ALIAS=osskn4w7
export KEYSTORE_FILE=../osskn4w7.jks
./gradlew assembleRelease
```

## 功能特性

- 🎵 **音乐播放** - 极简界面，后台播放，锁屏控制
- 🎬 **视频播放** - 全屏沉浸，长按2倍速，手势控制
- 🖼️ **图片浏览** - 全屏查看，双指缩放，GIF/WebP 支持
- 📁 **文件管理** - 分类视图，收藏功能，自建文件夹
- ☁️ **GitHub 备份** - 云端同步收藏和媒体文件
- 🔒 **无广告无追踪** - 完全离线，数据本地存储

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **播放**: Media3 (ExoPlayer)
- **图片**: Coil
- **数据库**: Room
- **网络**: Retrofit + OkHttp

## 构建状态

[![Build APK](https://github.com/OSSKn4w7/osskn-media-player/actions/workflows/build.yml/badge.svg)](https://github.com/OSSKn4w7/osskn-media-player/actions/workflows/build.yml)

## 许可证

MIT License
