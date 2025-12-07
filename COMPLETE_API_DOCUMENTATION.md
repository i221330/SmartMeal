# 🎯 COMPLETE BACKEND API DOCUMENTATION

## ✅ ALL 5 BACKEND APIs COMPLETED!

### Overview:
- ✅ Master Ingredients API
- ✅ Recipes API (with smart matching)
- ✅ Pantry API
- ✅ Shopping List API
- ✅ Meal Planner API

---

## 1. MASTER INGREDIENTS API (`/api/ingredients.php`)

### Get All Ingredients
```http
GET /ingredients.php
```
**Response:**
```json
{
  "success": true,
  "count": 100,
  "data": [
    {
      "id": 1,
      "ingredient_id": "ing_tomato",
      "name": "Tomato",
      "category": "Vegetables",
      "common_unit": "piece",
      "searchable_name": "tomato tomatoes"
    }
  ]
}
```

### Search Ingredients
```http
GET /ingredients.php?search=chicken
```

### Get by Category
```http
GET /ingredients.php?category=Vegetables
```

---

## 2. RECIPES API (`/api/recipes.php`)

### Get All Recipes
```http
GET /recipes.php
```

### Get Recipe Detail
```http
GET /recipes.php?action=detail&recipe_id=recipe_001
```

### Search Recipes
```http
GET /recipes.php?action=search&query=pasta
```

### ⭐ Get Smart Suggestions (Main Feature!)
```http
GET /recipes.php?action=suggestions&user_id=1
```
**Response:**
```json
{
  "success": true,
  "user_id": "1",
  "pantry_items_count": 5,
  "suggestions_count": 5,
  "data": [
    {
      "recipe_id": "recipe_001",
      "title": "Pasta Alfredo",
      "description": "Creamy pasta...",
      "total_ingredients": 7,
      "matched_ingredients": 6,
      "missing_ingredients_count": 1,
      "missing_ingredients": [
        {"name": "heavy cream", "quantity": "1 cup"}
      ],
      "match_percentage": 86,
      "can_make_now": false
    }
  ]
}
```

### Mark Recipe as Made
```http
POST /recipes.php?action=mark_made
Content-Type: application/json

{
  "user_id": 1,
  "recipe_id": "recipe_001",
  "servings_made": 4
}
```
**What it does:**
- Records in recipe_made_history
- Subtracts ingredients from pantry
- Returns list of ingredients used

---

## 3. PANTRY API (`/api/pantry.php`)

### Get User's Pantry
```http
GET /pantry.php?user_id=1
```
**Response:**
```json
{
  "success": true,
  "user_id": "1",
  "total_items": 10,
  "data": [...],
  "grouped_by_category": {
    "Vegetables": [...],
    "Dairy & Eggs": [...]
  }
}
```

### Add Item to Pantry
```http
POST /pantry.php
Content-Type: application/json

{
  "user_id": 1,
  "name": "Tomato",
  "quantity": "5 pieces",
  "category": "Vegetables",
  "expiry_date": 1702857600000
}
```

### Update Pantry Item
```http
PUT /pantry.php
Content-Type: application/json

{
  "item_id": "pantry_1_abc123",
  "quantity": "3 pieces"
}
```

### Delete Pantry Item
```http
DELETE /pantry.php?item_id=pantry_1_abc123
```

---

## 4. SHOPPING LIST API (`/api/shopping.php`)

### Get Shopping List
```http
GET /shopping.php?user_id=1
```
**Response:**
```json
{
  "success": true,
  "user_id": "1",
  "total_items": 5,
  "pending_items": 3,
  "completed_items": 2,
  "data": [...],
  "grouped_by_category": {...}
}
```

### Add Single Item
```http
POST /shopping.php
Content-Type: application/json

{
  "user_id": 1,
  "name": "Milk",
  "quantity": "1 gallon",
  "category": "Dairy & Eggs"
}
```

