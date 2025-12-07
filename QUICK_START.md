# 🚀 QUICK START - Do This Right Now!

## Step 1: Build the App (5 minutes)

Open Terminal and run:

```bash
cd /Users/mac/StudioProjects/SmartMeal

# Sync Gradle dependencies
./gradlew --refresh-dependencies

# Clean previous builds
./gradlew clean

# Build the app
./gradlew assembleDebug
```

**OR** in Android Studio:
1. Open project
2. File → Sync Project with Gradle Files
3. Build → Clean Project
4. Build → Rebuild Project

---

## Step 2: Run the App (2 minutes)

1. Click the green "Run" button in Android Studio
2. Select an emulator or connected device
3. Wait for app to launch

**Expected behavior:**
- Splash screen appears
- Navigates to onboarding (first launch)
- Can navigate through onboarding
- Can reach login/signup screens

---

## Step 3: Verify Firebase (2 minutes)

1. Go to https://console.firebase.google.com
2. Select project: **smartmeal-704ba**
3. Check these are enabled:
   - ✅ Authentication → Sign-in method → Email/Password (ENABLED)
   - ✅ Firestore Database → Create database (if not exist)
   - ✅ Storage → Get Started (if not exist)
   - ✅ Cloud Messaging (should be auto-enabled)

---

## Step 4: Test Basic Functionality (10 minutes)

### Test 1: Navigation
- [ ] App opens to splash
- [ ] Shows onboarding on first launch
- [ ] Can navigate through 3 onboarding screens
- [ ] Reaches login screen

### Test 2: Room Database (Offline)
Turn OFF WiFi and mobile data, then:

1. Open ActivityHome (navigate through UI)
2. Check if sample recipes loaded
3. App should work without internet

**Check database:**
1. In Android Studio: View → Tool Windows → App Inspection
2. Select "Database Inspector"
3. You should see: smartmeal_database with tables

---

## Step 5: Wire Your First ViewModel (30 minutes)

Let's connect RecipeList to its ViewModel.

### Create RecipeAdapter

Create file: `app/src/main/java/com/example/smartmeal/adapter/RecipeAdapter.kt`

```kotlin
package com.example.smartmeal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartmeal.R
import com.example.smartmeal.data.model.Recipe

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onRecipeClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeViewHolder(
        itemView: View,
        private val onRecipeClick: (Recipe) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView? = itemView.findViewById(R.id.recipeTitle)
        private val imageView: ImageView? = itemView.findViewById(R.id.recipeImage)
        private val timeText: TextView? = itemView.findViewById(R.id.recipeTime)

        fun bind(recipe: Recipe) {
            titleText?.text = recipe.title
            timeText?.text = "${recipe.prepTime + recipe.cookTime} min"
            
            imageView?.let { img ->
                Glide.with(itemView.context)
                    .load(recipe.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(img)
            }
            
            itemView.setOnClickListener { onRecipeClick(recipe) }
        }
    }

    private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = 
            oldItem == newItem
    }
}
```

### Update ActivityRecipeList

Edit: `app/src/main/java/com/example/smartmeal/ActivityRecipeList.kt`

Add imports and wire ViewModel:

```kotlin
package com.example.smartmeal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartmeal.adapter.RecipeAdapter
import com.example.smartmeal.data.repository.RecipeRepository
import com.example.smartmeal.viewmodel.RecipeViewModel
import com.example.smartmeal.viewmodel.RecipeViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class ActivityRecipeList : AppCompatActivity() {
    
    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(
            RecipeRepository(
                (application as SmartMealApplication).database.recipeDao(),
                FirebaseFirestore.getInstance()
            )
        )
    }
    
    private lateinit var adapter: RecipeAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        
        setupRecyclerView()
        observeData()
        
        // Load sample recipes
        viewModel.loadGlobalRecipes()
        
        setupBackButton()
    }
    
    private fun setupRecyclerView() {
        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, ActivityRecipeDetails::class.java)
            intent.putExtra("recipe_id", recipe.id)
            startActivity(intent)
        }
        
        // Find RecyclerView in layout (add if missing)
        findViewById<RecyclerView>(R.id.recipesRecyclerView)?.apply {
            this.adapter = this@ActivityRecipeList.adapter
            layoutManager = LinearLayoutManager(this@ActivityRecipeList)
        }
    }
    
    private fun observeData() {
        viewModel.allRecipes.observe(this) { recipes ->
            adapter.submitList(recipes)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            findViewById<ProgressBar>(R.id.progressBar)?.isVisible = isLoading
        }
    }
    
    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
}
```

