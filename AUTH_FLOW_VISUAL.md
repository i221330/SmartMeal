# 🎯 SmartMeal Authentication Flow - Visual Guide

## Complete Authentication Architecture

```
┌────────────────────────────────────────────────────────┐
│                    USER OPENS APP                      │
└─────────────────────┬──────────────────────────────────┘
                      │
                      ▼
         ┌───────────────────────┐
         │   SPLASH SCREEN       │
         │   (2-3 seconds)       │
         └─────────┬─────────────┘
                   │
                   ▼
         ┌───────────────────────┐
         │   CHECK AUTH STATE    │
         └─────────┬─────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
    LOGGED IN             NOT LOGGED IN
        │                     │
        ▼                     ▼
   ┌─────────┐         ┌─────────────┐
   │  HOME   │         │ LOGIN SCREEN│
   │ SCREEN  │         └──────┬──────┘
   └─────────┘                │
                      ┌───────┴────────┐
                      │                │
                   SIGN UP          SIGN IN
                      │                │
                      ▼                ▼
              ┌──────────────┐  ┌──────────────┐
              │ USER ENTERS: │  │ USER ENTERS: │
              │ • Email      │  │ • Email      │
              │ • Password   │  │ • Password   │
              │ • Name       │  └──────┬───────┘
              └──────┬───────┘         │
                     │                 │
                     ▼                 ▼
         ┌──────────────────────────────────────┐
         │      FIREBASE AUTHENTICATION         │
         │   (Email/Password auth handled       │
         │    by Google's servers)              │
         └──────────┬───────────────────────────┘
                    │
                    ▼
         ┌──────────────────────────────────────┐
         │     PHP/MYSQL BACKEND REGISTRATION    │
         │   POST to: users.php?action=register │
         │   Stores: firebase_uid, email, etc.  │
         └──────────┬───────────────────────────┘
                    │
                    ▼
         ┌──────────────────────────────────────┐
         │     ROOM DATABASE (Local Storage)    │
         │   Saves user data for offline access │
         └──────────┬───────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
   SIGNUP SUCCESS          LOGIN SUCCESS
        │                       │
        ▼                       ▼
  ┌─────────────┐         ┌─────────┐
  │ ONBOARDING 1│         │  HOME   │
  └──────┬──────┘         │ SCREEN  │
         │                └─────────┘
         ▼
  ┌─────────────┐
  │ ONBOARDING 2│
  └──────┬──────┘
         │
         ▼
  ┌─────────────┐
  │ ONBOARDING 3│
  └──────┬──────┘
         │
         ▼
    ┌─────────┐
    │  HOME   │
    │ SCREEN  │
    └─────────┘
```

---

## Data Flow During Signup

```
STEP 1: USER FILLS FORM
┌──────────────────────┐
│  Signup Screen       │
│  Email: test@ex.com  │
│  Password: ******    │
│  Name: John Doe      │
└──────────┬───────────┘
           │ Click "Sign Up"
           ▼

STEP 2: FIREBASE AUTH
┌───────────────────────────────────┐
│   Firebase Authentication         │
│   • Creates user account          │
│   • Returns Firebase UID          │
│   • Example: "abc123xyz456"       │
└──────────┬────────────────────────┘
           │ Success
           ▼

STEP 3: PHP BACKEND
┌───────────────────────────────────┐
│   HTTP POST Request               │
│   URL: users.php?action=register  │
│   Body: {                         │
│     firebase_uid: "abc123..."     │
│     email: "test@example.com"     │
│     display_name: "John Doe"      │
│   }                               │
└──────────┬────────────────────────┘
           │
           ▼
┌───────────────────────────────────┐
│   PHP processes request           │
│   • Validates data                │
│   • Inserts into MySQL:           │
│     INSERT INTO users (           │
│       firebase_uid,               │
│       email,                      │
│       display_name                │
│     ) VALUES (...)                │
└──────────┬────────────────────────┘
           │
           ▼
┌───────────────────────────────────┐
│   MySQL Database                  │
│   Table: users                    │
│   ┌────┬──────────┬────────────┐  │
│   │ id │firebase  │   email    │  │
│   ├────┼──────────┼────────────┤  │
│   │ 1  │abc123... │test@ex.com │  │
│   └────┴──────────┴────────────┘  │
└──────────┬────────────────────────┘
           │
           ▼

STEP 4: LOCAL STORAGE
┌───────────────────────────────────┐
│   Room Database (SQLite)          │
│   • Saves user for offline access │
│   • Used when internet is offline │
└──────────┬────────────────────────┘
           │
           ▼

STEP 5: NAVIGATE
┌───────────────────────────────────┐
│   Navigate to Onboarding Screen 1 │
└───────────────────────────────────┘
```

---

## Data Flow During Login

```
STEP 1: USER ENTERS CREDENTIALS
┌──────────────────────┐
│  Login Screen        │
│  Email: test@ex.com  │
│  Password: ******    │
└──────────┬───────────┘
           │ Click "Login"
           ▼

STEP 2: FIREBASE AUTH
┌───────────────────────────────────┐
│   Firebase Authentication         │
│   • Verifies email/password       │
│   • Returns user if valid         │
│   • Includes Firebase UID         │
└──────────┬────────────────────────┘
           │ Success
           ▼

STEP 3: VERIFY WITH PHP BACKEND
┌───────────────────────────────────┐
│   HTTP POST Request               │
│   URL: users.php?action=login     │
│   Body: {                         │
│     firebase_uid: "abc123..."     │
│   }                               │
└──────────┬────────────────────────┘
           │
           ▼
┌───────────────────────────────────┐
│   PHP checks MySQL                │
│   SELECT * FROM users             │
│   WHERE firebase_uid = ?          │
│                                   │
│   Returns user data if exists     │
└──────────┬────────────────────────┘
           │
           ▼

STEP 4: LOCAL STORAGE
┌───────────────────────────────────┐
│   Room Database                   │
│   • Updates local user data       │
│   • Syncs with server data        │
└──────────┬────────────────────────┘
           │
           ▼

STEP 5: NAVIGATE
┌───────────────────────────────────┐
│   Navigate directly to HOME       │
│   (Skips onboarding - seen before)│
└───────────────────────────────────┘
```

