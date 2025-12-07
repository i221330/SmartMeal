# SmartMeal - AI-Assisted Recipe & Meal Planner
## Mobile Application for Software for Mobile Devices Course

---

## 📱 Project Overview

**SmartMeal** is an Android mobile application that helps users manage their pantry, plan weekly meals, maintain shopping lists, and discover recipes based on available ingredients. The app features a rule-based "AI assistant" that suggests recipes by matching pantry items with recipe requirements.

### Key Features:
- **Pantry Management**: Track ingredients you have at home
- **Recipe Discovery**: Browse 30+ recipes with filtering and search
- **Meal Planning**: Plan meals for the entire week (Breakfast, Lunch, Dinner)
- **Smart Shopping List**: Auto-generated from meal plans, syncs with pantry
- **Recipe Suggestions**: Get recipe recommendations based on pantry contents
- **Image Upload**: Upload and display meal/ingredient images
- **Offline Support**: Works without internet, syncs when connected
- **Push Notifications**: Reminders for cooking times and shopping

---

## 🏗️ Architecture

### Technology Stack:

**Frontend (Mobile App):**
- Platform: Native Android (Kotlin)
- UI: Material Design 3 Components
- Local Storage: SQLite + Room Database
- Networking: Retrofit + OkHttp
- Image Loading: Glide
- Push Notifications: Firebase Cloud Messaging (FCM)
- Architecture: MVVM with Repository Pattern

**Backend (Server):**
- Server: PHP 8.2.4
- Database: MySQL 8.0
- Web Server: Apache (XAMPP)
- Authentication: PHP Sessions + Password Hashing (bcrypt)
- API Format: RESTful JSON APIs
- Image Storage: Local filesystem (htdocs/images/)

**Development Environment:**
- IDE: Android Studio
- Local Server: XAMPP on macOS
- Testing: Physical Android device (connected via WiFi)

---

## 📊 Implementation Approach

### Hybrid Local-First Architecture:

1. **Local-First Data Storage**:
   - All user data stored in SQLite database on device
   - App works fully offline
   - Fast, responsive user experience

2. **Cloud Backup & Sync**:
   - Data syncs with PHP/MySQL backend when online
   - Automatic sync on app start and after major actions
   - Conflict resolution: Server has priority

3. **API Communication**:
   - RESTful endpoints for all CRUD operations
   - JSON request/response format
   - Token-based authentication

---

## ✅ What Has Been Completed

### 1. Backend Infrastructure (100% Complete) ✅

#### PHP APIs Implemented:
- ✅ `users.php` - Signup, Login, User Management
- ✅ `ingredients.php` - Master ingredient list (for pantry/shopping)
- ✅ `pantry.php` - Full CRUD for user pantry items
- ✅ `shopping_list.php` - Full CRUD for shopping list
- ✅ `meal_planner.php` - Add/Get/Delete meals with shopping list integration
- ✅ `recipes.php` - Recipe browsing, search, pantry-based suggestions
- ✅ `home.php` - Today's meals and recipe recommendations

#### Database Schema:
- ✅ `users` table - User accounts with password hashing
- ✅ `ingredients` table - 50+ predefined ingredients
- ✅ `user_pantry` table - User-owned ingredients
- ✅ `shopping_list` table - Shopping items with pantry status
- ✅ `meal_plans` table - Weekly meal planning
- ✅ `recipes` table - 30 complete recipes with instructions
- ✅ `recipe_ingredients` table - Recipe-ingredient relationships

#### Backend Features:
- ✅ Password hashing with bcrypt
- ✅ Authentication via user tokens
- ✅ CORS headers for cross-origin requests
- ✅ Comprehensive error handling
- ✅ Detailed debug logging
- ✅ SQL injection prevention (prepared statements)

### 2. Authentication Flow (100% Complete) ✅

