#!/bin/sh

# Add basic Gradle wrapper functionality
echo "Running Gradle wrapper for osskn播放器 project"
exec java -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"