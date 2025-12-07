package com.example.smartmeal.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartmeal.data.model.MealPlan

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans WHERE isDeleted = 0 ORDER BY date ASC, mealType ASC")
    fun getAllMealPlans(): LiveData<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE isDeleted = 0 ORDER BY date ASC, mealType ASC")
    suspend fun getAllMealPlansList(): List<MealPlan>

    @Query("SELECT * FROM meal_plans WHERE id = :id")
    suspend fun getMealPlanById(id: String): MealPlan?

    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY date ASC, mealType ASC")
    fun getMealPlansForDateRange(startDate: Long, endDate: Long): LiveData<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY date ASC, mealType ASC")
    suspend fun getMealPlansByDateRange(startDate: Long, endDate: Long): List<MealPlan>

    @Query("SELECT * FROM meal_plans WHERE date = :date AND mealType = :mealType AND isDeleted = 0")
    suspend fun getMealPlanForDateTime(date: Long, mealType: String): MealPlan?

    @Query("SELECT * FROM meal_plans WHERE date = :date AND mealType = :mealType AND isDeleted = 0")
    suspend fun getMealPlanByDateAndType(date: Long, mealType: String): MealPlan?

    @Query("SELECT * FROM meal_plans WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedMealPlans(): List<MealPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealPlans: List<MealPlan>)

    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)

    @Query("UPDATE meal_plans SET isDeleted = 1, lastUpdated = :timestamp WHERE id = :id")
    suspend fun softDeleteMealPlan(id: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)

    @Query("DELETE FROM meal_plans")
    suspend fun deleteAll()

    @Query("DELETE FROM meal_plans WHERE date < :beforeDate")
    suspend fun deleteOldMealPlans(beforeDate: Long)

    @Query("UPDATE meal_plans SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}

