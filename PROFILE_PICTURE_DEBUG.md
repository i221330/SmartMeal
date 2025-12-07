# Profile Picture Persistence - Debug Guide

## The Issue
Profile picture was not persisting after logout/login even though it was supposedly synced to the server.

## Root Cause Analysis

### Previous Issues:
1. **File existence check during login** - The repository was checking if the profile image file existed when logging in, and clearing the URL if it didn't. This was preventing the URL from being saved to the local DB.
2. **Incomplete restoration logic** - The `checkAndRestoreProfileImage()` function wasn't handling all edge cases properly.
3. **Insufficient logging** - Hard to debug what was happening during sync.

## The Complete Flow

### 1. Setting Profile Picture
```
User selects image
  ↓
copyImageToInternalStorage()
  ↓
Save to: /data/data/com.example.smartmeal/files/profile_<userId>.jpg
  ↓
Update local Room database (User.profileImageUrl = file path)
  ↓
Sync to backend server via updateUserProfile()
  ↓
Server saves path in users.profile_image_url column
```

### 2. Logging Out
```
User logs out
  ↓
signOut() clears local Room database
  ↓
Profile image FILE remains on device storage
```

### 3. Logging Back In
```
User logs in
  ↓
Server returns user data (including profile_image_url)
  ↓
PhpAuthRepository saves to local database
  ↓
ActivityProfile loads user data
  ↓
checkAndRestoreProfileImage() validates and restores if needed
```

## Changes Made

### 1. PhpAuthRepository.kt - signInWithEmail()
**BEFORE:**
```kotlin
var profileImageUrl = userData.profile_image_url

// Check if the profile image file exists locally
if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
    val file = java.io.File(profileImageUrl)
    if (!file.exists()) {
        profileImageUrl = null  // ❌ This was clearing the URL!
    }
}
```

**AFTER:**
```kotlin
// IMPORTANT: Keep the profile_image_url from server as-is
// The ActivityProfile will check if the file exists and restore it if needed
val profileImageUrl = userData.profile_image_url
```

**Why:** Don't validate file existence in the repository - let the UI layer handle it.

### 2. ActivityProfile.kt - checkAndRestoreProfileImage()
**Enhanced to handle multiple scenarios:**

**Scenario A:** DB has URL, file exists at that path → ✓ All good
**Scenario B:** DB has URL, file doesn't exist, but exists at expected location → Update DB and sync to server
**Scenario C:** DB has URL, file doesn't exist anywhere → Clear DB entry
**Scenario D:** DB has no URL, but file exists locally → Restore to DB and sync to server
**Scenario E:** DB has no URL, no file exists → Nothing to do

### 3. Added Comprehensive Logging
- Profile image save process
- Server sync attempts
- Login process with profile image handling
- Restoration logic step-by-step

## How to Test

### Prerequisites
1. XAMPP must be running
2. Database must have `profile_image_url` column
3. Backend API must be accessible

### Test Procedure

#### Test 1: Set Profile Picture
1. Login to the app
2. Go to Profile screen
3. Tap profile picture → "Change picture"
4. Select an image
5. **Expected logs:**
   ```
   Image saved to internal storage
   ✓ Profile image URI saved to local database
   ═══ SYNCING TO SERVER ═══
   ✓✓✓ Profile image URL synced to server successfully!
   ```
6. **Expected UI:** Toast message "✓ Profile saved and synced!"

#### Test 2: Logout and Login (Same Device)
1. Logout from the app
2. Login again with the same account
3. **Expected logs during login:**
   ```
   Login: Server response received
     - profile_image_url: '/data/data/...'
   User saved to local DB
   ```
4. **Expected logs during profile load:**
   ```
   checkAndRestoreProfileImage: Starting check
     ✓ Profile image file exists at DB path, all good!
   ```
5. **Expected UI:** Profile picture is visible

#### Test 3: Logout, Delete Cache, Login
1. Logout
2. Clear app data (Settings → Apps → SmartMeal → Clear Data)
3. Login again
4. **Expected:** Profile picture should still appear (restored from server)

### Debugging Tips

#### If profile picture doesn't sync to server:
1. Check Logcat for "SYNCING TO SERVER" logs
2. Look for error messages like:
   - "Connection timeout" → XAMPP not running
   - "Cannot connect to server" → Wrong IP address in ApiClient
   - "Profile update failed" → Backend error

#### If profile picture doesn't persist after login:
1. Check Logcat for "Login: Server response received"
2. Verify `profile_image_url` field in the response
3. Check "checkAndRestoreProfileImage" logs
4. Look for file path mismatches

#### If you see "⚠ Server sync error":
- The profile picture is saved locally but didn't sync to server
- Check XAMPP is running
- Check backend logs in `/Applications/XAMPP/xamppfiles/logs/`
- Verify database connection

## Database Verification

### Check if profile image URL is in database:
```sql
SELECT firebase_uid, email, display_name, profile_image_url 
FROM users 
WHERE email = 'your@email.com';
```

### Expected result:
```
profile_image_url: /data/data/com.example.smartmeal/files/profile_user_xyz.jpg
```

## Backend Verification

### Test the update endpoint directly:
```bash
curl -X PUT "http://10.0.2.2/smartmeal/api/users.php?action=profile" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user_xyz",
    "profile_image_url": "/data/data/com.example.smartmeal/files/profile_user_xyz.jpg"
  }'
```

### Expected response:
```json
{
  "message": "Profile updated successfully",
  "user": {
    "user_id": "user_xyz",
    "profile_image_url": "/data/data/..."
  }
}
```

## Important Notes

### File Storage Location
- Profile images are stored in app's private internal storage
- Path format: `/data/data/com.example.smartmeal/files/profile_<userId>.jpg`
- These files persist across app restarts but are cleared when:
  - App data is cleared
  - App is uninstalled

### Multi-Device Limitation
- The current implementation stores the **file path** on the server, not the actual image
- This means profile pictures only work on the device where they were uploaded
- If user logs in on a different device, the file path won't resolve
- **To support multi-device:** Need to upload image to cloud storage and store the cloud URL

### Server URL
- Make sure `ApiClient.BASE_URL` points to your XAMPP server
- For Android Emulator: `http://10.0.2.2/smartmeal/api/`
- For Physical Device: `http://<your-computer-ip>/smartmeal/api/`

## What's Next?

If the profile picture still doesn't persist after these changes:
1. Run the app with Logcat open
2. Follow Test Procedure above
3. Copy all logs containing "ProfileImage", "SYNCING", or "checkAndRestore"
4. Check database to see if `profile_image_url` is actually saved
5. Verify backend logs for any PHP errors

