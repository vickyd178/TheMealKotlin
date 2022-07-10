package com.doops.themealkotlin.data.repository

import com.doops.themealkotlin.data.remote.MealApi
import com.doops.themealkotlin.data.remote.SafeApiCall
import javax.inject.Inject

class MealsRepository @Inject constructor(private val mealApi: MealApi) : SafeApiCall {

    suspend fun getMealCategories() = safeApiCall {
        mealApi.getMeals()
    }

    suspend fun getMealByCategory(category: String) = safeApiCall {
        mealApi.filterMealsByCategory(category)
    }

    suspend fun getMealDetails(mealId: String) = safeApiCall {
        mealApi.getMealDetails(mealId)
    }
}