### ⭐ Add Ingredients from Recipe
```http
POST /shopping.php?action=add_from_recipe
Content-Type: application/json

{
  "user_id": 1,
  "recipe_id": "recipe_001",
  "ingredients": [
    {
      "name": "heavy cream",
      "quantity": "1 cup",
      "category": "Dairy & Eggs",
      "buy_full_amount": false,
      "confirmed_quantity": "0.5 cup"
    }
  ]
}
```
**What it does:**
- Checks pantry for each ingredient
- Shows "already have" flag for items in pantry
- User can confirm full amount or remaining amount
- Adds to shopping list with confirmed quantities

**Response:**
```json
{
  "success": true,
  "items_added": 5,
  "data": [
    {
      "item_id": "shop_1_xyz789",
      "name": "heavy cream",
      "quantity": "0.5 cup",
      "status": "have_some_buying_rest",
      "has_in_pantry": true,
      "pantry_quantity": "0.5 cup"
    }
  ]
}
```

### ⭐ Move Item to Pantry (Buy Item)
```http
POST /shopping.php?action=move_to_pantry
Content-Type: application/json

{
  "item_id": "shop_1_xyz789",
  "user_id": 1,
  "quantity_bought": "1 gallon"
}
```
**What it does:**
- User specifies quantity they actually bought
- Adds to pantry with that quantity
- Marks shopping item as completed

### Update Shopping Item
```http
PUT /shopping.php
Content-Type: application/json

{
  "item_id": "shop_1_xyz789",
  "quantity": "2 gallons",
  "is_completed": false
}
```

### Delete Shopping Item
```http
DELETE /shopping.php?item_id=shop_1_xyz789
```

---

## 5. MEAL PLANNER API (`/api/meal_planner.php`)

### Get Meals for Specific Date
```http
GET /meal_planner.php?user_id=1&date=2025-12-07
```
**Response:**
```json
{
  "success": true,
  "user_id": "1",
  "date": "2025-12-07",
  "meals_count": 3,
  "meals": {
    "breakfast": {
      "plan_id": "plan_1_abc",
      "recipe_id": "recipe_005",
      "recipe_name": "Avocado Toast",
      "meal_type": "breakfast",
      ...
    },
    "lunch": {...},
    "dinner": {...}
  }
}
```

### Get Meals for Week
```http
GET /meal_planner.php?user_id=1&start_date=2025-12-07&end_date=2025-12-13
```
**Response:**
```json
{
  "success": true,
  "grouped_by_date": {
    "2025-12-07": {
      "breakfast": {...},
      "lunch": {...},
      "dinner": {...}
    },
    "2025-12-08": {...}
  }
}
```

### ⭐ Add Meal to Plan
```http
POST /meal_planner.php
Content-Type: application/json

{
  "user_id": 1,
  "recipe_id": "recipe_001",
  "meal_date": "2025-12-07",
  "meal_type": "dinner",
  "notes": "Optional notes"
}
```
**What it does:**
- Adds meal to planner
- Checks user's pantry
- Returns ALL ingredients with "already have" flags
- Frontend shows confirmation dialog
- User confirms which ingredients and quantities to add to shopping list

**Response:**
```json
{
  "success": true,
  "plan_id": "plan_1_xyz123",
  "ingredients_info": {
    "total_ingredients": 7,
    "already_have": 4,
    "need_to_buy": 3,
    "details": [
      {
        "name": "pasta",
        "quantity": "1 lb",
        "has_in_pantry": true,
        "pantry_quantity": "2 lb",
        "need_to_buy": false
      },
      {
        "name": "heavy cream",
        "quantity": "1 cup",
        "has_in_pantry": false,
        "pantry_quantity": null,
        "need_to_buy": true
      }
    ]
  },
  "note": "Use 'ingredients_info.details' to show user confirmation dialog"
}
```

### Update Meal
```http
PUT /meal_planner.php
Content-Type: application/json

{
  "plan_id": "plan_1_xyz123",
  "meal_date": "2025-12-08",
  "meal_type": "lunch"
}
```