---

## Database Structure

### MySQL (Cloud - PHP Backend)
```sql
users Table:
┌────┬──────────────┬─────────────────┬──────────────┬────────────┐
│ id │ firebase_uid │     email       │ display_name │ joined_date│
├────┼──────────────┼─────────────────┼──────────────┼────────────┤
│ 1  │ abc123xyz... │ test@example.com│ John Doe     │ 2025-12-06 │
│ 2  │ def456uvw... │ jane@example.com│ Jane Smith   │ 2025-12-06 │
└────┴──────────────┴─────────────────┴──────────────┴────────────┘
```

### Room (Local - Android Device)
```kotlin
User Entity (SQLite):
┌──────────────┬─────────────────┬──────────────┬────────────┐
│ uid          │     email       │ display_name │ joined_date│
├──────────────┼─────────────────┼──────────────┼────────────┤
│ abc123xyz... │ test@example.com│ John Doe     │ 1733529600 │
└──────────────┴─────────────────┴──────────────┴────────────┘
```

---

## File Locations

### PHP Backend Files:
```
/Applications/XAMPP/xamppfiles/htdocs/smartmeal/
├── backend/
│   ├── config/
│   │   └── database.php          ← MySQL connection config
│   ├── api/
│   │   └── users.php              ← User endpoints
│   └── database/
│       └── schema.sql             ← Database structure
```

### Android App Files:
```
/Users/mac/StudioProjects/SmartMeal/app/src/main/java/.../
├── network/
│   ├── ApiClient.kt              ← Retrofit setup
│   ├── ApiServices.kt            ← API endpoints
│   └── models/
│       └── ApiModels.kt          ← Request/Response models
├── data/
│   ├── local/
│   │   └── UserDao.kt            ← Room database access
│   └── repository/
│       └── AuthRepository.kt     ← Handles Firebase + PHP
└── viewmodel/
    └── AuthViewModel.kt          ← UI logic
```

---

## API Endpoints

### User Registration
```
POST http://10.0.2.2/smartmeal/backend/api/users.php?action=register

Request:
{
  "firebase_uid": "abc123xyz456",
  "email": "test@example.com",
  "phone_number": null,
  "display_name": "John Doe",
  "profile_image_url": null
}

Response (Success):
{
  "message": "User registered successfully",
  "user_id": 1
}

Response (Error):
{
  "message": "Unable to register user"
}
```

### User Login
```
POST http://10.0.2.2/smartmeal/backend/api/users.php?action=login

Request:
{
  "firebase_uid": "abc123xyz456"
}

Response (Success):
{
  "message": "Login successful",
  "user": {
    "id": 1,
    "firebase_uid": "abc123xyz456",
    "email": "test@example.com",
    "display_name": "John Doe",
    "is_premium": false,
    "joined_date": "2025-12-06 10:30:00"
  }
}

Response (Error):
{
  "message": "User not found"
}
```

---

## Testing Checklist

### ✅ XAMPP Setup:
- [ ] Apache running (green in XAMPP)
- [ ] MySQL running (green in XAMPP)
- [ ] phpMyAdmin accessible: `http://localhost/phpmyadmin`
- [ ] Database `smartmeal_db` created
- [ ] 7 tables visible in database
- [ ] 5 sample recipes in `recipes` table

### ✅ PHP Backend:
- [ ] Files copied to `/Applications/XAMPP/xamppfiles/htdocs/smartmeal/`
- [ ] Test URL works: `http://localhost/smartmeal/backend/api/users.php`
- [ ] Shows "Method not allowed" message

### ✅ Android App:
- [ ] Project synced in Android Studio
- [ ] No build errors
- [ ] App runs on emulator
- [ ] Splash screen appears

### ✅ Authentication:
- [ ] Signup creates user in Firebase
- [ ] Signup creates user in MySQL (check phpMyAdmin)
- [ ] Signup navigates to Onboarding 1
- [ ] Login works with registered user
- [ ] Login navigates to Home (skips onboarding)
- [ ] Can logout and login again

### ✅ Navigation:
- [ ] Splash → Login
- [ ] Login ↔ Signup toggle works
- [ ] Signup → Onboarding 1
- [ ] Onboarding 1 → Next → Onboarding 2
- [ ] Onboarding 2 → Back → Onboarding 1
- [ ] Onboarding 2 → Next → Onboarding 3
- [ ] Onboarding 3 → Back → Onboarding 2
- [ ] Onboarding 3 → "Got it!" → Home
- [ ] Login → Home (direct)

---

## 🎯 Success = All Checkboxes Checked!

Once everything works, we'll implement:
1. Home screen with recipe suggestions
2. Recipe list (from MySQL)
3. Pantry CRUD (MySQL)
4. Shopping list (MySQL)
5. Meal planner (MySQL)

**One screen at a time, fully working before moving to next!**