- ✅ **Splash Screen**: Animated logo with auto-navigation
- ✅ **Login Screen**: Email/password authentication
- ✅ **Signup Screen**: Full name, email, password with validation
- ✅ **Onboarding Screens**: 3 tutorial screens after signup
- ✅ **Session Management**: Token stored in SharedPreferences
- ✅ **Backend Integration**: PHP authentication with password hashing

**Flow**: Splash → Login ↔ Signup → Onboarding (3 screens) → Home

### 3. Home Screen (100% Complete) ✅

- ✅ **Today's Meals Display**: Shows breakfast, lunch, dinner for current date
- ✅ **Recipe Suggestions**: 5 recipe cards based on pantry match percentage
- ✅ **Empty State Handling**: Prompts when no meals planned
- ✅ **Quick Actions**: Navigate to Recipe List, Learn More, AI Assistant
- ✅ **Bottom Navigation**: Home, Pantry, Planner, Shopping, Profile
- ✅ **Real-time Data**: Loads from backend on each visit
- ✅ **Match Percentage**: Shows % of ingredients user has for each recipe

### 4. Pantry Management (100% Complete) ✅

- ✅ **View Pantry**: List all items with quantities
- ✅ **Add Items**: Search from master ingredient list with autocomplete
- ✅ **Update Quantity**: Edit existing item quantities
- ✅ **Delete Items**: Remove items with confirmation
- ✅ **Search/Filter**: Real-time search in pantry
- ✅ **Themed Dialogs**: Custom styled add/edit/delete dialogs
- ✅ **Backend Sync**: All operations sync with PHP API
- ✅ **Empty State**: Helpful message when pantry is empty

### 5. Shopping List (100% Complete) ✅

- ✅ **View List**: All items with quantities and pantry status
- ✅ **Auto-Add from Meals**: Ingredients added when meal is planned
- ✅ **Manual Add**: Add custom items with autocomplete dropdown
- ✅ **Mark as Purchased**: Check off items → auto-adds to pantry
- ✅ **Delete Items**: Remove unwanted items
- ✅ **Search/Filter**: Input field at top with real-time filtering
- ✅ **Autocomplete**: Dropdown suggestions from master ingredient list
- ✅ **Quantity Adjustment**: Confirm quantities before purchasing
- ✅ **Backend Sync**: Syncs with PHP API

### 6. Meal Planner (90% Complete) ⚠️

- ✅ **Weekly View**: 7 days (Sun-Sat) with day selection
- ✅ **Current Day Highlight**: Automatically selects today
- ✅ **Add Meals**: Select recipe for breakfast/lunch/dinner
- ✅ **View Planned Meals**: Shows meal cards per day/type
- ✅ **Date Navigation**: Toggle between days of the week
- ✅ **Shopping List Integration**: Ingredients auto-add to shopping list
- ✅ **Backend Sync**: Saves to PHP/MySQL
- ⚠️ **Missing**: Delete meal functionality
- ⚠️ **Missing**: Edit meal functionality

### 7. Recipe Features (90% Complete) ⚠️

- ✅ **Recipe List**: Grid view of 30 recipes
- ✅ **Recipe Details**: Full recipe info, ingredients, instructions
- ✅ **Search**: Real-time recipe search
- ✅ **Filter Chips**: Filter by cuisine, difficulty, diet
- ✅ **Add to Meal Plan**: Dialog to select day and meal type
- ✅ **Ingredient Matching**: Shows which ingredients user has/needs
- ✅ **Missing Ingredients**: List with "Add to Shopping List" button
- ✅ **Backend Integration**: Loads from recipes.php
- ⚠️ **Missing**: "Mark as Cooked" functionality (subtract ingredients)
- ⚠️ **Missing**: Recipe rating/favorites

### 8. Navigation & UI (95% Complete) ✅

- ✅ **Bottom Navigation**: 5 tabs (Home, Pantry, Planner, Shopping, Profile)
- ✅ **Consistent Theme**: Material Design 3, dark green color scheme
- ✅ **Custom Dialogs**: Themed confirmation/input dialogs
- ✅ **Loading States**: Progress indicators during API calls
- ✅ **Empty States**: Helpful messages when no data
- ✅ **Error Handling**: Toast messages for errors
- ✅ **Responsive Layouts**: ScrollViews for long content
- ⚠️ **Learn More Screen**: Created but needs content polish
- ⚠️ **AI Assistant Screen**: Placeholder only, not functional

