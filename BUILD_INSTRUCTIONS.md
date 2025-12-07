# 🔨 Build & Run Instructions

## Option 1: Android Studio (Recommended)

1. Open **Android Studio**
2. Open the **SmartMeal** project
3. **Build → Rebuild Project**
4. Connect your phone or start emulator
5. Click **Run** (green play button)

The app will build and install automatically.

---

## Option 2: Command Line

If you see "Unable to locate a Java Runtime", use Android Studio's terminal instead:

1. Open **Android Studio**
2. Click **Terminal** tab at the bottom
3. Run:
```bash
./gradlew clean assembleDebug installDebug
```

Android Studio's terminal has the correct Java path configured.

---

## What to Test:

### Home Screen Navigation:
1. **Quick Actions:**
   - Tap "Recipe List" → Should navigate (or show "coming soon")
   - Tap "Learn More" → Opens Learn More screen with full content ✅
   - Tap "AI Assistant" → Should navigate (or show "coming soon")

2. **Bottom Navigation:**
   - Tap "Pantry" → Should navigate (or show "coming soon")
   - Tap "Planner" → Should navigate (or show "coming soon")
   - Tap "Shopping" → Should navigate (or show "coming soon")
   - Tap "Profile" → Should navigate (or show "coming soon")

### Learn More Screen:
1. Scroll through all sections
2. Read FAQ items
3. Tap back button → Returns to Home

---

## Expected Behavior:

- **Learn More** button → Opens new screen with FAQs and info ✅
- Other buttons → May show "coming soon" toast (pages not implemented yet)
- All navigation is safe (won't crash even if destination missing)
- Bottom nav highlights "Home" on Home screen

---

## Debug Logging:

To see navigation logs:
```bash
adb logcat -s ActivityHome:D
```

You'll see:
```
ActivityHome: ActivityHome created
ActivityHome: Learn More button clicked
ActivityHome: Pantry nav clicked
etc.
```

---

## Files Changed:

✅ ActivityHome.kt - Added all navigation
✅ activity_home.xml - Updated button labels
✅ ActivityLearnMore.kt - NEW screen
✅ activity_learn_more.xml - NEW layout with content
✅ AndroidManifest.xml - Registered new activity

---

**Build from Android Studio for easiest experience!** 🚀

