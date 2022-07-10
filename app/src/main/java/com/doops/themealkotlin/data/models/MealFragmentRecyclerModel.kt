package com.doops.themealkotlin.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

sealed class MealFragmentRecyclerModel {
    @Parcelize
    data class Meal(
        @Json(name = "strMealThumb")
        val strMealThumb: String,
        @Json(name = "idMeal")
        val idMeal: String,
        @Json(name = "strMeal")
        val strMeal: String
    ) : Parcelable, MealFragmentRecyclerModel()

    data class Description(val categoryDescription: String) : MealFragmentRecyclerModel()

}