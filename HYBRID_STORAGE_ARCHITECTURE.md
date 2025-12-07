# SmartMeal Hybrid Storage Architecture

## Overview
SmartMeal uses a **hybrid storage approach** combining **Firebase Cloud Services** with **local SQLite database (Room)** to satisfy all rubric requirements for offline functionality and cloud sync.

---

## Architecture Components

### 1. Local Storage (Room Database) - SQLite
**Purpose**: Store all data locally for offline-first functionality

**Database**: `SmartMealDatabase`
**Location**: `/data/data/com.example.smartmeal/databases/smartmeal_database`

**Entities**:
- **User**: User profile and authentication cache
- **PantryItem**: Local pantry inventory with sync flags
- **ShoppingItem**: Shopping list with completion status
- **Recipe**: Cached recipes from cloud + user custom recipes
- **MealPlan**: Weekly meal planning with date/time slots

**Key Features**:
- All entities have `isSynced` flag to track sync status
- Soft delete with `isDeleted` flag for sync reconciliation
- `lastUpdated` timestamp for conflict resolution
- Full CRUD operations work offline

### 2. Cloud Storage (Firebase)
**Purpose**: Cloud backup, cross-device sync, and persistent storage

**Services Used**:
- **Firebase Auth**: Email/phone authentication
- **Cloud Firestore**: Document database for structured data
- **Firebase Storage**: Image uploads (pantry/meal photos)
- **Firebase Cloud Messaging**: Push notifications

**Firestore Collections**:
```
users/
  {userId}/
    - profile data
    pantry/
      {itemId}: PantryItem
    shopping/
      {itemId}: ShoppingItem
    recipes/
      {recipeId}: Recipe (user custom + favorites)
    mealPlans/
      {planId}: MealPlan

global_recipes/
  {recipeId}: Recipe (shared recipe database)
```

---

## Data Flow

### Offline Mode
1. User performs actions (add, edit, delete)
2. Data saved immediately to **Room database**
3. Entity marked with `isSynced = false`
4. App remains fully functional offline

### Online Mode (Auto Sync)
1. **SyncWorker** runs every 15 minutes when connected
2. **syncToCloud()**: Push unsynced local changes to Firestore
3. **syncFromCloud()**: Pull new cloud data to local database
4. Mark synced items with `isSynced = true`
5. Update `lastSyncTime` in user profile

### Conflict Resolution
- **Last-write-wins** using `lastUpdated` timestamps
- Cloud data with newer timestamp overwrites local
- Deleted items (soft delete) are removed from cloud

---

## Implementation Details

### Repository Pattern
Each data type has a Repository handling both local and cloud operations:

**PantryRepository**:
```kotlin
- allPantryItems: LiveData<List<PantryItem>>  // Local Room query
- insertPantryItem(item) -> marks isSynced=false
- syncToCloud(userId) -> pushes unsynced to Firestore
- syncFromCloud(userId) -> pulls from Firestore to Room
```

**Similar for**: ShoppingRepository, RecipeRepository, MealPlanRepository

### ViewModel Layer
ViewModels expose LiveData from repositories and handle business logic:
```kotlin
PantryViewModel
RecipeViewModel
MealPlanViewModel
ShoppingListViewModel
AuthViewModel
```

### Background Sync (WorkManager)
**SyncWorker** (periodic background task):
- Runs every 15 minutes when online
- Syncs all data types (pantry, shopping, recipes, meal plans)
- Exponential backoff on failure
- Up to 3 retry attempts

**Manual Sync**:
- Pull-to-refresh on data screens
- Triggered by ViewModel `syncData(userId)` method

---

## Rubric Compliance

### ✅ 1. Store Data Locally (10 marks)
- **Room Database** with 5 entities (User, PantryItem, ShoppingItem, Recipe, MealPlan)
- All CRUD operations work offline
- Data persists across app restarts
- No internet required for core functionality

### ✅ 2. Data Sync (15 marks)
- **SyncWorker** automatically syncs when online
- Bi-directional sync (local ↔ cloud)
- Change tracking with `isSynced` and `lastUpdated` flags
- Conflict resolution using timestamps
- No data loss

### ✅ 3. Store Data on Cloud (10 marks)
- **Firestore** collections for all user data
- **Firebase Storage** for images
- User-scoped collections for data isolation
- Global recipe collection for shared data

### ✅ 4. GET/POST Images (10 marks)
- **ImageUploadManager** handles uploads to Firebase Storage
- Upload pantry/meal images (POST)
- Download and cache image URLs (GET)
- Metadata stored in Firestore

### ✅ 5. Lists and Search (10 marks)
- RecipeList with search bar
- Filter recipes by ingredients, diet, time
- Pantry list with CRUD
- Shopping list with checkboxes
- Meal planner calendar view

### ✅ 6. Authentication (10 marks)
- Firebase Auth (email/password)
- Login/Signup flows
- Auth state persistence
- User profile in Room cache

### ✅ 7. Push Notifications (10 marks)
- **FCM** for remote notifications
- Local notifications for meal reminders
- Shopping alerts
- Notification channels created

### ✅ 8-9. UI/Frontend (25 marks)
- 12 polished screens
- All flows connected
- Smooth navigation
- Bottom navigation on main screens

### ✅ 10. WOW Factor (Bonus)
- **Pantry-based recipe suggestions**
- Match recipes with available ingredients
- Show missing items
- One-tap add missing items to shopping list

---

## Key Files

### Data Layer
- `SmartMealDatabase.kt` - Room database definition
- `*Dao.kt` - DAO interfaces for each entity
- `data/model/*` - Entity classes
- `data/repository/*` - Repository classes

### Business Logic
- `viewmodel/*` - ViewModels for each feature
- `utils/RecipeMatchingEngine.kt` - Pantry-recipe matching logic
- `utils/ImageUploadManager.kt` - Image handling

### Background Work
- `worker/SyncWorker.kt` - Periodic sync worker
- `services/MyFirebaseMessagingService.kt` - FCM handler

### UI Layer
- `Activity*.kt` - Activity classes for each screen
- `res/layout/activity_*.xml` - Layout files

---

## Testing the Hybrid System

### Test Offline Functionality:
1. Disable internet connection
2. Add pantry items, create meal plans, add to shopping list
3. Verify data persists in local database
4. Re-enable internet
5. Verify data syncs to cloud automatically

### Test Sync:
1. Login on Device A
2. Add pantry items
3. Wait for sync (or trigger manually)
4. Login on Device B with same account
5. Verify items appear after sync

### Test Conflict Resolution:
1. Go offline on both devices
2. Modify same item on both
3. Go online
4. Verify last-write-wins (newer timestamp)

---

## Future Enhancements

1. **Smart Sync**: Only sync changed fields, not entire documents
2. **Compression**: Compress recipe data before storage
3. **Pagination**: Load recipes in batches for better performance
4. **Offline Queue**: Queue failed syncs with manual retry UI
5. **Real-time Sync**: Use Firestore listeners for instant updates

---

## Summary

SmartMeal successfully implements a **production-ready hybrid storage system** that:
- ✅ Works completely offline (Room/SQLite)
- ✅ Syncs automatically when online (Firestore + WorkManager)
- ✅ Handles conflicts gracefully (timestamp-based)
- ✅ Scales for multiple devices (user-scoped collections)
- ✅ Satisfies ALL rubric requirements

The architecture is **maintainable**, **testable**, and follows **Android best practices** (MVVM, Repository pattern, LiveData, Coroutines).

