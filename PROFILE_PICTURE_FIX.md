# Profile Picture Persistence Fix

## Problem
The profile picture was not persisting after logout and login. The issue was that:
1. Profile pictures were saved locally to the device's internal storage
2. The profile image URL was saved to the local Room database
3. However, it was NOT synced to the backend server
4. When the user logged out and logged back in, the app fetched user data from the server (which had `profile_image_url` as null)
5. This overwrote the local database, losing the profile picture path

## Solution
Implemented a complete profile synchronization system:

### Backend Changes (users.php)
1. **Added PUT endpoint** for updating user profiles:
   - URL: `PUT users.php?action=profile`
   - Accepts: `display_name`, `phone_number`, `profile_image_url`
   - Updates the user record in the database
   - Returns the updated user data

2. **Added updateUserProfile() function** that:
   - Accepts user_id and optional profile fields
   - Dynamically builds UPDATE query based on provided fields
   - Returns updated user data on success

### Android App Changes

#### 1. ApiModels.kt
- Added `UserProfileUpdateRequest` data class
- Added `UserUpdateResponse` data class

#### 2. ApiServices.kt
- Added `updateUserProfile()` method to `UserApiService` interface

#### 3. PhpAuthRepository.kt
- Added `updateUserProfile()` method that:
  - Sends profile updates to the server
  - Updates the local Room database
  - Handles errors gracefully

#### 4. ActivityProfile.kt
Updated three key functions:

**copyImageToInternalStorage():**
- Saves profile image to internal storage
- Updates local database
- **NEW:** Syncs profile_image_url to server
- Handles server sync failures gracefully (doesn't block local save)

**removeProfilePicture():**
- Removes local image file
- Updates local database
- **NEW:** Syncs removal to server (sends empty string)

**updateUserName():**
- Updates display name locally
- **NEW:** Syncs to server using the repository method

## How It Works Now

### Setting a Profile Picture:
1. User selects an image
2. Image is copied to app's internal storage
3. Local database is updated with the file path
4. File path is synced to the backend server
5. Server stores the path in the `profile_image_url` column

### Logging Out and Back In:
1. User logs out (local data cleared)
2. User logs in
3. Server sends back user data including `profile_image_url`
4. App populates local database with server data
5. Profile picture path is restored!

### Important Notes:
- The profile image file itself is stored locally (not uploaded to server)
- The file path (e.g., `/data/data/com.example.smartmeal/files/profile_user_123.jpg`) is stored on the server
- If the user logs in on a different device, they won't see the profile picture (because the file doesn't exist on that device)
- For true multi-device sync, you would need to upload the image file to a server/cloud storage

## Testing Instructions

1. **Start XAMPP** (or your PHP server)
2. **Build and run the app**
3. **Test the flow:**
   - Log in with a test account
   - Go to Profile screen
   - Change the profile picture
   - Log out
   - Log back in
   - Verify the profile picture is still there

4. **Verify server sync:**
   - Check the database `users` table
   - The `profile_image_url` column should contain the local file path

## Database Schema
The `users` table should have a `profile_image_url` column:
```sql
ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(255) DEFAULT NULL;
```

This is already in the schema, so no migration needed.

## Error Handling
- If server sync fails, the profile picture is still saved locally
- Errors are logged but don't prevent the user from continuing
- The app works offline for local profile changes
- Next successful login will attempt to sync again