### 9. Profile & Settings (50% Complete) ⚠️

- ✅ **Profile Screen**: Basic layout exists
- ✅ **Logout**: Functional logout button
- ✅ **Navigation**: Accessible from bottom nav
- ⚠️ **Missing**: Display user info (name, email)
- ⚠️ **Missing**: Edit profile functionality
- ⚠️ **Missing**: Settings options
- ⚠️ **Missing**: About/Help sections

---

## 🔴 What's Left To Complete

### Critical (Must Complete for Demo):

#### 1. Local Storage Implementation (0% Complete) 🚨
**Status**: NOT STARTED - CRITICAL  
**Rubric Requirement**: 10 marks

**What's Needed:**
- Create Room Database schema matching backend
- Implement DAOs (Data Access Objects) for all tables
- Store all user data locally:
  - User profile
  - Pantry items
  - Shopping list
  - Meal plans
  - Cached recipes
- App must work completely offline
- Show cached data when no internet

**Files to Create:**
- `database/AppDatabase.kt`
- `database/dao/PantryDao.kt`
- `database/dao/ShoppingListDao.kt`
- `database/dao/MealPlanDao.kt`
- `database/dao/RecipeDao.kt`
- `database/entities/` (all entity classes)

**Estimated Time**: 8-10 hours

---

#### 2. Data Sync Implementation (0% Complete) 🚨
**Status**: NOT STARTED - CRITICAL  
**Rubric Requirement**: 15 marks (highest weight!)

**What's Needed:**
- Implement sync logic in all repositories
- Handle offline changes (queue operations)
- Sync on app start and when connectivity returns
- Conflict resolution (server wins)
- Timestamp-based sync strategy
- Handle scenarios:
  - Local changes while offline → push to server
  - Server changes → pull to local
  - Prevent duplicate records
  - Handle deletions properly

**Sync Strategy:**
```
1. Check internet connectivity
2. If online:
   - Push local changes (where local_timestamp > last_sync)
   - Pull server changes (where server_timestamp > last_sync)
   - Update last_sync timestamp
3. If offline:
   - Queue changes locally
   - Mark for sync later
```

**Files to Modify:**
- All Repository classes
- Add `SyncManager.kt`
- Add connectivity listener

**Estimated Time**: 10-12 hours

---

#### 3. Image Upload/Download (0% Complete) 🚨
**Status**: NOT STARTED - CRITICAL  
**Rubric Requirement**: 10 marks

**What's Needed:**
- Image picker for meals/pantry items
- Camera integration (optional)
- Upload images to PHP backend (multipart/form-data)
- Store images in `htdocs/images/` folder
- Return image URLs from API
- Display images with Glide library
- Compress images before upload

**Backend (PHP):**
- Create `upload.php` endpoint
- Handle multipart form data
- Save to local folder
- Return public URL

**Frontend (Kotlin):**
- Image picker activity
- Retrofit multipart upload
- Display in recipe detail/pantry

**Estimated Time**: 6-8 hours

---

#### 4. Push Notifications (0% Complete) 🚨
**Status**: NOT STARTED - CRITICAL  
**Rubric Requirement**: 10 marks

**What's Needed:**
- Integrate Firebase Cloud Messaging (FCM)
- Create notification channels
- Implement notification types:
  - **Cooking Reminders**: "Time to cook [meal name]!"
  - **Shopping Reminders**: "Don't forget to shop for [ingredients]"
- Schedule notifications based on meal plan
- Handle notification clicks
- Background notification service

**Files Needed:**
- Update `google-services.json` (already exists)
- `MyFirebaseMessagingService.kt` (already exists, needs work)
- `NotificationHelper.kt`
- Schedule logic in meal planner

