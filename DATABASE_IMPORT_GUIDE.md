# 🗄️ DATABASE SETUP GUIDE - SmartMeal Enhanced Backend

## What I've Created:

### 1. Enhanced Database Schema (`enhanced_schema.sql`)
- **Master Ingredients Table** (100+ predefined ingredients across 12 categories)
- **Recipe Made History** (track when users cook recipes)
- Categories: Vegetables, Fruits, Meat, Seafood, Dairy, Grains, Legumes, Spices, Oils, Sweeteners, Broths, Nuts, Misc

### 2. Recipe Seed Data (`recipe_seed_data.sql`)  
- **30 complete recipes** with:
  - Detailed ingredient lists
  - Step-by-step instructions
  - Prep/cook times, servings, difficulty
  - Cuisine type and diet information
  - Ranges from Easy to Medium difficulty
  - Covers: Italian, American, Chinese, Mexican, Thai, Greek, Japanese, Indian, French cuisines

---

## 📥 HOW TO IMPORT (Do This First!)

### Step 1: Import Enhanced Schema

1. Open **phpMyAdmin**: `http://localhost/phpmyadmin`
2. Select database: **`smartmeal_db`**
3. Click **"SQL"** tab
4. Open file: `/Users/mac/StudioProjects/SmartMeal/backend/database/enhanced_schema.sql`
5. Copy ALL content
6. Paste into SQL box
7. Click **"Go"**

**Result:** Creates master_ingredients and recipe_made_history tables, populates 100+ ingredients

---

### Step 2: Import Recipe Data

1. Still in **`smartmeal_db`** database
2. Click **"SQL"** tab again
3. Open file: `/Users/mac/StudioProjects/SmartMeal/backend/database/recipe_seed_data.sql`
4. Copy ALL content
5. Paste into SQL box  
6. Click **"Go"**

**Result:** Adds 30 complete recipes to your database

---

### Step 3: Verify Import

In phpMyAdmin, check:

**master_ingredients table:**
- Should have 100+ rows
- Categories: Vegetables, Fruits, Meat & Poultry, Seafood, etc.
- Each ingredient has: ID, name, category, common_unit, searchable_name

**recipes table:**
- Should have 30+ recipes
- Check recipe_id, title, ingredients (JSON), instructions, cuisine, etc.

**recipe_made_history table:**
- Should be empty (users haven't cooked anything yet)
- Will track when users mark recipes as "made"

---

## 🍳 WHAT EACH RECIPE INCLUDES:

All 30 recipes have:
- ✅ Unique recipe_id
- ✅ Title and description
- ✅ Prep time and cook time
- ✅ Servings and difficulty level
- ✅ Cuisine type (Italian, Chinese, Mexican, etc.)
- ✅ Diet type (Vegetarian, Vegan, or NULL for contains meat)
- ✅ **Ingredients as JSON array** with name and quantity
- ✅ **Detailed step-by-step instructions** (10 steps each)
- ✅ Global flag (TRUE = available to all users)

---

## 📋 INGREDIENT CATEGORIES (100+ items):

1. **Vegetables** (20 items) - tomato, onion, garlic, potato, carrot, cucumber, peppers, lettuce, etc.
2. **Fruits** (9 items) - lemon, lime, apple, banana, orange, mango, avocado, berries
3. **Meat & Poultry** (9 items) - chicken breast, ground beef, bacon, sausage, turkey, lamb
4. **Seafood** (5 items) - salmon, tuna, shrimp, cod, tilapia
5. **Dairy & Eggs** (11 items) - milk, butter, various cheeses, yogurt, cream, eggs
6. **Grains & Pasta** (9 items) - rice, pasta, bread, flour, quinoa, oats, tortillas
7. **Legumes & Beans** (5 items) - black beans, kidney beans, chickpeas, lentils, tofu
8. **Spices & Herbs** (18 items) - salt, pepper, cumin, paprika, chili powder, oregano, basil, etc.
9. **Oils & Condiments** (13 items) - olive oil, soy sauce, vinegar, ketchup, mustard, mayo, etc.
10. **Sweeteners & Baking** (9 items) - sugar, honey, vanilla, baking powder, cocoa, etc.
11. **Broths & Stocks** (3 items) - chicken, beef, vegetable broth
12. **Nuts & Seeds** (8 items) - almonds, walnuts, peanuts, cashews, sesame seeds, etc.
13. **Miscellaneous** (16 items) - croutons, dressings, olives, peanut butter, tomato paste, etc.

---

## 🎯 HOW THE SYSTEM WILL WORK:

### For Users:
1. **Add to Pantry:** Users search from master_ingredients, select items, add with quantity
2. **View Recipes:** System shows 30 global recipes
3. **Smart Suggestions:** Algorithm matches pantry ingredients with recipes, suggests top 5
4. **Add to Meal Plan:** User adds recipe to specific date/meal type
5. **Auto Shopping List:** Missing ingredients automatically added to shopping list
6. **Clear/Buy Item:** Mark shopping item as bought → auto-adds to pantry
7. **Cook Recipe:** Mark recipe as "made" → subtracts ingredients from pantry
8. **Recipe Updates:** After pantry changes, home screen suggestions refresh

### Database Flow:
```
master_ingredients (predefined) 
    ↓
User adds to → pantry_items (user's ingredients with quantities)
    ↓
Recipes compared with pantry → Smart matching algorithm
    ↓
User adds recipe to → meal_plans (specific date/meal)
    ↓
Missing ingredients → shopping_items
    ↓
User buys item → moves to pantry_items
    ↓
User cooks recipe → recipe_made_history + pantry quantities decreased
```

---

## 📊 EXAMPLE RECIPE STRUCTURE:

```json
{
  "recipe_id": "recipe_001",
  "title": "Classic Pasta Alfredo",
  "description": "Creamy pasta with parmesan cheese",
  "prep_time": 10,
  "cook_time": 15,
  "servings": 4,
  "difficulty": "Easy",
  "cuisine": "Italian",
  "diet_type": "Vegetarian",
  "ingredients": [
    {"name":"pasta","quantity":"1 lb"},
    {"name":"heavy cream","quantity":"1 cup"},
    {"name":"parmesan cheese","quantity":"1/2 cup"},
    {"name":"garlic","quantity":"2 cloves"},
    {"name":"butter","quantity":"2 tbsp"}
  ],
  "instructions": "Step 1...\nStep 2...\n..."
}
```

---

## ✅ AFTER IMPORT:

You'll have:
- ✅ 100+ ingredients users can add to their pantry
- ✅ 30 complete recipes with full details
- ✅ Ready for PHP backend API development
- ✅ Database structure for meal planning, shopping lists, and recipe matching

---

## 🚀 NEXT STEPS:

After importing the data:
1. ✅ I'll create PHP APIs for all CRUD operations
2. ✅ I'll implement the recipe matching algorithm
3. ✅ I'll create ingredient search endpoint
4. ✅ I'll build shopping list auto-generation logic
5. ✅ I'll update Android frontend to connect with backend

---

**Import these SQL files NOW in phpMyAdmin, then I'll proceed with PHP backend APIs!** 🎉

