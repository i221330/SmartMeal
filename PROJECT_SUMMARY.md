# SmartMeal - AI-Assisted Recipe & Meal Planner

## Project Overview

SmartMeal is a mobile application for Android that helps users manage their recipes, meal planning, pantry inventory, and shopping lists. The app features intelligent recipe suggestions based on available pantry items and provides a complete meal management solution.

## Technology Stack

### Frontend
- **Platform**: Android (Kotlin)
- **UI**: Material Design 3 components
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Storage**: Room Database (SQLite)
- **Networking**: Retrofit + OkHttp
- **Async Operations**: Kotlin Coroutines + Flow

### Backend
- **Server**: Apache (XAMPP)
- **Language**: PHP 8.2.4
- **Database**: MySQL
- **API Style**: RESTful JSON APIs
- **Image Storage**: Local filesystem (htdocs/images/)

## Core Features

### 1. User Authentication
- **Status**: ✅ Complete
- Signup with email and password
- Login with session management
- Password hashing in PHP backend
- User-specific data isolation

### 2. Pantry Management
- **Status**: ✅ Complete
- Add ingredients from master list (autocomplete search)
- Update quantities
- Delete items
- View all pantry items
- Local + server sync

### 3. Shopping List
- **Status**: ✅ Complete
- Add custom items (autocomplete from master ingredients)
- Auto-populate from meal plans (missing ingredients)
- Mark items as purchased → moves to pantry
- Delete items
- Clear All (moves all to pantry)
- Delete All
- Long-press for quick actions
- Local + server sync

