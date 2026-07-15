#!/bin/bash
export ANDROID_SDK_ROOT=$HOME/android-sdk
export ANDROID_HOME=$HOME/android-sdk
mkdir -p $ANDROID_SDK_ROOT/cmdline-tools
cd $ANDROID_SDK_ROOT/cmdline-tools
if [ ! -d "latest" ]; then
  wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
  unzip -q commandlinetools-linux-*.zip
  mv cmdline-tools latest
  rm commandlinetools-linux-*.zip
fi
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
yes | sdkmanager --licenses > /dev/null 2>&1
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" > /dev/null 2>&1
cd $GITHUB_WORKSPACE
wget -q https://services.gradle.org/distributions/gradle-8.4-bin.zip
unzip -q gradle-8.4-bin.zip
gradle-8.4/bin/gradle assembleDebug
