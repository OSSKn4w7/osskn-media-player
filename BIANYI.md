# 编译参数与构建指南

## 必需环境
- **Java**：JDK 17
- **Android SDK**：Platform 34, Build-Tools 34.0.0
- **Gradle**：8.2（使用 Wrapper 自动下载）
- **Git**：2.40+

## 签名配置
- 存放位置：`keystore.jks`，别名：`upload`
- 密码和别名密码从环境变量读取：
  ```
  STORE_PASSWORD=xxx
  KEY_PASSWORD=xxx
  KEY_ALIAS=upload
  ```

## 构建命令
### Debug 版本
```
./gradlew assembleDebug
```

### Release 版本（需签名）
```
export STORE_PASSWORD=xxx
export KEY_PASSWORD=xxx
./gradlew assembleRelease
```

## 依赖库版本（精简版）
- Media3 (ExoPlayer): 1.2.1
- Coil: 2.6.0
- Room: 2.6.1
- Retrofit: 2.9.0
- OkHttp: 4.12.0
- Compose BOM: 2024.02.00
- DataStore Preferences: 1.0.0
- Navigation Compose: 2.7.7
- KSP: 1.9.22-1.0.17

## 轻量化优化
- 开启 R8 混淆与资源压缩
- 移除未使用资源
- 目标 APK 体积：< 15MB

## 环境自检与安装脚本
执行 `./scripts/setup_env.sh` 将自动检查并安装缺失组件，最后打印验证结果。

## 项目结构
```
osskn媒体播放器/
├── app/
│   ├── src/main/
│   │   ├── java/com/osskn/mediaplayer/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── local/          # Room 数据库
│   │   │   │   ├── remote/         # 网络请求
│   │   │   │   └── repository/     # 数据仓库
│   │   │   ├── model/              # 数据模型
│   │   │   ├── service/            # 后台服务
│   │   │   ├── ui/
│   │   │   │   ├── navigation/     # 导航
│   │   │   │   ├── screens/        # 页面
│   │   │   │   └── theme/          # 主题
│   │   │   └── viewmodel/          # ViewModel
│   │   ├── res/                    # 资源文件
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── scripts/
│   └── setup_env.sh
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
