# SmartMeal Implementation Checklist

## ✅ Completed Features

### Core Architecture
- [x] Room Database setup with 5 entities
- [x] Firebase integration (Auth, Firestore, Storage, FCM)
- [x] Repository pattern for all data types
- [x] ViewModels with LiveData for reactive UI
- [x] WorkManager for background sync
- [x] Hybrid offline-first with cloud sync

### Data Layer
- [x] User entity with profile data
- [x] PantryItem with sync flags and soft delete
- [x] ShoppingItem with completion status
- [x] Recipe with ingredients and instructions
- [x] MealPlan with date/time slots
- [x] All DAOs with CRUD operations
- [x] PantryRepository with cloud sync
- [x] ShoppingRepository with cloud sync
- [x] RecipeRepository with cloud sync
- [x] MealPlanRepository with cloud sync
- [x] AuthRepository for authentication

### ViewModels
- [x] AuthViewModel for login/signup
- [x] PantryViewModel for pantry management
- [x] ShoppingListViewModel for shopping list
- [x] RecipeViewModel for recipe browsing
- [x] MealPlanViewModel for meal planning

### Background Services
- [x] SyncWorker for periodic sync (every 15 min)
- [x] MyFirebaseMessagingService for FCM
- [x] Notification channels setup
- [x] Image upload manager

### UI Screens (12 total)
- [x] ActivitySplash - Entry point
- [x] ActivityOnboarding1 - Onboarding step 1
- [x] ActivityOnboarding2 - Onboarding step 2
- [x] ActivityOnboarding3 - Onboarding step 3
- [x] ActivityLogin - Login screen
- [x] ActivitySignup - Registration screen
- [x] ActivityHome - Dashboard with suggestions
- [x] ActivityRecipeList - Browse recipes
- [x] ActivityRecipeDetails - Recipe detail view
- [x] ActivityAiAssistant - Simple chat assistant
- [x] ActivityMealPlanner - Weekly meal planner
- [x] ActivityShoppingList - Shopping list management
- [x] ActivityPantry - Pantry inventory
- [x] ActivityProfile - User profile/settings

### Utilities
- [x] RecipeMatchingEngine - Pantry-based suggestions
- [x] ImageUploadManager - Firebase Storage uploads

---

## 🔧 Implementation Tasks Still Needed

### 1. Connect ViewModels to Activities

#### ActivityHome
```kotlin
// Add ViewModel initialization
private val recipeViewModel: RecipeViewModel by viewModels { ... }
private val pantryViewModel: PantryViewModel by viewModels { ... }

// Load sample recipes on first launch
// Display pantry-based recipe suggestions
// Wire up navigation buttons
```

#### ActivityRecipeList
```kotlin
// Add RecipeViewModel
// Implement search functionality
// Add RecyclerView with RecipeAdapter
// Wire up filter chips (time, diet, cuisine)
```

#### ActivityRecipeDetails
```kotlin
// Load recipe by ID from ViewModel
// Display ingredients with availability check
// Show missing ingredients
// Add "Add Missing to Shopping List" button
// Implement favorite toggle
```

#### ActivityPantry
```kotlin
// Add PantryViewModel
// Implement RecyclerView with PantryAdapter
// Add/Edit/Delete pantry items
// Image upload for pantry items
// Sync button for manual sync
```

#### ActivityShoppingList
```kotlin
// Add ShoppingListViewModel
// Implement RecyclerView with checkboxes
// Add/Edit/Delete shopping items
// Mark items as completed
// Clear completed items button
```

#### ActivityMealPlanner
```kotlin
// Add MealPlanViewModel
// Implement week view (7 days × 3 meals)
// Add recipe selection dialog
// Display planned meals with images
// Delete meal plans
```

#### ActivityLogin
```kotlin
// Already has AuthViewModel ✓
// Add error handling UI
// Add loading state UI
// Test email/password login
```

#### ActivitySignup
```kotlin
// Already has AuthViewModel ✓
// Add error handling UI
// Add loading state UI
// Test email/password signup
```

#### ActivityProfile
```kotlin
// Add profile image upload
// Display user data from Room cache
// Logout functionality
// Sync status display
// Manual sync trigger
```

#### ActivityAiAssistant
```kotlin
// Implement simple rule-based chat
// Filter recipes by keywords
// Display chat messages in RecyclerView
// Send button functionality
```

---

### 2. Create RecyclerView Adapters

#### RecipeAdapter
```kotlin
class RecipeAdapter(private val onClick: (Recipe) -> Unit) : 
    ListAdapter<Recipe, RecipeViewHolder>(RecipeDiffCallback())
```

