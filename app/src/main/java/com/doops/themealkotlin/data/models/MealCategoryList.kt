package com.doops.themealkotlin.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoriesItem(
    @Json(name = "strCategory")
    val strCategory: String,
    @Json(name = "strCategoryDescription")
    val strCategoryDescription: String,
    @Json(name = "idCategory")
    val idCategory: String,
    @Json(name = "strCategoryThumb")
    val strCategoryThumb: String
) : Parcelable


data class MealCategoryList(
    @Json(name = "categories")
    val categories: List<CategoriesItem>?
)


