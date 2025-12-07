# 🎯 FINAL SOLUTION - Your Exact Steps

## You DON'T Need to Fix ADB!

Since you're using a **physical phone**, forget about `adb`. It's not necessary!

---

## ✅ HERE'S WHAT TO DO:

### 1. Get Your Mac's IP Address

**EASIEST WAY - Use System Preferences:**
1. Click the **Apple logo** (top-left corner)
2. Click **System Preferences**
3. Click **Network**
4. Look at the active connection (Wi-Fi will have a green dot)
5. You'll see: `IP Address: 192.168.x.x` ← **THAT'S YOUR IP!**
6. Write it down or remember it

**Example:** `192.168.1.5` or `192.168.0.10` or `10.0.0.5`

---

### 2. Edit One Line in Your Code

I've **already opened** the file you need to edit: **ApiClient.kt**

**Look at line 12:**
```kotlin
const val BASE_URL = "http://localhost:8080/smartmeal/backend/api/"
```

**Change it to** (use YOUR Mac's IP):
```kotlin
const val BASE_URL = "http://192.168.1.5/smartmeal/backend/api/"
```
☝️ Replace `192.168.1.5` with the IP you found in Step 1!

**Examples of what it should look like:**
- If your IP is `192.168.0.10`: `"http://192.168.0.10/smartmeal/backend/api/"`
- If your IP is `10.0.0.5`: `"http://10.0.0.5/smartmeal/backend/api/"`
- If your IP is `192.168.1.100`: `"http://192.168.1.100/smartmeal/backend/api/"`

**Save the file!** (Cmd+S in Android Studio)

---

### 3. Rebuild Your App

In the **Terminal** at the bottom of Android Studio (or any terminal):
```bash
cd /Users/mac/StudioProjects/SmartMeal
./gradlew clean assembleDebug installDebug
```

This will:
- Clean the old build
- Build with the new IP address
- Install on your phone

Takes about 1-2 minutes.

---

### 4. Test From Your Phone's Browser FIRST

**Before running the app**, make sure the connection works:

1. **Connect your phone to the SAME Wi-Fi** as your Mac
2. Open **Chrome** or **Safari** on your phone
3. Type in the browser: `http://YOUR_MAC_IP/smartmeal/backend/api/users.php`
   - Example: `http://192.168.1.5/smartmeal/backend/api/users.php`
4. You should see text like: `{"message":"SmartMeal User API"...}`

**If you see that JSON text** = ✅ Your backend is accessible!  
**If you get "can't reach" or timeout** = ❌ See troubleshooting below

---

### 5. Run Your App and Test Signup!

Now your app should work:
1. **Splash screen** (2 seconds)
2. **Login screen** appears
3. Click "Sign Up"
4. Fill in the form
5. Click "Sign Up" button
6. Should navigate to **Onboarding 1** ✅

---

## 🛠️ Troubleshooting

### If phone browser can't access the URL:

**Check #1: Same Wi-Fi?**
- Mac and phone MUST be on the same Wi-Fi network
- Check Wi-Fi name matches on both devices

**Check #2: XAMPP Running?**
- Open XAMPP on your Mac
- Apache should show "Running" (green light)
- MySQL should show "Running" (green light)

**Check #3: Firewall?**
- Mac's firewall might block incoming connections
- **Temporary test**: System Preferences → Security → Firewall → Turn Off
- **Test again** from phone browser
- **Remember to turn it back on after!**

**Check #4: Test from Mac first:**
- On your Mac, open browser
- Go to: `http://localhost/smartmeal/backend/api/users.php`
- Should see JSON
- If this doesn't work, backend isn't set up correctly

---

## 📋 Quick Checklist

Before testing:
- [ ] Got my Mac's IP address (System Preferences → Network)
- [ ] Updated `ApiClient.kt` line 12 with MY IP
- [ ] Saved the file (Cmd+S)
- [ ] Ran `./gradlew clean assembleDebug installDebug`
- [ ] Phone and Mac on same Wi-Fi
- [ ] XAMPP Apache is running
- [ ] Tested URL in phone's browser - saw JSON
- [ ] Ready to test signup in app!

---

## 🎉 Success Looks Like:

1. Phone browser shows JSON from backend URL ✅
2. App signup button → Creates account ✅
3. Navigates to Onboarding 1 ✅
4. Can go through: Onboarding 1 → 2 → 3 → Home ✅

---

## 📞 Still Stuck?

Tell me:
1. What's your Mac's IP? (System Preferences → Network)
2. Can you access `http://YOUR_IP/smartmeal/backend/api/users.php` from phone's browser?
3. What happens when you try to signup in the app?

I'll help you fix it!

---

## 💡 Why This Works

- **localhost** = your phone itself (doesn't work)
- **Your Mac's IP** = your Mac on the network (works!)
- Physical phones need actual IP addresses to reach other devices
- Emulators can use `localhost` with `adb reverse`, phones cannot

**Bottom line: Use your Mac's real IP address and everything will work!** 🚀