### Delete Meal
```http
DELETE /meal_planner.php?plan_id=plan_1_xyz123
```

---

## 🔄 COMPLETE USER FLOW:

### Flow 1: Add to Pantry → See Suggestions
```
1. User adds "tomato" to pantry
   POST /pantry.php
   
2. App refreshes suggestions
   GET /recipes.php?action=suggestions&user_id=1
   
3. Shows recipes with tomato, ranked by match %
```

### Flow 2: Plan Meal → Add to Shopping List
```
1. User adds recipe to meal planner
   POST /meal_planner.php
   → Returns ingredients with "has_in_pantry" flags
   
2. Frontend shows confirmation dialog:
   "You have 4 out of 7 ingredients. Add missing 3 to shopping list?"
   User can adjust quantities per ingredient
   
3. User confirms
   POST /shopping.php?action=add_from_recipe
   → Adds confirmed items to shopping list
```

### Flow 3: Buy Shopping Item → Add to Pantry
```
1. User clicks "Buy" on shopping item
   Frontend shows quantity prompt: "How much did you buy?"
   
2. User enters quantity (e.g., "2 gallons")
   POST /shopping.php?action=move_to_pantry
   
3. Backend:
   - Adds to pantry with specified quantity
   - Marks shopping item as completed
   
4. App refreshes suggestions automatically
```

### Flow 4: Cook Recipe → Subtract from Pantry
```
1. User views recipe details
   GET /recipes.php?action=detail&recipe_id=recipe_001
   
2. User clicks "I made this"
   POST /recipes.php?action=mark_made
   
3. Backend:
   - Records in recipe_made_history
   - Subtracts ingredients from pantry
   - Returns list of ingredients used
   
4. App refreshes pantry and suggestions
```

---

## 📱 FRONTEND INTEGRATION POINTS:

### Home Screen:
```kotlin
// Load today's meals
GET /meal_planner.php?user_id=${userId}&date=2025-12-07

// Load recipe suggestions
GET /recipes.php?action=suggestions&user_id=${userId}
```

### Pantry Screen:
```kotlin
// Search ingredients
GET /ingredients.php?search=${query}

// Add to pantry
POST /pantry.php

// Get user's pantry
GET /pantry.php?user_id=${userId}
```

### Shopping List Screen:
```kotlin
// Get shopping list
GET /shopping.php?user_id=${userId}

// Buy item (with quantity prompt)
POST /shopping.php?action=move_to_pantry
```

### Meal Planner Screen:
```kotlin
// Get week view
GET /meal_planner.php?user_id=${userId}&start_date=...&end_date=...

// Add meal (shows ingredient confirmation)
POST /meal_planner.php
// Then: POST /shopping.php?action=add_from_recipe
```

### Recipe Details Screen:
```kotlin
// Get recipe
GET /recipes.php?action=detail&recipe_id=${recipeId}

// Mark as made
POST /recipes.php?action=mark_made
```

---

## 🚀 DEPLOYMENT CHECKLIST:

1. ✅ Import enhanced_schema.sql to create new tables
2. ✅ Import recipe_seed_data.sql for 30 recipes
3. ✅ Copy all 5 API files to XAMPP htdocs:
   - ingredients.php
   - recipes.php
   - pantry.php
   - shopping.php
   - meal_planner.php
4. ✅ Test each endpoint in Postman or browser
5. ✅ Integrate with Android frontend

---

## 🎯 KEY FEATURES IMPLEMENTED:

✅ **Smart Recipe Matching** - Match % based on pantry
✅ **Ingredient Confirmation** - User confirms quantities before shopping list
✅ **Quantity Prompting** - User specifies bought amount
✅ **Pantry Subtraction** - Auto-subtract when cooking
✅ **Category Grouping** - Organized by ingredient categories
✅ **Soft Deletes** - All items use is_deleted flag
✅ **Timestamp Tracking** - For sync functionality
✅ **Recipe History** - Track what users have cooked

---

**ALL BACKEND APIS READY FOR INTEGRATION!** 🎉

