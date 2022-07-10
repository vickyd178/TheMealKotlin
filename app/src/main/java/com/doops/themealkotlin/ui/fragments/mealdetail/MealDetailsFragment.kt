package com.doops.themealkotlin.ui.fragments.mealdetail

import android.content.res.Configuration
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.doops.themealkotlin.adaters.MealsAdapter
import com.doops.themealkotlin.data.models.MealFragmentRecyclerModel
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.databinding.FragmentMealDetailBinding
import com.doops.themealkotlin.databinding.FragmentMealsBinding
import com.doops.themealkotlin.other.collectLatestLifeCycleFlow
import com.doops.themealkotlin.other.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MealDetailsFragment : Fragment() {
    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MealDetailViewModel>()
    private val arguments: MealDetailsFragmentArgs by navArgs()
    private lateinit var mealsAdapter: MealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMealDetailBinding.inflate(
                LayoutInflater.from(requireContext()),
                container,
                false
            )
        viewModel.meal = arguments.meal
        binding.toolbarLayout.title = viewModel.meal?.strMeal ?: "Meal Details"
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        mealsAdapter = MealsAdapter()
        mealsAdapter.onMealItemClickListener = { meal, position, view ->

            when (meal) {
                is MealFragmentRecyclerModel.Description -> {}
                is MealFragmentRecyclerModel.Meal -> showToast(meal.strMeal)
            }
        }
        //            showToast(meal.strMeal, Toast.LENGTH_SHORT)


        viewModel.meal?.let {
            binding.toolbar.title = it.strMeal
            binding.detailImage.load(it.strMealThumb)
            binding.detailImage.transitionName = it.idMeal

        }
        val orientation = this.resources.configuration.orientation

        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
        val mLayoutManager = GridLayoutManager(requireContext(), spanCount)
        mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> spanCount
                    else -> 1
                }
            }
        }

        collectLatestLifeCycleFlow(viewModel.mealDetailFlow) {
            when (it) {
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                }
                is Resource.Failure -> {
                    binding.refreshLayout.isRefreshing = false
                    Timber.d(it.errorBody?.string())
                }
                Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                else -> {
                    binding.refreshLayout.isRefreshing = false

                }
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getMealDetails()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