### 4. Meal Planner
- **Status**: ✅ Complete
- Weekly view (Sunday - Saturday)
- Add meals to specific days and meal types (Breakfast/Lunch/Dinner)
- Date-aware (today's date highlighted)
- Ingredient confirmation dialog before adding to shopping list
- Navigation from planner → recipe list
- Local + server sync

### 5. Recipe Management
- **Status**: ✅ Complete
- Browse 30 pre-loaded recipes
- Recipe details with ingredients and instructions
- Filter by cuisine, diet, difficulty
- Search by name
- Add to meal plan from recipe list
- Mark as made (deducts ingredients from pantry)
- Local + server sync

### 6. Home Dashboard
- **Status**: ✅ Complete
- Today's meals display (Breakfast, Lunch, Dinner)
- Recipe suggestions based on pantry
- Match percentage calculation
- Missing ingredients display
- Quick actions: Recipe List, Learn More, AI Assistant
- Navigation to all major sections

### 7. Recipe Suggestions (Wow Factor)
- **Status**: ✅ Complete
- Intelligent matching algorithm
- Ranks recipes by pantry ingredient match
- Shows match percentage
- Displays missing ingredients
- Quick add to shopping list
- Prioritizes recipes user can make now

## Implementation Status by Rubric

### 1. Complete Feature Implementation ✅
- All proposed features are fully implemented
- No partial features or placeholder screens
- Full CRUD operations for all entities

### 2. Local Storage (10 marks) ✅
- Room Database implementation
- Offline support for all features
- Local caching of recipes, pantry, shopping list, meal plans
- Works without internet connection

### 3. Data Sync (15 marks) ⚠️ Partial
- **Completed**:
  - Push to server on create/update/delete
  - Pull from server on screen load
  - Timestamp-based change tracking
- **TODO**:
  - Bidirectional conflict resolution
  - Background sync service
  - Sync status indicators
  - Handle network failures gracefully

### 4. Cloud/Server Storage (10 marks) ✅
- MySQL database on XAMPP
- PHP REST APIs for all operations
- User-specific data with user_id filtering
- Full CRUD endpoints for:
  - Users (signup/login)
  - Pantry items
  - Shopping list
  - Meal plans
  - Recipes

### 5. Image GET/POST via Web APIs (10 marks) ❌ Not Started
- **TODO**:
  - Image upload endpoint (multipart/form-data)
  - Storage in htdocs/images/
  - Return image URLs
  - Display images in app
  - Image compression/optimization
  
### 6. Lists and Search (10 marks) ✅
- Recipe List with search and filters
- Pantry list with search
- Shopping List with search
- Meal Planner list view
- AutoComplete for ingredient selection

### 7. Signup + Login (10 marks) ✅
- Secure PHP authentication
- Password hashing (password_hash)
- Session tokens
- User-specific data access
- Login persistence

### 8. Push Notifications (10 marks) ❌ Not Started
- **TODO**:
  - FCM integration
  - Cooking time reminders
  - Shopping reminders
  - Notification scheduling
  - Permission handling

### 9. UI Submission (10 marks) ✅
All screens implemented:
1. Splash Screen ✅
2. Onboarding 1 ✅
3. Onboarding 2 ✅
4. Onboarding 3 ✅
5. Login ✅
6. Signup ✅
7. Home ✅
8. Recipe List ✅
9. Recipe Detail ⚠️ (Needs polish)
10. Meal Planner ✅
11. Shopping List ✅
12. Pantry ✅
13. Learn More ✅
14. Profile/Settings ⚠️ (Basic implementation)
15. AI Assistant ⚠️ (Placeholder)

### 10. Frontend Submission (15 marks) ✅
- Functional and responsive UI
- Material Design 3
- Proper navigation
- Error handling
- Loading states
- Empty states
- Consistent theming

### 11. Wow Factor (10 marks) ✅
**Pantry-Based Recipe Suggestions**:
- Algorithm compares user pantry with recipe ingredients
- Calculates match percentage
- Ranks by best matches
- Shows missing ingredients
- One-click add missing items to shopping list
- Updates dynamically as pantry changes

## Remaining Work

### Critical (Must Complete)
1. **Image Upload/Display System**
   - Recipe images
   - Meal plan images
   - User profile pictures
   - PHP upload endpoint
   - Image storage and retrieval

2. **Push Notifications**
   - FCM setup
   - Notification permissions
   - Scheduling logic
   - Cooking reminders
   - Shopping reminders

3. **Data Sync Improvements**
   - Conflict resolution
   - Background sync worker
   - Offline queue
   - Sync indicators

### High Priority
4. **Recipe Detail Screen Enhancement**
   - Better layout
   - Image display
   - Serving size adjustment
   - Share recipe feature
   - Favorite/bookmark

5. **AI Assistant Implementation**
   - Rule-based recipe filtering
   - Quick suggestions
   - Conversational UI
   - Filter by time/cuisine/diet

6. **Profile/Settings Screen**
   - Edit user info
   - Dietary preferences
   - Notification settings
   - Theme preferences
   - Account management

### Medium Priority
7. **Data Validation**
   - Input validation on all forms
   - Error messages
   - Required field indicators

8. **Performance Optimization**
   - Image caching
   - Lazy loading
   - Database indexing
   - API response caching

9. **Testing**
   - Unit tests for repositories
   - UI tests for critical flows
   - Integration tests

### Nice to Have
10. **Advanced Features**
    - Recipe ratings
    - User reviews
    - Recipe creation
    - Meal plan templates
    - Shopping list sharing
    - Nutrition information

## Database Schema

### MySQL Tables

**users**
- id (PK)
- email (unique)
- password_hash
- display_name
- created_at
- updated_at

**recipes**
- id (PK)
- recipe_id (unique)
- title
- description
- image_url
- prep_time
- cook_time
- servings
- difficulty
- cuisine
- diet_type
- ingredients (JSON)
- instructions (TEXT)
- is_global
- created_by
- last_updated

**pantry_items**
- id (PK)
- user_id (FK)
- item_id (unique per user)
- name
- quantity
- category
- expiry_date
- last_updated
- is_synced
- is_deleted

**shopping_list**
- id (PK)
- user_id (FK)
- item_id (unique per user)
- name
- quantity
- from_recipe_id
- is_checked
- last_updated
- is_synced
- is_deleted

**meal_plans**
- id (PK)
- user_id (FK)
- plan_id (unique per user)
- recipe_id
- recipe_name
- recipe_image_url
- meal_date (timestamp)
- meal_type (breakfast/lunch/dinner)
- notes
- last_updated
- is_synced
- is_deleted

**master_ingredients**
- id (PK)
- name (unique)
- category
- common_unit
- created_at

## API Endpoints

### Authentication
- `POST /api/users.php?action=register` - User signup
- `POST /api/users.php?action=login` - User login

### Pantry
- `GET /api/pantry.php?user_id={id}` - Get user pantry
- `POST /api/pantry.php` - Add item
- `PUT /api/pantry.php` - Update item
- `DELETE /api/pantry.php?item_id={id}` - Delete item

### Shopping List
- `GET /api/shopping.php?user_id={id}` - Get shopping list
- `POST /api/shopping.php` - Add item
- `DELETE /api/shopping.php?item_id={id}` - Delete item

### Recipes
- `GET /api/recipes.php` - Get all recipes
- `GET /api/recipes.php?action=suggestions&user_id={id}` - Get suggestions
- `POST /api/recipes.php?action=mark_made` - Mark recipe as made

### Meal Planner
- `GET /api/meal_planner.php?user_id={id}&date={date}` - Get meals for date
- `POST /api/meal_planner.php` - Add meal to plan
- `DELETE /api/meal_planner.php?plan_id={id}` - Remove meal

### Ingredients
- `GET /api/ingredients.php` - Get all master ingredients
- `GET /api/ingredients.php?search={query}` - Search ingredients

## Local Database (Room)

### Entities
- UserEntity
- RecipeEntity
- PantryItemEntity
- ShoppingItemEntity
- MealPlanEntity
- IngredientEntity

### DAOs
- UserDao
- RecipeDao
- PantryDao
- ShoppingDao
- MealPlanDao
- IngredientDao

## Project Structure

```
app/src/main/java/com/example/smartmeal/
├── data/
│   ├── api/          # Retrofit API interfaces
│   ├── local/        # Room database
│   ├── model/        # Data models
│   └── repository/   # Data repositories
├── viewmodel/        # ViewModels
├── adapter/          # RecyclerView adapters
├── util/             # Utility classes
├── worker/           # Background workers
├── services/         # FCM and other services
└── Activity*.kt      # UI Activities

backend/
├── api/
│   ├── users.php
│   ├── pantry.php
│   ├── shopping.php
│   ├── recipes.php
│   ├── meal_planner.php
│   └── ingredients.php
├── config/
│   └── database.php
└── database/
    └── schema.sql
```

## How to Run

### Backend Setup
1. Install XAMPP (Apache + MySQL + PHP)
2. Start Apache and MySQL servers
3. Import `backend/database/schema.sql` into MySQL
4. Place `backend/` folder in `htdocs/smartmeal/`
5. Configure database credentials in `backend/config/database.php`
6. Access at `http://localhost/smartmeal/backend/api/`

### Android App Setup
1. Open project in Android Studio
2. Update `BASE_URL` in `ApiConfig.kt` to your server IP
3. Ensure phone and server are on same network
4. Build and run on device or emulator
5. Create account and start using the app

## Testing Credentials
- Email: test@example.com
- Password: Test@123

## Known Issues
- Sync conflicts not fully handled
- Images not implemented yet
- Push notifications not set up
- Some screens need UI polish
- Network error handling needs improvement

## Next Sprint Goals
1. Implement image upload/display
2. Set up FCM and push notifications
3. Improve data sync reliability
4. Polish Recipe Detail screen
5. Implement AI Assistant features
6. Complete Profile/Settings

## Contributors
- Daniyal Khawar (Developer)

## Last Updated
December 7, 2025