**Estimated Time**: 5-6 hours

---

### Important (Should Complete):

#### 5. Complete Meal Planner CRUD (30% Complete) ⚠️
**Current**: Can add meals  
**Missing**: Delete meals, Edit meals

**What's Needed:**
- Delete button on meal cards
- Confirmation dialog for deletion
- Backend DELETE endpoint
- Edit meal functionality (optional)

**Estimated Time**: 2-3 hours

---

#### 6. Recipe "Mark as Cooked" Feature (0% Complete) ⚠️
**What's Needed:**
- "I Made This" button in recipe detail
- Subtract ingredients from pantry
- Update pantry quantities
- Backend endpoint for ingredient subtraction

**Estimated Time**: 3-4 hours

---

#### 7. AI Assistant Screen (0% Complete) ⚠️
**Current**: Placeholder screen only  
**What's Needed:**
- Rule-based recipe filter UI
- Input fields: cooking time, cuisine, diet, ingredients
- "Find Recipes" button
- Results list with match scores
- Reuse recipe list adapter

**Estimated Time**: 4-5 hours

---

#### 8. Profile Completion (50% Complete) ⚠️
**What's Needed:**
- Display user name and email
- Edit profile functionality
- Change password
- App settings (optional)

**Estimated Time**: 2-3 hours

---

### Nice to Have (Polish):

#### 9. Favorites/Ratings
- Save favorite recipes
- Rate recipes
- Filter by favorites

#### 10. Recipe Search Improvements
- Advanced filters
- Sort by match percentage
- Recent searches

#### 11. UI Polish
- Animations and transitions
- Better loading states
- Improved empty states
- Help tooltips

---

## 📋 Rubric Compliance Status

| # | Requirement | Status | Marks | Notes |
|---|-------------|--------|-------|-------|
| 1 | Complete Features | 🟡 80% | 0/Pass | Most features done, needs polish |
| 2 | **Local Storage** | 🔴 0% | **0/10** | **NOT STARTED - CRITICAL** |
| 3 | **Data Sync** | 🔴 0% | **0/15** | **NOT STARTED - CRITICAL** |
| 4 | Cloud/Server Storage | 🟢 100% | 10/10 | PHP APIs + MySQL complete |
| 5 | **Image Upload/Download** | 🔴 0% | **0/10** | **NOT STARTED - CRITICAL** |
| 6 | Lists & Search | 🟢 100% | 10/10 | All screens have lists + search |
| 7 | Signup/Login Auth | 🟢 100% | 10/10 | Complete with PHP backend |
| 8 | **Push Notifications** | 🔴 0% | **0/10** | **NOT STARTED - CRITICAL** |
| 9 | UI Screens | 🟢 95% | 9/10 | 12 screens implemented |
| 10 | Frontend Functional | 🟡 85% | 12/15 | Works but missing features |
| 11 | Wow Factor (Bonus) | 🟢 100% | 10/10 | Pantry matching implemented |

**Current Score**: ~61/100 (without 4 critical features)  
**Potential Score**: 96/100 (if all features complete)

---

## 🚨 Priority Action Items

### CRITICAL (Must Do Before Demo):

1. **Implement Local Storage** (2-3 days)
   - Create Room Database
   - Implement all DAOs
   - Store data locally
   - Make app work offline

2. **Implement Data Sync** (2-3 days)
   - Sync manager
   - Queue offline changes
   - Bi-directional sync
   - Conflict resolution

3. **Image Upload/Download** (1-2 days)
   - PHP upload endpoint
   - Image picker in app
   - Display images

4. **Push Notifications** (1 day)
   - FCM integration
   - Notification scheduling
   - Handle notification clicks

**Total Estimated Time**: 6-9 days of focused work

---

## 📁 Project Structure

