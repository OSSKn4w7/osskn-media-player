#!/bin/bash

# OSSKN 媒体播放器 - 环境自检与安装脚本
# 此脚本将检查并安装构建项目所需的依赖

set -e

echo "========================================="
echo "  OSSKN 媒体播放器 - 环境配置"
echo "========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查函数
check_command() {
    if command -v "$1" &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1 已安装: $($1 --version 2>&1 | head -n 1)"
        return 0
    else
        echo -e "${RED}✗${NC} $1 未安装"
        return 1
    fi
}

# 检查 Java JDK 17
echo ""
echo "检查 Java JDK..."
if check_command java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | grep -oP 'version "\K[0-9]+')
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${YELLOW}警告: Java 版本需要 17 或更高${NC}"
        echo "请安装 JDK 17: https://adoptium.net/"
    fi
else
    echo "正在安装 JDK 17..."
    if [ -x "$(command -v apt-get)" ]; then
        sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
    elif [ -x "$(command -v brew)" ]; then
        brew install openjdk@17
    else
        echo "请手动安装 JDK 17"
        exit 1
    fi
fi

# 检查 Git
echo ""
echo "检查 Git..."
if ! check_command git; then
    echo "正在安装 Git..."
    if [ -x "$(command -v apt-get)" ]; then
        sudo apt-get update && sudo apt-get install -y git
    elif [ -x "$(command -v brew)" ]; then
        brew install git
    else
        echo "请手动安装 Git"
        exit 1
    fi
fi

# 检查 Android SDK
echo ""
echo "检查 Android SDK..."
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo -e "${YELLOW}未检测到 ANDROID_HOME 环境变量${NC}"
    echo "请设置 Android SDK 路径:"
    echo "  export ANDROID_HOME=\$HOME/Android/Sdk"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
else
    echo -e "${GREEN}✓${NC} Android SDK 已配置"
fi

# 检查 Gradle Wrapper
echo ""
echo "检查 Gradle Wrapper..."
if [ -f "./gradlew" ]; then
    echo -e "${GREEN}✓${NC} Gradle Wrapper 存在"
    ./gradlew --version
else
    echo -e "${YELLOW}Gradle Wrapper 不存在，正在生成...${NC}"
    if command -v gradle &> /dev/null; then
        gradle wrapper
    else
        echo "请先安装 Gradle 或使用 Android Studio 打开项目"
    fi
fi

# 最终验证
echo ""
echo "========================================="
echo "  环境验证结果"
echo "========================================="
echo ""
java -version 2>&1 | head -n 1
git --version
echo ""

if [ -n "$ANDROID_HOME" ]; then
    echo "ANDROID_HOME: $ANDROID_HOME"
else
    echo -e "${YELLOW}请设置 ANDROID_HOME 环境变量${NC}"
fi

echo ""
echo "========================================="
echo "  配置完成!"
echo "========================================="
echo ""
echo "现在可以运行以下命令构建项目:"
echo "  ./gradlew assembleDebug"
echo ""
