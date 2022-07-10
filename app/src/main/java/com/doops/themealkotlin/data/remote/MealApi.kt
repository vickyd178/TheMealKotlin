package com.doops.themealkotlin.data.remote

import com.doops.themealkotlin.data.models.MealCategoryList
import com.doops.themealkotlin.data.models.MealDetailResponse
import com.doops.themealkotlin.data.models.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    @GET("categories.php")
    suspend fun getMeals(): MealCategoryList


    @GET("filter.php")
    suspend fun filterMealsByCategory(
        @Query("c") categoryName: String
    ): MealsResponse

    @GET("lookup.php")
    suspend fun getMealDetails(
        @Query("i") mealId: String
    ): MealDetailResponse
}