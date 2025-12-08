# SmartMeal - Image Integration Guide

This guide explains how to add images for recipes and pantry items to give your app a polished, finished look.

## Overview

Your app is already set up to handle images! The data models have `imageUrl` fields:
- **Recipe**: `image_url` field in database and `RecipeDetail` model
- **PantryItem**: `imageUrl` field in both local Room database and API model
- **Glide library**: Already integrated in `build.gradle.kts` for image loading

## Option 1: Using Online Image URLs (Recommended - Easiest)

### For Recipes

#### Step 1: Update Recipe Seed Data with Image URLs

Find free food images from:
- **Unsplash**: https://unsplash.com/s/photos/food (free, no attribution required)
- **Pexels**: https://www.pexels.com/search/food/ (free stock photos)
- **Pixabay**: https://pixabay.com/images/search/food/ (free images)

**Example**: Update `/backend/database/recipe_seed_data.sql`:

```sql
-- Replace NULL with actual image URLs
('recipe_001', 'Classic Pasta Alfredo', 'Creamy pasta with parmesan cheese', 10, 15, 4, 'Easy', 'Italian', 'Vegetarian',
'[...]',
'1. Bring a large pot...',
TRUE, UNIX_TIMESTAMP() * 1000, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9'),
```

#### Step 2: Re-import the SQL File
1. Open phpMyAdmin (http://localhost/phpmyadmin)
2. Select `smartmeal_db` database
3. Go to "Import" tab
4. Choose `/backend/database/recipe_seed_data.sql`
5. Click "Go"

#### Step 3: The App Already Loads Images!

The `ActivityRecipeDetails.kt` already uses Glide to load images:

```kotlin
// Line ~150 in ActivityRecipeDetails.kt
Glide.with(this)
    .load(recipe.image_url)
    .placeholder(R.drawable.ic_launcher_foreground)
    .error(R.drawable.ic_launcher_foreground)
    .centerCrop()
    .into(recipeImage)
```

### For Pantry Items

Images for pantry items can be added when users create items. The UI already uses emoji icons as placeholders.

To add default images:
1. Use ingredient image URLs from sources above
2. Update the backend `pantry.php` to set default images based on category
3. Or allow users to select images when adding items

## Option 2: Using Local Drawable Resources

### Step 1: Add Images to Project

1. **Get Images**: Download recipe/ingredient images
2. **Resize Images**: Recommended sizes:
   - `drawable-mdpi`: 240x240px
   - `drawable-hdpi`: 360x360px
   - `drawable-xhdpi`: 480x480px
   - `drawable-xxhdpi`: 720x720px
   - Or use one size in `drawable-nodpi` folder

3. **Add to Project**:
   ```
   /app/src/main/res/drawable/
   ├── recipe_pasta_alfredo.jpg
   ├── recipe_tomato_soup.jpg
   ├── recipe_caesar_salad.jpg
   ├── ingredient_tomato.jpg
   ├── ingredient_cheese.jpg
   └── ... etc
   ```

### Step 2: Update Recipe Adapter for Local Images

Modify `/app/src/main/java/com/example/smartmeal/adapter/RecipeListAdapter.kt`:

```kotlin
// Add ImageView to item_recipe.xml first, then:
fun bind(recipe: RecipeDetail) {
    recipeTitle.text = recipe.title
    recipeDescription.text = recipe.description ?: "Delicious recipe"
    
    // Load image
    val imageResId = when(recipe.recipe_id) {
        "recipe_001" -> R.drawable.recipe_pasta_alfredo
        "recipe_002" -> R.drawable.recipe_tomato_soup
        // ... add more mappings
        else -> R.drawable.ic_launcher_foreground
    }
    
    Glide.with(itemView.context)
        .load(imageResId)
        .into(recipeImage)
    
    // ... rest of binding code
}
```

### Step 3: Update Recipe List Layout

Modify `/app/src/main/res/layout/item_recipe.xml` to replace the placeholder TextView with an ImageView:

```xml
<!-- Replace the recipeImagePlaceholder TextView with: -->
<ImageView
    android:id="@+id/recipeImage"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:scaleType="centerCrop"
    android:contentDescription="Recipe image"
    android:background="@color/primary" />
```

## Option 3: Using Firebase Storage (Best for Production)

### Step 1: Upload Images to Firebase

1. Go to Firebase Console: https://console.firebase.google.com
2. Select your SmartMeal project
3. Navigate to Storage
4. Create folder structure:
   ```
   /recipes/
   ├── recipe_001.jpg
   ├── recipe_002.jpg
   └── ...
   /ingredients/
   ├── tomato.jpg
   ├── cheese.jpg
   └── ...
   ```
5. Upload images

### Step 2: Get Public URLs

1. Click on each uploaded image
2. Copy the download URL
3. Update your database with these URLs

### Step 3: Update Backend

Modify `/backend/database/recipe_seed_data.sql` with Firebase URLs:

```sql
'https://firebasestorage.googleapis.com/v0/b/your-project.appspot.com/o/recipes%2Frecipe_001.jpg?alt=media&token=...'
```

## Recommended Free Food Images by Recipe

Here are suggested search terms for each recipe type:

| Recipe | Search Term | Recommended Source |
|--------|-------------|-------------------|
| Pasta Alfredo | "fettuccine alfredo" | Unsplash |
| Tomato Soup | "tomato soup bowl" | Pexels |
| Caesar Salad | "caesar salad" | Unsplash |
| Scrambled Eggs | "scrambled eggs" | Pexels |
| Avocado Toast | "avocado toast" | Unsplash |
| Greek Salad | "greek salad" | Pexels |
| Grilled Cheese | "grilled cheese sandwich" | Unsplash |
| Fried Rice | "fried rice" | Pexels |

## Quick Start: Add Images in 10 Minutes

### Method 1: Online URLs (Fastest)

1. Go to https://unsplash.com
2. Search for each recipe name
3. Right-click on image → "Copy image address"
4. Update `recipe_seed_data.sql` replacing `NULL` with URL
5. Re-import SQL file in phpMyAdmin
6. Restart your app - images will load automatically!

### Method 2: Use Placeholder Service

Use a placeholder image service for quick testing:

```sql
-- For recipe_001 (Pasta)
image_url = 'https://source.unsplash.com/400x400/?pasta'

-- For recipe_002 (Soup)
image_url = 'https://source.unsplash.com/400x400/?soup'
```

**Note**: This generates random images each time, good for testing only.

## Current Image Loading Code

Your app **already has** image loading implemented:

### In RecipeListAdapter:
Currently uses emoji placeholder, can be upgraded to ImageView.

### In ActivityRecipeDetails:
```kotlin
// Already working! Line ~150
Glide.with(this)
    .load(recipe.image_url)
    .placeholder(R.drawable.ic_launcher_foreground)
    .error(R.drawable.ic_launcher_foreground)
    .centerCrop()
    .into(recipeImage)
```

### In PantryAdapter:
Currently uses emoji icons (🥬🍎🍗 etc.) which look good, but could be replaced with actual images.

## Adding Images to Recipe List (Card View)

Currently `item_recipe.xml` shows a placeholder emoji. To show actual images:

### Update item_recipe.xml:

Replace:
```xml
<TextView
    android:id="@+id/recipeImagePlaceholder"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:background="@color/primary"
    android:gravity="center"
    android:text="🍽️"
    android:textSize="32sp"
    android:alpha="0.3" />
```

With:
```xml
<ImageView
    android:id="@+id/recipeImage"
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:scaleType="centerCrop"
    android:contentDescription="Recipe image" />
```

### Update RecipeListAdapter.kt:

Add ImageView binding in the ViewHolder:
```kotlin
private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)

fun bind(recipe: RecipeDetail) {
    // ... existing code ...
    
    // Load image
    Glide.with(itemView.context)
        .load(recipe.image_url)
        .placeholder(R.drawable.ic_launcher_foreground)
        .error(R.drawable.ic_launcher_foreground)
        .centerCrop()
        .into(recipeImage)
}
```

## Adding Images for Pantry Items

### Option A: Keep Emoji Icons (Current - Looks Good!)
Your current implementation with category-based emojis (🥬🍎🍗) is actually quite nice and functional.

### Option B: Add Real Images

1. Update `item_pantry.xml` to use ImageView instead of TextView for icon
2. Modify `PantryAdapter.kt` to load images with Glide
3. Add default images per category or per item

## Testing Image Loading

1. **Check Internet**: Ensure device/emulator has internet access
2. **Check Logs**: Look for Glide errors in Logcat
3. **Test URL**: Open image URL in browser to verify it works
4. **Check Placeholder**: If image fails, placeholder should show

## Troubleshooting

### Images Not Loading?

1. **Check Internet Permission**: Already in `AndroidManifest.xml`
2. **Check URL Format**: Must be valid HTTP/HTTPS URL
3. **Clear App Cache**: Settings → Apps → SmartMeal → Clear Cache
4. **Check Glide Dependency**: Already in `build.gradle.kts` ✓

### Images Loading Slowly?

1. Use smaller image sizes (400x400 max for thumbnails)
2. Use `.thumbnail(0.1f)` in Glide for progressive loading
3. Implement image caching (Glide does this automatically)

## Best Practice Recommendations

1. **Use Online URLs** for 30 global recipes (easy to update)
2. **Use Firebase Storage** for user-uploaded recipes
3. **Keep emoji icons** for pantry items (they're fast and look good)
4. **Optimize images**: 800x800px max, JPG format, compress to <100KB
5. **Add loading states**: Show placeholder while loading

## Next Steps

1. **Quick Win**: Add Unsplash URLs to recipe_seed_data.sql (10 min)
2. **Polish**: Update RecipeListAdapter to show thumbnails (20 min)
3. **Enhancement**: Allow users to upload images for custom recipes (later)

## Sample Image URLs for First 5 Recipes

Here are ready-to-use URLs (from Unsplash):

```sql
-- Recipe 001: Pasta Alfredo
'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400'

-- Recipe 002: Tomato Soup
'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400'

-- Recipe 003: Caesar Salad
'https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400'

-- Recipe 004: Scrambled Eggs
'https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400'

-- Recipe 005: Avocado Toast
'https://images.unsplash.com/photo-1541519227354-08fa5d50c44d?w=400'
```

Just copy these URLs into your SQL file where it says `NULL` for `image_url`!

---

**Summary**: Your app is already image-ready! Just add URLs to the database and update the RecipeListAdapter to display them. The easiest path is using online image URLs from Unsplash or Pexels.

