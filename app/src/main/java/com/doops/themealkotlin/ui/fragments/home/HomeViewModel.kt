package com.doops.themealkotlin.ui.fragments.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doops.themealkotlin.data.models.CategoriesItem
import com.doops.themealkotlin.data.models.MealCategoryList
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.data.repository.MealsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MealsRepository, private val state: SavedStateHandle
) : ViewModel() {


    private val _categories = MutableSharedFlow<Resource<MealCategoryList>?>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val categories = _categories.stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )

    private val _refreshStateFlow = MutableStateFlow<RefreshState>(RefreshState.Initial)
     val refreshStateFlow get() = _refreshStateFlow.asStateFlow()

    init {
        refreshHomeScreen()
    }

    fun refreshHomeScreen() = viewModelScope.launch {
        _refreshStateFlow.emit(RefreshState.Refreshing)
        val deferredMeals = async { repository.getMealCategories() }
        _categories.emit(Resource.Loading)
        val meals = deferredMeals.await()
        _categories.emit(meals)
        _refreshStateFlow.emit(RefreshState.Idle)


    }
}

sealed class RefreshState {
    object Refreshing : RefreshState()
    object Initial : RefreshState()
    object Idle : RefreshState()
}