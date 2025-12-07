# SmartMeal - AI-Assisted Recipe & Meal Planner

> **Mobile Application for Software for Mobile Devices Course**

A native Android app that helps users manage their pantry, plan weekly meals, maintain shopping lists, and discover recipes based on available ingredients.

---

## 📖 Quick Links

- **Full Documentation**: See [PROJECT_STATUS.md](PROJECT_STATUS.md) for complete project details
- **Backend API**: `backend/api/` directory
- **Database Schema**: `backend/database/schema.sql`
- **Test Scripts**: `test_backend.sh` and `test_connectivity.sh`

---

## 🚀 Quick Start

### Prerequisites
- Android Studio Arctic Fox or later
- XAMPP (Apache + MySQL + PHP 8.2+)
- Physical Android device or emulator
- Java JDK 11+

### Setup in 3 Steps

#### 1. Start Backend (XAMPP)
```bash
# Start Apache and MySQL servers in XAMPP
# Import database schema from: backend/database/schema.sql
# Access PhpMyAdmin: http://localhost/phpmyadmin
```

#### 2. Configure Network
```bash
# Find your Mac's local IP
ifconfig | grep "inet " | grep -v 127.0.0.1

# Update in app code:
# app/src/main/java/com/example/smartmeal/data/api/RetrofitClient.kt
# Change BASE_URL to: http://YOUR_IP/smartmeal/backend/api/
```

#### 3. Build & Run
```bash
# Open in Android Studio
# Build > Rebuild Project
# Run > Run 'app'
```

---

## 🎯 Current Status

**✅ Completed (85%):**
- Authentication (Login/Signup)
- Home Screen with Recipe Suggestions
- Pantry Management (Full CRUD)
- Shopping List (Full CRUD)
- Meal Planner (Add meals)
- Recipe List & Details
- PHP/MySQL Backend APIs

**🚨 Critical Missing (15%):**
- Local Storage (Room Database)
- Data Synchronization
- Image Upload/Download
- Push Notifications

**See [PROJECT_STATUS.md](PROJECT_STATUS.md) for complete checklist.**

---

## 📱 Features

- ✅ Browse 30+ recipes with search/filter
- ✅ Manage pantry ingredients
- ✅ Plan weekly meals (7 days)
- ✅ Auto-generate shopping lists
- ✅ Pantry-based recipe suggestions (Wow Factor!)
- ✅ Works with PHP/MySQL backend
- ⚠️ Offline mode (in progress)
- ⚠️ Push notifications (in progress)

---

## 🛠️ Tech Stack

**Frontend:** Kotlin, Material Design 3, Retrofit, Room (planned), FCM  
**Backend:** PHP 8.2, MySQL 8.0, Apache  
**Architecture:** MVVM with Repository Pattern

---

## 📞 Testing

```bash
# Test backend connectivity
./test_connectivity.sh

# Test PHP APIs
./test_backend.sh
```

---

## 📄 License

Academic project for educational purposes.

**Last Updated:** December 7, 2025

