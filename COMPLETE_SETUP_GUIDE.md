# 🎯 SmartMeal Complete Setup Guide
## For Signup → Onboarding → Home Flow

---

## ✅ STEP 1: Verify XAMPP Installation & Start Services

### 1.1 Check if XAMPP is Running
Open Terminal and run:
```bash
# Check if Apache is running
ps aux | grep httpd | grep -v grep

# Check if MySQL is running
ps aux | grep mysql | grep -v grep
```

**Expected Output:** You should see processes for both `httpd` (Apache) and `mysqld` (MySQL)

### 1.2 If XAMPP is NOT Running
- Open **XAMPP Control Panel** from Applications
- Click **Start** for Apache (should turn green)
- Click **Start** for MySQL (should turn green)

### 1.3 Find Your XAMPP htdocs Directory
```bash
# Common locations on macOS:
# /Applications/XAMPP/htdocs/
# /Applications/XAMPP/xamppfiles/htdocs/
# /opt/lampp/htdocs/

# Find it automatically:
find /Applications -name "htdocs" -type d 2>/dev/null
```

---

## ✅ STEP 2: Setup Database

### 2.1 Access phpMyAdmin
1. Open browser: **http://localhost/phpmyadmin** or **http://localhost:8080/phpmyadmin**
2. If it doesn't load, check Apache is running

### 2.2 Create Database
1. Click **"New"** in the left sidebar
2. Database name: **`smartmeal_db`**
3. Click **"Create"**

### 2.3 Import Schema
1. Click on **`smartmeal_db`** in the left sidebar
2. Click **"SQL"** tab at the top
3. Copy the **ENTIRE contents** of: `/Users/mac/StudioProjects/SmartMeal/backend/database/schema.sql`
4. Paste into the SQL text box
5. Click **"Go"** at the bottom
6. Should see success message: **"5 rows inserted"** (sample recipes)

### 2.4 Verify Tables
Click on `smartmeal_db` → You should see these tables:
- ✅ users (with `password_hash` column)
- ✅ pantry_items
- ✅ shopping_items
- ✅ recipes
- ✅ user_favorites
- ✅ meal_plans

### 2.5 Verify Users Table Has Password Column
1. Click on **`users`** table
2. Click **"Structure"** tab
3. Verify you see column: **`password_hash VARCHAR(255)`**

**If password_hash column is missing:**
```sql
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) AFTER email;
```

---

## ✅ STEP 3: Copy Backend Files to XAMPP

### 3.1 Find Your htdocs Directory
```bash
# Replace with your actual path from Step 1.3
HTDOCS_PATH="/Applications/XAMPP/htdocs"
```

### 3.2 Copy Backend Files
```bash
cd "$HTDOCS_PATH"
mkdir -p smartmeal/backend
cp -r /Users/mac/StudioProjects/SmartMeal/backend/* smartmeal/backend/
```

### 3.3 Verify Files Were Copied
```bash
ls -la "$HTDOCS_PATH/smartmeal/backend/api/"
# Should show: users.php, recipes.php, pantry.php, shopping.php, mealplans.php
```

---

## ✅ STEP 4: Test Backend APIs

### 4.1 Test User API Endpoint
Open browser or use curl:
```bash
curl http://localhost/smartmeal/backend/api/users.php
```

**Expected Response:**
```json
{
  "message": "SmartMeal User API",
  "status": "active",
  "endpoints": {
    "POST ?action=register": "Register new user",
    "POST ?action=login": "Login user"
  }
}
```

**If you get 404 or error:**
- Verify Apache is running
- Check the file path is correct
- Try: `http://localhost:8080/smartmeal/backend/api/users.php` (if using port 8080)

### 4.2 Test Database Connection
```bash
curl http://localhost/smartmeal/backend/config/database.php
```
Should not show any error messages

---

## ✅ STEP 5: Setup Android Emulator/Device Connection

> **⚠️ If you get "adb: command not found" error:**  
> See **FIX_ADB_COMMAND.md** for detailed solutions, or use Android Studio's built-in terminal.

> **📱 Using a Physical Phone Instead of Emulator?**  
> See **PHYSICAL_PHONE_SETUP.md** for special instructions! ADB reverse may not work on all phones.  
> You may need to use your Mac's IP address instead of localhost.

### 5.1 Start Android Emulator or Connect Device
```bash
# Check devices
adb devices
```

**Expected Output:**
```
List of devices attached
emulator-5554    device
```

### 5.2 Setup Port Forwarding (ADB Reverse)
```bash
# Forward emulator's port 8080 to your Mac's port 80 (Apache)
adb reverse tcp:8080 tcp:80

# Verify
adb reverse --list
```

**Expected Output:**
```
tcp:8080 tcp:80
```

**Note:** This allows the Android app to use `http://localhost:8080` to access your Mac's Apache server

---

## ✅ STEP 6: Build & Run Android App

### 6.1 Clean & Rebuild
```bash
cd /Users/mac/StudioProjects/SmartMeal
./gradlew clean
./gradlew assembleDebug
```

### 6.2 Install on Device/Emulator
```bash
./gradlew installDebug
```

