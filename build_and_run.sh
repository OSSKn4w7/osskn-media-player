#!/bin/bash

# Build and run script for Multimedia Player app

echo "Building Multimedia Player App..."

# Make sure we're in the right directory
cd /root/1132/multimediaplayer

# Make gradlew executable
chmod +x ./gradlew

# Clean previous builds
./gradlew clean

# Build the debug APK
./gradlew assembleDebug

echo "Build completed!"

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "APK generated successfully at app/build/outputs/apk/debug/app-debug.apk"
    echo "To install on a connected device, run:"
    echo "adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Error: APK was not generated"
fi