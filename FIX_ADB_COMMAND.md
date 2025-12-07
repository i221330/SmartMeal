# 🔧 Fix "adb: command not found" Error

## Quick Solution

The `adb` command is not in your system PATH. Here are **3 ways** to fix it:

---

## ✅ Option 1: Add ADB to PATH (Recommended)

### Step 1: Find your Android SDK location
Your Android SDK is at: `/Users/mac/Library/Android/sdk` (from your local.properties)

### Step 2: Add to your shell configuration

**For zsh (macOS default):**
```bash
echo 'export ANDROID_HOME=/Users/mac/Library/Android/sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools' >> ~/.zshrc
source ~/.zshrc
```

**For bash:**
```bash
echo 'export ANDROID_HOME=/Users/mac/Library/Android/sdk' >> ~/.bash_profile
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools' >> ~/.bash_profile
source ~/.bash_profile
```

### Step 3: Verify it works
```bash
adb --version
adb devices
```

---

## ✅ Option 2: Use Full Path (Temporary)

Instead of just `adb`, use the full path:

```bash
/Users/mac/Library/Android/sdk/platform-tools/adb devices
/Users/mac/Library/Android/sdk/platform-tools/adb reverse tcp:8080 tcp:80
```

---

## ✅ Option 3: Use Android Studio Terminal

1. Open **Android Studio**
2. Open your **SmartMeal** project
3. Click **Terminal** tab at the bottom
4. Android Studio's terminal has ADB in PATH automatically
5. Run your adb commands there:
   ```bash
   adb devices
   adb reverse tcp:8080 tcp:80
   ```

---

## 🎯 After Fixing ADB

Once ADB is working, run these commands for SmartMeal:

```bash
# Check device is connected
adb devices

# Set up port forwarding (IMPORTANT for SmartMeal backend)
adb reverse tcp:8080 tcp:80

# Verify
adb reverse --list
```

---

## 🆘 Troubleshooting

### If SDK directory doesn't exist
The Android SDK might not be installed. Install it via Android Studio:
1. Open **Android Studio**
2. Go to: **Settings/Preferences** → **Appearance & Behavior** → **System Settings** → **Android SDK**
3. Note the **Android SDK Location**
4. Make sure **Android SDK Platform-Tools** is checked in the **SDK Tools** tab
5. Click **Apply** to install

### If you get "permission denied"
Make adb executable:
```bash
chmod +x /Users/mac/Library/Android/sdk/platform-tools/adb
```

### To check if ADB is already in PATH
```bash
echo $PATH | grep -i android
```

### To verify your current shell
```bash
echo $SHELL
# Output: /bin/zsh (use .zshrc) or /bin/bash (use .bash_profile)
```

---

## 📝 Copy-Paste Solution (Choose One)

### For zsh users (macOS Catalina and later):
```bash
cat >> ~/.zshrc << 'EOF'

# Android SDK for SmartMeal
export ANDROID_HOME=/Users/mac/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
EOF

source ~/.zshrc
adb --version
```

### For bash users:
```bash
cat >> ~/.bash_profile << 'EOF'

# Android SDK for SmartMeal
export ANDROID_HOME=/Users/mac/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
EOF

source ~/.bash_profile
adb --version
```

---

## ✅ After Setup Checklist

- [ ] Run `adb --version` - should show version info
- [ ] Run `adb devices` - should list devices (connect device/emulator first)
- [ ] Run `adb reverse tcp:8080 tcp:80` - sets up port forwarding for SmartMeal
- [ ] Run `adb reverse --list` - should show `tcp:8080 tcp:80`
- [ ] Continue with SmartMeal setup from **COMPLETE_SETUP_GUIDE.md**

---

## 🔗 Next Steps

After ADB is working, continue with:
1. **COMPLETE_SETUP_GUIDE.md** - Step 5 (Setup Android Connection)
2. Test backend connection from your app
3. Test signup flow

---

## 💡 Pro Tip

You can also install ADB standalone (without full Android Studio):
```bash
brew install android-platform-tools
```

This installs just ADB via Homebrew (if you have Homebrew installed).

