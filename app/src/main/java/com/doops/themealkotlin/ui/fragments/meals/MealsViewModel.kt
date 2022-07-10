package com.doops.themealkotlin.ui.fragments.meals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doops.themealkotlin.data.models.CategoriesItem
import com.doops.themealkotlin.data.models.MealsResponse
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.data.repository.MealsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val repository: MealsRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var category = state.get<CategoriesItem>("category")
        set(value) {
            field = value
            value?.let {
                if (_meals.value == null)
                    getMealsByCategory()
            }
        }

    private val _meals = MutableStateFlow<Resource<MealsResponse>?>(null)
    val meals = _meals.asSharedFlow()

    fun getMealsByCategory() = viewModelScope.launch {
        category?.let {
            delay(300)
            _meals.emit(Resource.Loading)
            delay(500)
            val mealsDeferred = async { repository.getMealByCategory(it.strCategory) }
            _meals.emit(mealsDeferred.await())
        }

    }


}