### Add RecyclerView to Layout

Edit: `app/src/main/res/layout/activity_recipe_list.xml`

Add RecyclerView if not present:

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recipesRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_marginTop="16dp"
    app:layout_constraintTop_toBottomOf="@id/searchView"
    app:layout_constraintBottom_toBottomOf="parent"
    android:clipToPadding="false"
    android:paddingBottom="80dp" />

<ProgressBar
    android:id="@+id/progressBar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:visibility="gone"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

---

## Step 6: Test Recipe List (5 minutes)

1. **Run the app**
2. **Navigate to Recipe List**
3. **You should see:**
   - Sample recipes loaded from Room
   - Can scroll through list
   - Clicking recipe goes to details (even if details page is empty)

**If you see recipes → SUCCESS!** Your ViewModel is connected!

---

## Step 7: Verify Offline Mode (5 minutes)

1. Turn OFF WiFi
2. Close and reopen app
3. Navigate to Recipe List
4. Recipes still appear? ✅ **Offline mode works!**

---

## Step 8: Test Firebase Sync (10 minutes)

1. Turn ON WiFi
2. Keep app open for 15 minutes (or trigger manual sync)
3. Go to Firebase Console → Firestore Database
4. You should see collections being created

**OR** force sync by:
- Restarting the app (sync runs on startup)
- Checking Logcat for "Sync" messages

---

## 🎯 Success Checklist

After these steps, you should have:

- [x] App builds without errors
- [x] App runs and shows UI
- [x] Room database working (sample recipes load)
- [x] RecipeList shows data from ViewModel
- [x] App works offline
- [x] Firebase configured

**If all checked → You're ready to proceed with full frontend integration!**

---

## 📋 Next Steps

Once Recipe List works, repeat the same pattern for:

1. **Pantry** (PantryAdapter + wire PantryViewModel)
2. **Shopping List** (ShoppingAdapter + wire ShoppingListViewModel)
3. **Meal Planner** (MealPlanAdapter + wire MealPlanViewModel)
4. **Recipe Details** (load recipe by ID)
5. **Home** (recipe suggestions with RecipeMatchingEngine)

**Time estimate:** 2-3 hours per screen = 10-15 hours total

---

## 🆘 Troubleshooting

### Build Error: "Unresolved reference"
**Solution:** File → Invalidate Caches → Restart

### App crashes on startup
**Solution:** Check Logcat (View → Tool Windows → Logcat)
- Look for red error messages
- Most likely: Missing view IDs in XML

### RecyclerView shows nothing
**Solution:**
1. Check Logcat for database errors
2. Verify recipes exist: Database Inspector → smartmeal_database → recipes table
3. If empty, sample recipes didn't load → Check ActivityHome.kt

### Firebase not syncing
**Solution:**
1. Verify google-services.json is in `/app/` folder
2. Check Firebase Console: Authentication must be enabled
3. Check Firestore rules allow read/write

---

## 📞 Quick Links

- **Firebase Console:** https://console.firebase.google.com/project/smartmeal-704ba
- **Android Documentation:** https://developer.android.com/docs
- **Room Database:** https://developer.android.com/training/data-storage/room
- **Firebase Android:** https://firebase.google.com/docs/android/setup

---

## 🎉 You're Ready!

Your backend is solid. Now just:
1. Build the app ✅
2. Wire ViewModels to Activities 🔄
3. Test everything works 🔄

**Estimated time to complete app: 12-15 hours**

**Good luck! 🚀**

