# Profile Feature - Testing Guide

## How to Test the Profile Feature

### Prerequisites
1. User must be logged in (have valid user data in database)
2. App must have storage permissions for profile picture upload

### Test Scenarios

#### 1. Profile Display
**Steps**:
1. Login to the app
2. Navigate to Profile tab (bottom navigation)
3. Verify the following displays correctly:
   - ✓ User name (from database or email)
   - ✓ Membership status (Premium Member or Free Member)
   - ✓ Joined date (year extracted from timestamp)
   - ✓ Default profile avatar (if no picture set)

**Expected Result**: All user information displays correctly

---

#### 2. Change Profile Picture
**Steps**:
1. On Profile screen, tap the camera FAB button (or profile image)
2. Select "Change picture" from dialog
3. Choose an image from gallery
4. Verify image updates immediately

**Expected Result**: 
- ✓ Image picker opens
- ✓ Selected image displays in circular crop
- ✓ Toast shows "Profile picture updated!"
- ✓ Image persists after closing and reopening app

---

#### 3. Remove Profile Picture
**Steps**:
1. On Profile screen with a profile picture set
2. Tap profile image
3. Select "Remove picture"
4. Verify default avatar appears

**Expected Result**:
- ✓ Default avatar icon shows
- ✓ Toast shows "Profile picture removed"
- ✓ Change persists after app restart

---

#### 4. Notification Toggle
**Steps**:
1. On Profile screen, locate Notifications toggle
2. Toggle it OFF
3. Verify toast shows "Notifications disabled"
4. Close and reopen app
5. Verify toggle state persisted (OFF)
6. Toggle it ON
7. Verify toast shows "Notifications enabled"

**Expected Result**:
- ✓ Toggle works smoothly
- ✓ Appropriate toast messages display
- ✓ Setting persists across app restarts

---

#### 5. Settings Button
**Steps**:
1. Tap settings icon (gear) in top right
2. Verify toast shows "Settings coming soon!"

**Expected Result**:
- ✓ Toast message appears
- ✓ No crash or error

---

#### 6. Logout
**Steps**:
1. Tap "Log Out" button
2. Verify confirmation dialog appears
3. Tap "Cancel" - verify nothing happens
4. Tap "Log Out" button again
5. Tap "Log Out" in dialog
6. Verify redirect to login screen

**Expected Result**:
- ✓ Confirmation dialog shows
- ✓ Cancel works correctly
- ✓ Logout clears user data
- ✓ Redirects to login screen
- ✓ Back button doesn't return to profile
- ✓ Toast shows "Logged out successfully"

---

#### 7. Bottom Navigation
**Steps**:
1. From Profile screen, tap each navigation item:
   - Home
   - Pantry
   - Planner
   - Shopping
2. Verify navigation works
3. Navigate back to Profile
4. Verify profile data still shows correctly

**Expected Result**:
- ✓ All navigation items work
- ✓ No data loss when navigating
- ✓ Profile tab highlights when on profile screen

---

#### 8. Error Handling - No User Data
**Steps**:
1. Clear app data or use fresh install
2. Open Profile without logging in (if possible)
3. Verify default profile shows:
   - Name: "Guest User"
   - Status: "Free Member"
   - Joined: "Joined 2025"

**Expected Result**:
- ✓ No crash
- ✓ Default values display
- ✓ No errors in logcat

---

#### 9. Profile Picture Persistence
**Steps**:
1. Set a profile picture
2. Force close app (swipe from recent apps)
3. Reopen app
4. Navigate to profile
5. Verify picture still shows

**Expected Result**:
- ✓ Profile picture persists after force close
- ✓ Profile picture persists after normal close
- ✓ Profile picture persists after device restart

---

#### 10. Long Names/Edge Cases
**Steps**:
1. Test with various user names:
   - Very long name (50+ characters)
   - Name with special characters
   - Name with emojis
   - Empty display name (should fall back to email)

**Expected Result**:
- ✓ Layout doesn't break
- ✓ Text truncates properly if needed
- ✓ Fallback to email works

---

## Logcat Monitoring

### Tags to Watch:
```
ActivityProfile
PhpAuthRepository
PhpAuthViewModel
```

### Expected Log Messages:
- "Attempting login: [email]" (on login)
- "User saved to local DB" (after login)
- "Error loading profile image" (if image fails to load)
- "User signed out, local data cleared" (on logout)

### Error Scenarios:
Monitor for exceptions and handle gracefully:
- Image loading failures
- Database access issues
- SharedPreferences errors
- Network issues (for remote profile images)

---

## Performance Checks

### Load Times:
- ✓ Profile screen loads in < 1 second
- ✓ Image picker opens quickly
- ✓ Image loads without visible delay
- ✓ No lag when toggling settings

### Memory:
- ✓ No memory leaks (use Android Studio profiler)
- ✓ Images properly cached by Glide
- ✓ No excessive database queries

---

## Accessibility Testing

### Requirements:
- ✓ All images have contentDescription
- ✓ All clickable items have proper touch targets (48dp minimum)
- ✓ Screen reader announces all interactive elements
- ✓ Contrast ratios meet WCAG guidelines

---

## Testing Checklist Summary

- [ ] Profile displays user data correctly
- [ ] Profile picture can be changed
- [ ] Profile picture can be removed
- [ ] Profile picture persists across restarts
- [ ] Default avatar shows when appropriate
- [ ] Notification toggle works
- [ ] Notification setting persists
- [ ] Settings button shows placeholder message
- [ ] Logout shows confirmation
- [ ] Logout clears data and redirects
- [ ] Bottom navigation works
- [ ] No crashes or errors
- [ ] Logcat shows expected messages
- [ ] Performance is acceptable
- [ ] Accessibility requirements met

## Status
Once all items above are checked, the Profile feature is verified as **production-ready**.

