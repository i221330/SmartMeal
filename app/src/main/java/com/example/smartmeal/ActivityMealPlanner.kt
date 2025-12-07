package com.example.smartmeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartmeal.data.api.IngredientsInfo
import com.example.smartmeal.data.api.MealPlan
import com.example.smartmeal.data.api.RecipeDetail
import com.example.smartmeal.data.repository.MealPlannerRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityMealPlanner : AppCompatActivity() {

    private val TAG = "ActivityMealPlanner"
    private val mealPlannerRepository = MealPlannerRepository()
    private var userId: String = "1" // TODO: Get from auth session

    // Date management
    private val calendar = Calendar.getInstance()
    private var selectedDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("d", Locale.getDefault())

    // UI Elements
    private lateinit var progressBar: ProgressBar
    private lateinit var currentDateText: TextView
    private lateinit var selectedDateText: TextView
    private lateinit var breakfastMealText: TextView
    private lateinit var lunchMealText: TextView
    private lateinit var dinnerMealText: TextView

    // Day selector views
    private val dayViews = mutableListOf<LinearLayout>()
    private val dayDateTexts = mutableListOf<TextView>()

    // Current meals
    private var currentBreakfast: MealPlan? = null
    private var currentLunch: MealPlan? = null
    private var currentDinner: MealPlan? = null

    // Available recipes
    private var allRecipes = listOf<RecipeDetail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        Log.d(TAG, "ActivityMealPlanner created")

        initializeViews()
        setupDaySelectors()
        setupMealButtons()
        setupBottomNavigation()

        // Load recipes and set to today
        loadAllRecipes()
        selectToday()
    }

    private fun initializeViews() {
        progressBar = findViewById(R.id.progressBar)
        currentDateText = findViewById(R.id.currentDateText)
        selectedDateText = findViewById(R.id.selectedDateText)
        breakfastMealText = findViewById(R.id.breakfastMealText)
        lunchMealText = findViewById(R.id.lunchMealText)
        dinnerMealText = findViewById(R.id.dinnerMealText)

        // Set current date
        currentDateText.text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

        // Setup day views
        dayViews.add(findViewById(R.id.daySun))
        dayViews.add(findViewById(R.id.dayMon))
        dayViews.add(findViewById(R.id.dayTue))
        dayViews.add(findViewById(R.id.dayWed))
        dayViews.add(findViewById(R.id.dayThu))
        dayViews.add(findViewById(R.id.dayFri))
        dayViews.add(findViewById(R.id.daySat))

        dayDateTexts.add(findViewById(R.id.daySunDate))
        dayDateTexts.add(findViewById(R.id.dayMonDate))
        dayDateTexts.add(findViewById(R.id.dayTueDate))
        dayDateTexts.add(findViewById(R.id.dayWedDate))
        dayDateTexts.add(findViewById(R.id.dayThuDate))
        dayDateTexts.add(findViewById(R.id.dayFriDate))
        dayDateTexts.add(findViewById(R.id.daySatDate))
    }

    private fun setupDaySelectors() {
        // Calculate this week starting from Sunday
        val today = Calendar.getInstance()
        val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday

        // Find the Sunday of this week
        val sundayOfWeek = today.clone() as Calendar
        sundayOfWeek.add(Calendar.DAY_OF_WEEK, -(currentDayOfWeek - Calendar.SUNDAY))

        // Set dates for all 7 days
        for (i in 0..6) {
            val dayCalendar = sundayOfWeek.clone() as Calendar
            dayCalendar.add(Calendar.DAY_OF_WEEK, i)

            val dayNumber = dayFormat.format(dayCalendar.time)
            dayDateTexts[i].text = dayNumber

            // Set click listener
            val dayIndex = i
            dayViews[i].setOnClickListener {
                selectDay(dayIndex, dayCalendar)
            }
        }
    }

    private fun selectToday() {
        val today = Calendar.getInstance()
        val todayDayOfWeek = today.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 7=Saturday

        // Convert to 0-based index (Sunday=0, Saturday=6)
        val todayIndex = todayDayOfWeek - 1

        selectDay(todayIndex, today)
    }

    private fun selectDay(dayIndex: Int, dayCalendar: Calendar) {
        // Update selected date
        selectedDate = dayCalendar.clone() as Calendar

        // Update UI - highlight selected day
        dayViews.forEachIndexed { index, view ->
            view.isSelected = (index == dayIndex)
            view.findViewById<TextView>(when(index) {
                0 -> R.id.daySunLetter
                1 -> R.id.dayMonLetter
                2 -> R.id.dayTueLetter
                3 -> R.id.dayWedLetter
                4 -> R.id.dayThuLetter
                5 -> R.id.dayFriLetter
                else -> R.id.daySatLetter
            }).setTextColor(
                if (index == dayIndex) getColor(R.color.white)
                else getColor(R.color.text_light)
            )
            dayDateTexts[index].setTextColor(
                if (index == dayIndex) getColor(R.color.white)
                else getColor(R.color.text_light)
            )
        }

        // Update selected date text
        val today = Calendar.getInstance()
        val isToday = selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                     selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        val isTomorrow = selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1

        val prefix = when {
            isToday -> "Today"
            isTomorrow -> "Tomorrow"
            selectedDate.before(today) -> "Past"
            else -> "Next"
        }

        selectedDateText.text = "$prefix - ${displayDateFormat.format(selectedDate.time)}"

        // Load meals for selected date
        loadMealsForSelectedDate()
    }

    private fun loadAllRecipes() {
        lifecycleScope.launch {
            try {
                val result = mealPlannerRepository.getAllRecipes()
                result.onSuccess { recipes ->
                    allRecipes = recipes
                    Log.d(TAG, "Loaded ${recipes.size} recipes")
                }
                result.onFailure { error ->
                    Log.e(TAG, "Failed to load recipes", error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading recipes", e)
            }
        }
    }

    private fun loadMealsForSelectedDate() {
        showLoading(true)

        val dateString = dateFormat.format(selectedDate.time)
        Log.d(TAG, "Loading meals for date: $dateString")

        lifecycleScope.launch {
            try {
                val result = mealPlannerRepository.getMealsForDate(userId, dateString)

                result.onSuccess { response ->
                    Log.d(TAG, "Loaded ${response.meals_count} meals")
                    currentBreakfast = response.meals.breakfast
                    currentLunch = response.meals.lunch
                    currentDinner = response.meals.dinner

                    updateMealUI()
                }

                result.onFailure { error ->
                    Log.e(TAG, "Failed to load meals", error)
                    // Show empty meals
                    currentBreakfast = null
                    currentLunch = null
                    currentDinner = null
                    updateMealUI()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading meals", e)
                updateMealUI()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun updateMealUI() {
        breakfastMealText.text = currentBreakfast?.recipe_name ?: "No meal planned"
        lunchMealText.text = currentLunch?.recipe_name ?: "No meal planned"
        dinnerMealText.text = currentDinner?.recipe_name ?: "No meal planned"
    }

    private fun setupMealButtons() {
        findViewById<MaterialButton>(R.id.addBreakfastButton).setOnClickListener {
            launchRecipeList("breakfast")
        }

        findViewById<MaterialButton>(R.id.addLunchButton).setOnClickListener {
            launchRecipeList("lunch")
        }

        findViewById<MaterialButton>(R.id.addDinnerButton).setOnClickListener {
            launchRecipeList("dinner")
        }
    }

    private fun launchRecipeList(mealType: String) {
        val dateString = dateFormat.format(selectedDate.time)

        val intent = Intent(this, ActivityRecipeList::class.java).apply {
            putExtra("MEAL_TYPE", mealType)
            putExtra("MEAL_DATE", dateString)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Reload meals when returning from recipe list
        loadMealsForSelectedDate()
    }


    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, ActivityHome::class.java))
            finish()
        }
        findViewById<View>(R.id.navPantry)?.setOnClickListener {
            startActivity(Intent(this, ActivityPantry::class.java))
            finish()
        }
        findViewById<View>(R.id.navPlanner)?.setOnClickListener {
            // Already on planner
        }
        findViewById<View>(R.id.navShopping)?.setOnClickListener {
            startActivity(Intent(this, ActivityShoppingList::class.java))
            finish()
        }
        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
            finish()
        }
    }
}

