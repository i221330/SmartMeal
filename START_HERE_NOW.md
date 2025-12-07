# 🎯 SmartMeal - Quick Start

## Your Current Issue: ✅ SOLVED

**Problem:** `adb: command not found`  
**Solution:** See **[FIX_ADB_COMMAND.md](FIX_ADB_COMMAND.md)** for 3 easy ways to fix this.

**Quickest Fix (copy-paste into terminal):**
```bash
echo 'export ANDROID_HOME=/Users/mac/Library/Android/sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools' >> ~/.zshrc
source ~/.zshrc
adb --version
```

---

## 📚 Documentation Files

### 1. **[FIX_ADB_COMMAND.md](FIX_ADB_COMMAND.md)** ← **START HERE**
   - Fixes "adb: command not found" error
   - 3 different solutions (choose what works for you)
   - Copy-paste commands ready

### 2. **[COMPLETE_SETUP_GUIDE.md](COMPLETE_SETUP_GUIDE.md)** ← **THEN GO HERE**
   - Complete step-by-step setup
   - XAMPP/MySQL database setup
   - Backend PHP API setup
   - Android app testing
   - Flow: Splash → Login → Signup → Onboarding → Home

### 3. **[PHP_BACKEND_SETUP.md](PHP_BACKEND_SETUP.md)**
   - Detailed PHP backend architecture
   - Database schema explained

### 4. **[START_HERE_PHP.md](START_HERE_PHP.md)**
   - Quick PHP backend setup

---

## 🚀 Quick Setup (30 mins)

### 1. Fix ADB (5 mins)
```bash
# Add to your PATH
echo 'export ANDROID_HOME=/Users/mac/Library/Android/sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools' >> ~/.zshrc
source ~/.zshrc

# Verify
adb --version
```

### 2. Setup XAMPP & Database (10 mins)
- Open XAMPP → Start Apache & MySQL
- Open http://localhost/phpmyadmin
- Create database: `smartmeal_db`
- Import: `backend/database/schema.sql`

### 3. Copy Backend Files (2 mins)
```bash
# Find your htdocs (usually /Applications/XAMPP/htdocs/)
sudo mkdir -p /Applications/XAMPP/htdocs/smartmeal/backend
sudo cp -r backend/* /Applications/XAMPP/htdocs/smartmeal/backend/
```

### 4. Test Backend (2 mins)
Open browser: http://localhost/smartmeal/backend/api/users.php

### 5. Setup Android Connection (3 mins)
```bash
# Start emulator or connect device
adb devices

# Setup port forwarding
adb reverse tcp:8080 tcp:80
adb reverse --list
```

### 6. Build & Run App (8 mins)
```bash
./gradlew clean assembleDebug installDebug
```

---

## ✅ Test the App Flow

1. **Splash Screen** (2 sec) → Auto-navigates to Login
2. **Signup**: Create account → Goes to Onboarding 1
3. **Onboarding**: Navigate through 3 screens → Goes to Home
4. **Login**: Login with created account → Goes directly to Home

---

## 🆘 Troubleshooting

| Issue | Solution |
|-------|----------|
| `adb: command not found` | See **FIX_ADB_COMMAND.md** |
| "Connection timeout" | Check Apache running, test backend URL |
| "Cannot connect to server" | Run `adb reverse tcp:8080 tcp:80` |
| Database errors | Verify `password_hash` column exists in users table |
| Signup hanging | Check logcat: `adb logcat -s PhpAuthRepository:D` |

---

## 📊 Project Status

**Currently Working:**
- ✅ Database schema with password_hash column
- ✅ PHP backend APIs (users.php, recipes.php, etc.)
- ✅ Android app structure
- ✅ Login/Signup activities
- ✅ Onboarding flow (1 → 2 → 3)
- ✅ Home screen

**Testing Now:**
- 🔄 Splash → Login → Signup → Onboarding → Home flow

---

## 🔧 Helper Scripts

- `fix_adb.sh` - Automatically configure ADB in PATH
- `test_backend.sh` - Test backend connectivity
- `test_connectivity.sh` - Test network setup

---

## 📞 Your Next Step

**Run this command and tell me the result:**
```bash
echo 'export ANDROID_HOME=/Users/mac/Library/Android/sdk' >> ~/.zshrc
echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools' >> ~/.zshrc
source ~/.zshrc
adb --version
```

If that works, proceed to **COMPLETE_SETUP_GUIDE.md** Step 2 (Setup Database).

