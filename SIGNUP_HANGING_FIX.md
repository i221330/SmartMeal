# 🔧 SIGNUP HANGING - TROUBLESHOOTING GUIDE

## Problem Analysis:

From your logs, the app **crashes/hangs** when you try to signup. Key indicators:
- `PROCESS ENDED (18904)` - App crashed
- No `PhpAuthRepository` logs - Never reached the auth code
- App took very long then stopped - Network timeout

## Root Cause:

**The PHP backend server is likely not accessible from the Android app.**

---

## ✅ FIXES APPLIED:

### 1. Added 10-Second Timeout
- App will now fail fast instead of hanging forever
- Clear error messages: "Connection timeout. Is XAMPP running?"

### 2. Better Error Messages
- ✅ Timeout: "Connection timeout. Is XAMPP running?"
- ✅ Connection failed: "Cannot connect to server. Check XAMPP and network."
- ✅ Other errors: Specific error details

### 3. Detailed Logging
- Logs API URL being used
- Logs each step of the process
- Easy to debug connection issues

---

## 🧪 STEP-BY-STEP FIX:

### Step 1: Verify XAMPP is Running

```bash
# Check if Apache and MySQL are running
# Open XAMPP Control Panel and ensure:
# ✅ Apache is running (green)
# ✅ MySQL is running (green)
```

### Step 2: Test PHP Backend Directly

```bash
# Test from your Mac browser:
open http://localhost/smartmeal/backend/api/users.php

# Should show empty page or PHP output, NOT "404 Not Found"
```

### Step 3: Add Password Column to MySQL

```sql
-- Open: http://localhost/phpmyadmin
-- Select: smartmeal_db
-- Run SQL:

ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255) AFTER email;
```

### Step 4: Test Backend Registration

```bash
# Test signup endpoint:
curl -X POST http://localhost/smartmeal/backend/api/users.php?action=register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123","display_name":"Test User"}'

# Expected output:
# {"message":"User registered successfully","user_id":"user_...","email":"test@test.com","display_name":"Test User"}
```

### Step 5: Rebuild & Install App

```bash
cd /Users/mac/StudioProjects/SmartMeal
./gradlew clean
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 6: Monitor Logs While Testing

```bash
# Clear logs and watch for errors:
adb logcat -c
adb logcat | grep -E "PhpAuthRepository|PhpAuthViewModel|ActivitySignup|FATAL"
```

---

## 📱 Test Signup with New Timeouts:

### Test 1: Server Not Running
```
1. Stop XAMPP
2. Try signup
3. Should show: "Connection timeout. Is XAMPP running?" (after 10 seconds)
```

### Test 2: Server Running
```
1. Start XAMPP
2. Try signup
3. Should either:
   - ✅ Success → Navigate to Onboarding
   - ❌ Error → Show clear message
```

---

## 🔍 Expected Logcat Output:

### If Working:
```
D PhpAuthRepository: Attempting signup: test@example.com
D PhpAuthRepository: API URL: http://10.0.2.2/smartmeal/backend/api/
D PhpAuthRepository: Making API call with timeout: 10000ms
D PhpAuthRepository: Signup successful: User registered successfully
D PhpAuthRepository: User saved to local DB
D PhpAuthViewModel: Signup successful: test@example.com
D ActivitySignup: Auth state changed: Authenticated
```

### If XAMPP Not Running:
```
D PhpAuthRepository: Attempting signup: test@example.com
D PhpAuthRepository: API URL: http://10.0.2.2/smartmeal/backend/api/
D PhpAuthRepository: Making API call with timeout: 10000ms
E PhpAuthRepository: Signup connection failed - cannot reach server
D PhpAuthViewModel: Signup failed: Cannot connect to server. Check XAMPP and network.
D ActivitySignup: Auth state changed: Error
```

### If Timeout:
```
D PhpAuthRepository: Attempting signup: test@example.com
D PhpAuthRepository: API URL: http://10.0.2.2/smartmeal/backend/api/
D PhpAuthRepository: Making API call with timeout: 10000ms
E PhpAuthRepository: Signup timeout - server not responding
D PhpAuthViewModel: Signup failed: Connection timeout. Is XAMPP running?
D ActivitySignup: Auth state changed: Error
```

---

## 🆘 Common Issues & Solutions:

### Issue 1: "Connection timeout"
**Cause:** XAMPP not running or Apache stopped
**Fix:**
```bash
# Start XAMPP Apache
# Or restart Apache in XAMPP Control Panel
```

### Issue 2: "Cannot connect to server"
**Cause:** Wrong URL or firewall blocking
**Fix:**
```bash
# Verify backend exists:
ls -la /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/users.php

# Should exist and not be empty
```

### Issue 3: "404 Not Found"
**Cause:** Backend files not in correct location
**Fix:**
```bash
# Copy backend to XAMPP:
cp -r /Users/mac/StudioProjects/SmartMeal/backend /Applications/XAMPP/xamppfiles/htdocs/smartmeal/

# Verify:
ls -la /Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/api/users.php
```

### Issue 4: "User with this email already exists"
**Cause:** Testing with same email twice
**Fix:**
```bash
# Use different email each time, or delete from MySQL:
# Open phpMyAdmin → smartmeal_db → users → Delete test user
```

---

## ✅ Checklist Before Testing:

- [ ] XAMPP is running (Apache green, MySQL green)
- [ ] Backend files exist in `/Applications/XAMPP/xamppfiles/htdocs/smartmeal/backend/`
- [ ] Test URL works: http://localhost/smartmeal/backend/api/users.php
- [ ] MySQL has `password_hash` column
- [ ] App rebuilt with new timeout code
- [ ] Logcat monitoring is running

---

## 🎯 What Changed:

| Before | After |
|--------|-------|
| ❌ Hangs forever | ✅ 10-second timeout |
| ❌ No error message | ✅ Clear error: "Is XAMPP running?" |
| ❌ No logging | ✅ Detailed logs at every step |
| ❌ Generic errors | ✅ Specific connection errors |

---

## 🚀 Next Steps:

1. **Verify XAMPP is running**
2. **Test backend with curl** (see Step 4 above)
3. **Rebuild & install app**
4. **Watch logcat while testing signup**
5. **Share the logs** if still having issues

The app will now fail fast and tell you exactly what's wrong instead of hanging! 🎉

