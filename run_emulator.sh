#!/bin/bash

# Android SDK path
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

# List available AVDs
echo "Available Android Virtual Devices:"
$ANDROID_HOME/emulator/emulator -list-avds

echo ""
echo "To start an emulator, run:"
echo "$ANDROID_HOME/emulator/emulator -avd <AVD_NAME>"
echo ""
echo "Or use Android Studio:"
echo "1. Open Android Studio"
echo "2. Tools > Device Manager"
echo "3. Click Play button next to your AVD"