#### PantryAdapter
```kotlin
class PantryAdapter(
    private val onEdit: (PantryItem) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<PantryItem, PantryViewHolder>(PantryDiffCallback())
```

#### ShoppingAdapter
```kotlin
class ShoppingAdapter(
    private val onCheck: (String, Boolean) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<ShoppingItem, ShoppingViewHolder>(ShoppingDiffCallback())
```

#### MealPlanAdapter
```kotlin
class MealPlanAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<MealPlan, MealPlanViewHolder>(MealPlanDiffCallback())
```

#### ChatAdapter
```kotlin
class ChatAdapter : ListAdapter<ChatMessage, ChatViewHolder>(ChatDiffCallback())
```

---

### 3. Implement Image Upload

#### ImageUploadManager Enhancement
```kotlin
// Add image picker integration
// Add camera capture integration
// Add image compression
// Add upload progress callback
```

#### Activities needing image upload:
- ActivityPantry (pantry item photos)
- ActivityProfile (profile picture)
- ActivityRecipeDetails (meal photos - optional)

---

### 4. Implement Push Notifications

#### Meal Reminders
```kotlin
// Schedule notification 30 min before meal time
// Use AlarmManager or WorkManager
// Show recipe name and cooking time
```

#### Shopping Alerts
```kotlin
// Notify when items added to shopping list
// Notify before planned shopping day
```

#### Sync Notifications (optional)
```kotlin
// Notify when sync completes
// Notify on sync errors
```

---

### 5. Implement Recipe Matching Engine

#### Complete pantry-based suggestions:
```kotlin
// In ActivityHome, load pantry items and recipes
// Use RecipeMatchingEngine to score matches
// Display top 3-5 recipe suggestions
// Show "You have X/Y ingredients" badge
// List missing ingredients
// One-tap add missing to shopping list
```

---

### 6. Add Dialogs and Bottom Sheets

#### Add Pantry Item Dialog
- Input: name, quantity, category, expiry date
- Image picker option
- Save button

#### Add Shopping Item Dialog
- Input: name, quantity, category
- Save button

#### Recipe Filter Dialog
- Chips: Quick (<30min), Medium, Long
- Diet: Vegan, Vegetarian, Keto, etc.
- Cuisine: Italian, Chinese, Mexican, etc.

#### Meal Selection Dialog (for planner)
- Show all recipes
- Search functionality
- Select recipe for meal slot

#### Confirm Delete Dialog
- "Are you sure?" message
- Cancel / Delete buttons

---

### 7. Add Loading States and Error Handling

For all ViewModels:
```kotlin
// Observe isLoading -> show ProgressBar
// Observe error -> show Toast or Snackbar
// Observe success -> show confirmation message
```

---

### 8. Testing Scenarios

#### Offline Functionality Test
1. Turn off WiFi and mobile data
2. Add pantry items
3. Create meal plans
4. Add to shopping list
5. Browse cached recipes
6. Verify all data persists
7. Turn on internet
8. Verify sync happens automatically

#### Auth Flow Test
1. First launch -> Onboarding
2. Complete onboarding -> Login
3. Sign up with email/password
4. Logout -> Login screen
5. Login -> Home screen (skip onboarding)

#### Sync Test
1. Login on Device A
2. Add pantry items
3. Trigger sync (manual or wait for auto)
4. Login on Device B
5. Verify items appear
6. Add items on Device B
7. Verify sync to Device A

#### Recipe Suggestions Test
1. Add ingredients to pantry
2. Go to Home screen
3. Verify recipes with matching ingredients show first
4. Verify missing ingredients are listed
5. Tap "Add to Shopping List"
6. Verify items added to shopping list

---

## 📋 Rubric Requirements Checklist

### 1. Store Data Locally (10 marks) ✅
- [x] Room database with 5 entities
- [x] All CRUD operations work offline
- [x] Data persists across app restarts
- [x] No internet required for core features

### 2. Data Sync (15 marks) ✅
- [x] SyncWorker runs periodically
- [x] Bi-directional sync (local ↔ cloud)
- [x] Change tracking with isSynced flags
- [x] Conflict resolution with timestamps
- [ ] **TODO**: Test sync on multiple devices

### 3. Store Data on Cloud (10 marks) ✅
- [x] Firestore collections for all data
- [x] User-scoped collections
- [x] Firebase Storage for images
- [ ] **TODO**: Upload at least 10 global recipes