```
SmartMeal/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/smartmeal/
│   │   │   ├── Activities (12 screens)
│   │   │   ├── adapters/ (RecyclerView adapters)
│   │   │   ├── data/
│   │   │   │   ├── api/ (Retrofit services)
│   │   │   │   └── repository/ (Data layer)
│   │   │   ├── database/ (⚠️ TO BE CREATED)
│   │   │   ├── services/ (FCM service)
│   │   │   └── viewmodel/ (MVVM)
│   │   └── res/
│   │       ├── layout/ (XML layouts)
│   │       ├── values/ (colors, strings, themes)
│   │       └── drawable/ (icons, images)
│   └── build.gradle.kts
├── backend/
│   ├── api/
│   │   ├── users.php ✅
│   │   ├── pantry.php ✅
│   │   ├── shopping_list.php ✅
│   │   ├── meal_planner.php ✅
│   │   ├── recipes.php ✅
│   │   ├── ingredients.php ✅
│   │   ├── home.php ✅
│   │   └── upload.php (⚠️ TO BE CREATED)
│   ├── config/
│   │   └── database.php ✅
│   └── database/
│       └── schema.sql ✅
└── PROJECT_STATUS.md (this file)
```

---

## 🔧 Technical Details

### API Endpoints (Base URL: `http://192.168.1.4/smartmeal/backend/api/`)

**Authentication:**
- `POST users.php?action=register` - Sign up
- `POST users.php?action=login` - Log in

**Pantry:**
- `GET pantry.php?user_id={id}` - Get pantry
- `POST pantry.php` - Add item
- `PUT pantry.php` - Update item
- `DELETE pantry.php?item_id={id}` - Delete item

**Shopping List:**
- `GET shopping_list.php?user_id={id}` - Get list
- `POST shopping_list.php` - Add item
- `PUT shopping_list.php` - Mark purchased (adds to pantry)
- `DELETE shopping_list.php?item_id={id}` - Delete item

**Meal Planner:**
- `GET meal_planner.php?user_id={id}&date={date}` - Get meals for date
- `POST meal_planner.php` - Add meal (auto-adds ingredients to shopping)
- `DELETE meal_planner.php?plan_id={id}` - Delete meal

**Recipes:**
- `GET recipes.php` - Get all recipes
- `GET recipes.php?action=suggestions&user_id={id}` - Get pantry-based suggestions
- `GET recipes.php?search={query}` - Search recipes

**Home:**
- `GET home.php?user_id={id}` - Get today's meals + suggestions

**Ingredients:**
- `GET ingredients.php` - Get master ingredient list

---

## 🎯 Demo Flow Plan

1. **Launch App** → Splash screen
2. **Login** → Show login screen
3. **Home Screen** → Today's meals + recipe suggestions
4. **Pantry** → Add some ingredients (bread, eggs, milk)
5. **Recipe Suggestions** → Show updated match percentages
6. **Meal Planner** → Add recipe to today's dinner
7. **Shopping List** → Auto-added ingredients appear
8. **Mark Purchased** → Items move to pantry
9. **Offline Mode** → Toggle airplane mode, show app still works
10. **Online Sync** → Turn on internet, data syncs
11. **Push Notification** → Show scheduled notification

---

## 📞 Contact & Submission

**Student**: Daniyal Khawar  
**Course**: Software for Mobile Devices  
**University**: [Your University]  
**Submission Date**: [To Be Determined]

---

## 🎓 Learning Outcomes Achieved

✅ Native Android development with Kotlin  
✅ Material Design 3 implementation  
✅ RESTful API design and consumption  
✅ PHP backend development  
✅ MySQL database design  
✅ MVVM architecture pattern  
✅ Retrofit networking  
✅ RecyclerView with custom adapters  
✅ Coroutines for async operations  
✅ User authentication  
⚠️ Local database (Room) - In Progress  
⚠️ Data synchronization - In Progress  
⚠️ Push notifications - In Progress  
⚠️ Image upload/download - In Progress

---

**Last Updated**: December 7, 2025  
**Version**: 1.0-alpha  
**Build Status**: Compiling Successfully ✅  
**Demo Ready**: 60% (4 critical features remaining)