### 6.3 Check Logcat for Connection Issues
```bash
adb logcat -s PhpAuthRepository:D PhpAuthViewModel:D ActivitySignup:D ActivityLogin:D
```

---

## ✅ STEP 7: Test the Complete Flow

### 7.1 Launch App
- App starts at **Splash Screen** (2 seconds)
- Automatically navigates to **Login Screen**

### 7.2 Test Signup Flow
1. Click **"Don't have an account? Sign Up"**
2. Fill in:
   - Name: `Test User`
   - Email: `test@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
3. Click **"Sign Up"**
4. Should navigate to **Onboarding Screen 1**

### 7.3 Test Onboarding Flow
1. **Onboarding 1:** Click **"Next"** → Goes to Onboarding 2
2. **Onboarding 2:** 
   - Click **"Back"** → Goes back to Onboarding 1
   - Click **"Next"** → Goes to Onboarding 3
3. **Onboarding 3:**
   - Click **"Back"** → Goes back to Onboarding 2
   - Click **"Got It"** or **"Finish"** → Goes to **Home Screen**

### 7.4 Test Login Flow
1. Close app and reopen
2. At **Login Screen**, enter:
   - Email: `test@example.com`
   - Password: `password123`
3. Click **"Login"**
4. Should navigate directly to **Home Screen** (skips onboarding for returning users)

---

## ✅ STEP 8: Troubleshooting

### Issue: "Connection timeout" or "Cannot connect to server"

**Solutions:**
1. Verify Apache is running: `ps aux | grep httpd`
2. Verify MySQL is running: `ps aux | grep mysql`
3. Test backend URL in browser: `http://localhost/smartmeal/backend/api/users.php`
4. Check adb reverse: `adb reverse --list` (should show `tcp:8080 tcp:80`)
5. Re-run adb reverse: `adb reverse tcp:8080 tcp:80`

### Issue: "User with this email already exists"

**Solution:**
Delete the test user from database:
```sql
DELETE FROM users WHERE email = 'test@example.com';
```

### Issue: SQL Error about `password_hash` column

**Solution:**
```sql
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) AFTER email;
```

### Issue: App crashes on signup

**Check Logcat:**
```bash
adb logcat -s AndroidRuntime:E PhpAuthRepository:E
```

Look for stack traces and error messages

### Issue: Signup hangs or doesn't respond

**Check:**
1. Network request timeout in logcat
2. Backend API is accessible
3. MySQL connection in `database.php` is correct:
   - Host: `localhost`
   - Database: `smartmeal_db`
   - User: `root`
   - Password: `` (empty)

---

## ✅ STEP 9: Verify Everything is Working

### Checklist:
- ✅ XAMPP Apache running (green)
- ✅ XAMPP MySQL running (green)
- ✅ Database `smartmeal_db` exists with all tables
- ✅ Users table has `password_hash` column
- ✅ Backend files in htdocs/smartmeal/backend/
- ✅ Backend API responds at http://localhost/smartmeal/backend/api/users.php
- ✅ adb reverse tcp:8080 tcp:80 is active
- ✅ App builds without errors
- ✅ Signup creates user in database
- ✅ Login works with created user
- ✅ Onboarding flow works (1 → 2 → 3 → Home)
- ✅ Back buttons work in onboarding
- ✅ Home screen loads successfully

---

## 📝 Quick Commands Reference

```bash
# Check XAMPP services
ps aux | grep -E "httpd|mysql" | grep -v grep

# Test backend
curl http://localhost/smartmeal/backend/api/users.php

# Setup adb reverse
adb reverse tcp:8080 tcp:80
adb reverse --list

# Build & install app
cd /Users/mac/StudioProjects/SmartMeal
./gradlew clean assembleDebug installDebug

# Watch logs
adb logcat -s PhpAuthRepository:D PhpAuthViewModel:D ActivitySignup:D ActivityLogin:D ActivityOnboarding1:D

# Clear test data
# In phpMyAdmin SQL tab:
DELETE FROM users WHERE email = 'test@example.com';
```

---

## 🎉 Success Indicators

When everything is working:

1. **Signup:**
   - Toast: "Account created successfully!"
   - Navigates to Onboarding 1
   - User appears in database: `SELECT * FROM users;`

2. **Onboarding:**
   - Can navigate: 1 → 2 → 3
   - Can go back: 3 → 2 → 1
   - "Got It" on screen 3 → Home

3. **Login:**
   - Toast: "Login successful!"
   - Navigates directly to Home (skips onboarding)

4. **Home Screen:**
   - Loads without crashes
   - Shows bottom navigation
   - Sample recipes loaded

---

## 🆘 Need Help?

If you encounter issues:

1. Check Apache/MySQL status in XAMPP
2. Verify database exists and has password_hash column
3. Test backend URL in browser
4. Check adb reverse is set up
5. Read logcat for detailed error messages
6. Verify backend files are in correct htdocs location

**Your backend URL should be one of:**
- `http://localhost/smartmeal/backend/api/`
- `http://localhost:8080/smartmeal/backend/api/`

**App uses:** `http://localhost:8080/` (forwarded via adb reverse to your Mac's port 80)

