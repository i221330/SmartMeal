package com.example.smartmeal.worker

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Background sync worker
 * TODO: Implement PHP backend sync when needed
 * Currently using direct API calls instead of background sync
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // TODO: Implement PHP backend sync
            // For now, the app syncs directly when screens load
            // No background sync needed yet

            /* OLD FIREBASE SYNC CODE - COMMENTED OUT
            val app = applicationContext as SmartMealApplication
            val database = app.database
            val firestore = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                // User not logged in, skip sync
                return@withContext Result.success()
            }

            // Sync pantry data
            val pantryRepo = PantryRepository(database.pantryDao(), firestore)
            pantryRepo.syncToCloud(userId)
            pantryRepo.syncFromCloud(userId)

            // Sync shopping list data
            val shoppingRepo = ShoppingRepository(database.shoppingDao(), firestore)
            shoppingRepo.syncToCloud(userId)
            shoppingRepo.syncFromCloud(userId)

            // Sync meal plan data
            val mealPlanRepo = MealPlanRepository(database.mealPlanDao(), firestore)
            mealPlanRepo.syncToCloud(userId)
            mealPlanRepo.syncFromCloud(userId)

            // Sync recipe data (user's custom recipes and favorites)
            val recipeRepo = RecipeRepository(database.recipeDao(), firestore)
            recipeRepo.syncToCloud(userId)
            recipeRepo.syncFromCloud(userId)

            // Load global recipes if local db is empty
            val localRecipeCount = database.recipeDao().getAllRecipesList().size
            if (localRecipeCount < 10) {
                recipeRepo.loadGlobalRecipes()
            }

            // Update last sync time
            database.userDao().updateLastSyncTime(userId, System.currentTimeMillis())
            */

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retry on failure
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val WORK_NAME = "smart_meal_sync"
        private const val SYNC_INTERVAL_MINUTES = 15L

        /**
         * Schedule periodic sync work
         * TODO: Re-enable when PHP backend sync is implemented
         */
        fun schedule(context: Context) {
            // Currently disabled - using direct API calls
            /* OLD CODE:
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // Flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10, TimeUnit.SECONDS
                )
                .addTag("sync")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest
                )
            */
        }

        /**
         * Cancel scheduled sync work
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
        }

        /**
         * Trigger immediate one-time sync
         */
        fun syncNow(context: Context) {
            // Currently disabled - using direct API calls
            /* OLD CODE:
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .addTag("sync_now")
                .build()

            WorkManager.getInstance(context)
                .enqueue(syncRequest)
            */
        }
    }
}

