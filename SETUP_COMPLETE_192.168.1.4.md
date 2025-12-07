# ✅ SmartMeal Setup Complete - Your IP: 192.168.1.4

## What I Just Did For You:

✅ **Updated ApiClient.kt**
- Changed: `http://localhost:8080/smartmeal/backend/api/`
- To: `http://192.168.1.4/smartmeal/backend/api/`

✅ **Started rebuilding your app**
- Running: `./gradlew clean assembleDebug installDebug`
- This will take 1-2 minutes

---

## 🎯 NEXT: Test Before Running App

### Step 1: Test from Your Phone's Browser (IMPORTANT!)

**On your phone:**
1. Make sure you're on the **same Wi-Fi** as your Mac
2. Open **Chrome** or **Safari**
3. Go to this URL:
   ```
   http://192.168.1.4/smartmeal/backend/api/users.php
   ```
4. You should see JSON like:
   ```json
   {
     "message": "SmartMeal User API",
     "status": "active",
     "endpoints": { ... }
   }
   ```

### ✅ If you see JSON:
**Perfect! Your backend is accessible. Continue to Step 2.**

### ❌ If you get "Can't reach this page" or timeout:

**Check these:**
1. **Same Wi-Fi?** Phone and Mac must be on the same network
2. **XAMPP running?** Open XAMPP, Apache should show "Running" (green)
3. **Test on Mac first:**
   - Open browser on Mac
   - Go to: `http://localhost/smartmeal/backend/api/users.php`
   - Should see JSON
   - If this doesn't work, backend isn't set up correctly
4. **Firewall blocking?**
   - System Preferences → Security & Privacy → Firewall
   - Temporarily **Turn Off** for testing
   - Test again from phone browser

---

## Step 2: Run Your App and Test Signup!

Once phone browser shows JSON:

### Test the Complete Flow:

1. **Open app** on your phone
2. **Splash screen** (2 seconds) → Auto-navigates to Login
3. Click **"Don't have an account? Sign Up"**
4. **Fill in signup form:**
   - Name: `Test User`
   - Email: `test@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
5. Click **"Sign Up"**

### Expected Results:

✅ **Success!** Should see:
- Toast: "Account created successfully!"
- Navigate to **Onboarding Screen 1**

✅ **Onboarding Flow:**
- **Screen 1** → Click "Next" → Screen 2
- **Screen 2** → Click "Back" → Screen 1 (test back button)
- **Screen 2** → Click "Next" → Screen 3
- **Screen 3** → Click "Back" → Screen 2 (test back button)
- **Screen 3** → Click "Got It" → **Home Screen** ✅

✅ **Login Flow:**
- Close app and reopen
- At Login screen, enter:
  - Email: `test@example.com`
  - Password: `password123`
- Click "Login"
- Should go directly to **Home Screen** (skips onboarding for returning users)

---

## 🆘 Troubleshooting

### Issue: Phone browser can't access backend

**Solutions:**
```
1. Verify both on same Wi-Fi
2. Check XAMPP Apache is running (open XAMPP app)
3. Test on Mac: http://localhost/smartmeal/backend/api/users.php
4. Disable Mac firewall: System Preferences → Security → Firewall → Turn Off
5. Retry phone browser test
```

### Issue: Signup hangs or shows "Connection timeout"

**Solutions:**
```
1. First verify phone browser can access backend URL
2. Check logcat for errors:
   In Android Studio Terminal:
   adb logcat -s PhpAuthRepository:D PhpAuthViewModel:D

3. If logcat not working (no adb), check:
   - Phone and Mac on same Wi-Fi
   - Backend URL works in phone browser
   - XAMPP Apache running
```

### Issue: "User already exists" error

**Solution:**
```
Delete test user from database:
1. Open: http://localhost/phpmyadmin
2. Select: smartmeal_db
3. Click: users table
4. Find: test@example.com
5. Delete that row
6. Try signup again
```

### Issue: Database error about password_hash

**Solution:**
```
Add missing column:
1. Open: http://localhost/phpmyadmin
2. Select: smartmeal_db
3. Click: users table
4. Click: SQL tab
5. Run:
   ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) AFTER email;
```

---

## 📊 Success Checklist

Before testing:
- [x] Updated ApiClient.kt with IP: 192.168.1.4
- [x] Rebuilt app
- [ ] Phone and Mac on same Wi-Fi
- [ ] XAMPP Apache running
- [ ] MySQL running
- [ ] Database smartmeal_db exists
- [ ] Backend accessible from phone browser
- [ ] Ready to test signup!

After testing:
- [ ] Phone browser shows JSON from backend ✅
- [ ] App signup creates account ✅
- [ ] Navigates to Onboarding 1 ✅
- [ ] Can navigate: Onboarding 1 → 2 → 3 ✅
- [ ] Onboarding 3 → Home Screen ✅
- [ ] Login with created account works ✅
- [ ] Goes directly to Home (skips onboarding) ✅

---

## 🎉 Summary

**Your app is now configured for physical phone!**

**Backend URL:** `http://192.168.1.4/smartmeal/backend/api/`

**What works without adb:**
✅ Signup → Creates user in MySQL
✅ Login → Validates against MySQL
✅ All CRUD operations with backend
✅ Data sync between app and server

**Just make sure:**
- Phone and Mac on same Wi-Fi
- XAMPP running
- Test backend URL in phone browser first
- Then run the app!

---

## 📱 Your Current Network Setup

```
┌─────────────┐         Same Wi-Fi         ┌─────────────┐
│             │    192.168.1.x network     │             │
│  Your Mac   │◄──────────────────────────►│ Your Phone  │
│ 192.168.1.4 │                            │             │
│             │                            │             │
│ XAMPP:80    │                            │ SmartMeal   │
│ MySQL:3306  │                            │    App      │
└─────────────┘                            └─────────────┘
```

**The app makes HTTP requests to:** `http://192.168.1.4/smartmeal/backend/api/`

---

## 🔗 Quick Links

- **Backend API:** http://192.168.1.4/smartmeal/backend/api/users.php
- **phpMyAdmin:** http://localhost/phpmyadmin (on Mac)
- **Test on Mac:** http://localhost/smartmeal/backend/api/users.php

---

**🚀 You're all set! Test the backend URL in your phone's browser, then run the app!**

