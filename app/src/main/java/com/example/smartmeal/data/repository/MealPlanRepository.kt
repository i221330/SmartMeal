package com.example.smartmeal.data.repository

import androidx.lifecycle.LiveData
import com.example.smartmeal.data.local.MealPlanDao
import com.example.smartmeal.data.model.MealPlan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealPlanRepository(
    private val mealPlanDao: MealPlanDao,
    private val firestore: FirebaseFirestore
) {

    val allMealPlans: LiveData<List<MealPlan>> = mealPlanDao.getAllMealPlans()

    suspend fun getAllMealPlansList(): List<MealPlan> {
        return mealPlanDao.getAllMealPlansList()
    }

    suspend fun getMealPlanById(id: String): MealPlan? {
        return mealPlanDao.getMealPlanById(id)
    }

    suspend fun getMealPlansByDate(startDate: Long, endDate: Long): List<MealPlan> {
        return mealPlanDao.getMealPlansByDateRange(startDate, endDate)
    }

    suspend fun getMealPlanByDateAndType(date: Long, mealType: String): MealPlan? {
        return mealPlanDao.getMealPlanByDateAndType(date, mealType)
    }

    suspend fun insertMealPlan(mealPlan: MealPlan) {
        mealPlanDao.insertMealPlan(mealPlan.copy(isSynced = false))
    }

    suspend fun updateMealPlan(mealPlan: MealPlan) {
        mealPlanDao.updateMealPlan(mealPlan.copy(
            lastUpdated = System.currentTimeMillis(),
            isSynced = false
        ))
    }

    suspend fun deleteMealPlan(id: String) {
        mealPlanDao.softDeleteMealPlan(id)
    }

    suspend fun clearOldMealPlans(beforeDate: Long) {
        mealPlanDao.deleteOldMealPlans(beforeDate)
    }

    suspend fun syncToCloud(userId: String) {
        val unsyncedPlans = mealPlanDao.getUnsyncedMealPlans()

        unsyncedPlans.forEach { plan ->
            try {
                if (plan.isDeleted) {
                    firestore.collection("users")
                        .document(userId)
                        .collection("mealPlans")
                        .document(plan.id)
                        .delete()
                        .await()
                } else {
                    firestore.collection("users")
                        .document(userId)
                        .collection("mealPlans")
                        .document(plan.id)
                        .set(plan)
                        .await()
                }

                mealPlanDao.markAsSynced(plan.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncFromCloud(userId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("mealPlans")
                .get()
                .await()

            val cloudPlans = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MealPlan::class.java)?.copy(isSynced = true)
            }

            mealPlanDao.insertAll(cloudPlans)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

