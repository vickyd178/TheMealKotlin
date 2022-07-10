package com.doops.themealkotlin.ui.fragments.meals

import android.content.res.Configuration
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.doops.themealkotlin.adaters.MealsAdapter
import com.doops.themealkotlin.data.models.MealFragmentRecyclerModel
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.databinding.FragmentMealsBinding
import com.doops.themealkotlin.other.collectLatestLifeCycleFlow
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MealsFragment : Fragment() {
    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MealsViewModel>()
    private val arguments: MealsFragmentArgs by navArgs()
    private lateinit var mealsAdapter: MealsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMealsBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        postponeEnterTransition()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.title = viewModel.category?.strCategory ?: "Category"
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        initialize()

    }

    private fun initialize() {
        viewModel.category = arguments.category
        mealsAdapter = MealsAdapter()
        mealsAdapter.onMealItemClickListener = { meal, _, view ->
            when (meal) {
                is MealFragmentRecyclerModel.Description -> {}
                is MealFragmentRecyclerModel.Meal -> {
                    val extras = FragmentNavigatorExtras(view to meal.idMeal)

                    findNavController().navigate(
                        MealsFragmentDirections.actionMealsFragmentToMealDetailFragment(meal),
                        extras
                    )
                }
            }
        }

        viewModel.category?.let {
            binding.toolbar.title = it.strCategory
            binding.detailImage.load(it.strCategoryThumb)
            binding.detailImage.transitionName = it.idCategory
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


        binding.recyclerViewMeals.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(true)
            adapter = mealsAdapter
        }
        collectLatestLifeCycleFlow(viewModel.meals) {
            when (it) {
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    val meals = ArrayList<MealFragmentRecyclerModel>()
                    viewModel.category?.let { category ->
                        meals.add(MealFragmentRecyclerModel.Description(categoryDescription = category.strCategoryDescription))
                    }
                    meals.addAll(it.data.meals)

                    mealsAdapter.submitList(meals)


                }
                is Resource.Failure -> {
                    binding.refreshLayout.isRefreshing = false
                    Timber.d(it.errorBody?.string())
                }
                Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                else -> {}
            }
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.getMealsByCategory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

