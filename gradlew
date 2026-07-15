#!/bin/bash
cd $GITHUB_WORKSPACE
wget -q https://services.gradle.org/distributions/gradle-8.4-bin.zip
unzip -q gradle-8.4-bin.zip
mkdir -p $HOME/android-sdk/cmdline-tools
cd $HOME/android-sdk/cmdline-tools
wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip -q commandlinetools-linux-*.zip
mv cmdline-tools latest
export ANDROID_SDK_ROOT=$HOME/android-sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
cd $GITHUB_WORKSPACE
gradle-8.4/bin/gradle assembleDebug