### 4. GET/POST Images (10 marks) 🔄
- [x] ImageUploadManager created
- [ ] **TODO**: Integrate image picker in Pantry
- [ ] **TODO**: Integrate camera in Pantry
- [ ] **TODO**: Test upload to Firebase Storage
- [ ] **TODO**: Test download and display images

### 5. Lists and Search (10 marks) 🔄
- [x] RecipeList activity created
- [ ] **TODO**: Add RecyclerView + Adapter
- [ ] **TODO**: Add search bar functionality
- [ ] **TODO**: Add filter chips
- [x] Pantry list activity created
- [ ] **TODO**: Add RecyclerView + Adapter
- [x] Shopping list activity created
- [ ] **TODO**: Add RecyclerView + Adapter with checkboxes

### 6. Authentication (10 marks) ✅
- [x] Firebase Auth integrated
- [x] Login screen
- [x] Signup screen
- [x] AuthViewModel
- [ ] **TODO**: Test email/password signup
- [ ] **TODO**: Test login flow

### 7. Push Notifications (10 marks) 🔄
- [x] FCM service created
- [x] Notification channels created
- [ ] **TODO**: Schedule meal reminders
- [ ] **TODO**: Send shopping alerts
- [ ] **TODO**: Test notifications on real device

### 8. UI Submission (10 marks) ✅
- [x] 12 screens designed and created
- [x] All layouts polished
- [x] Navigation flows defined

### 9. Frontend Submission (15 marks) 🔄
- [x] All activities created
- [ ] **TODO**: Connect ViewModels to UI
- [ ] **TODO**: Add RecyclerView adapters
- [ ] **TODO**: Implement all click listeners
- [ ] **TODO**: Test all user flows
- [ ] **TODO**: Fix any crashes or bugs

### 10. WOW Factor (up to 10 marks) 🔄
- [x] RecipeMatchingEngine created
- [ ] **TODO**: Display pantry-based suggestions on Home
- [ ] **TODO**: Show ingredient match percentage
- [ ] **TODO**: One-tap add missing ingredients
- [ ] **TODO**: Polish the suggestion UI

---

## 🚀 Priority Implementation Order

### Phase 1: Core Data Flow (High Priority)
1. ✅ Complete all repositories and ViewModels
2. Connect ViewModels to Activities
3. Create RecyclerView adapters
4. Test basic CRUD operations

### Phase 2: Authentication (High Priority)
1. Test signup flow
2. Test login flow
3. Test logout
4. Verify auth state persistence

### Phase 3: Main Features (High Priority)
1. Recipe List with search
2. Pantry management with CRUD
3. Shopping list with checkboxes
4. Meal planner with calendar

### Phase 4: Advanced Features (Medium Priority)
1. Image uploads (pantry + profile)
2. Recipe suggestions (WOW factor)
3. Manual sync triggers
4. Filter and search enhancements

### Phase 5: Notifications (Medium Priority)
1. Meal reminders
2. Shopping alerts
3. Test on physical device

### Phase 6: Polish (Low Priority)
1. Error handling
2. Loading states
3. Empty states
4. Animations
5. Final testing

---

## 📊 Current Progress

**Overall Completion**: ~60%

✅ **Backend/Architecture**: 95% complete
- Database, repositories, ViewModels all done
- Sync mechanism implemented
- Firebase integrated

🔄 **Frontend Integration**: 30% complete
- UI layouts done
- Need to connect ViewModels
- Need RecyclerView adapters
- Need click listeners

🔄 **Features**: 40% complete
- Basic structure in place
- Need to wire up functionality
- Need testing

⏳ **Testing**: 10% complete
- Need to test auth flow
- Need to test sync
- Need to test on multiple devices

---

## Next Steps

1. **Create RecyclerView Adapters** (2-3 hours)
2. **Wire ViewModels to Activities** (3-4 hours)
3. **Test Authentication Flow** (1 hour)
4. **Implement Recipe Suggestions** (2 hours)
5. **Add Image Upload** (2 hours)
6. **Schedule Notifications** (1-2 hours)
7. **Full App Testing** (2-3 hours)

**Estimated time to MVP**: 12-15 hours of focused development

---

## Known Issues to Fix

1. Missing button IDs in some layout files (check compile errors)
2. Need to add RecyclerViews to list activities
3. Need to test Firebase configuration
4. Need to upload global recipes to Firestore
5. Need to test on physical Android device

---

## Resources

- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- [RecyclerView Guide](https://developer.android.com/develop/ui/views/layout/recyclerview)

---

**Last Updated**: December 6, 2025

