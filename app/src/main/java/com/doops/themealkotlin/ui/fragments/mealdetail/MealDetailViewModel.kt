package com.doops.themealkotlin.ui.fragments.mealdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doops.themealkotlin.data.models.*
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.data.repository.MealsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealDetailViewModel @Inject constructor(
    private val repository: MealsRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var meal = state.get<MealFragmentRecyclerModel.Meal>("meal")
        set(value) {
            field = value
            value?.let {
                getMealDetails()
            }
        }


    private val _mealDetailFlow = MutableStateFlow<Resource<MealDetailResponse>?>(null)
    val mealDetailFlow = _mealDetailFlow.asSharedFlow()

    fun getMealDetails() = viewModelScope.launch {
        meal?.idMeal?.let {
            _mealDetailFlow.emit(Resource.Loading)
            val deferredDetails = async { repository.getMealDetails(it) }
            _mealDetailFlow.emit(deferredDetails.await())
        }
    }


}