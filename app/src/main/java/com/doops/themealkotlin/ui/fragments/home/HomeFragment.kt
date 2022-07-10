package com.doops.themealkotlin.ui.fragments.home

import android.content.res.Configuration
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.doops.themealkotlin.MainActivity
import com.doops.themealkotlin.adaters.CategoryAdapter
import com.doops.themealkotlin.data.remote.Resource
import com.doops.themealkotlin.databinding.FragmentHomeBinding
import com.doops.themealkotlin.other.collectLatestLifeCycleFlow
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentHomeBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        postponeEnterTransition()
        binding.toolbar.title = "The Meal"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        categoryAdapter = CategoryAdapter()
        categoryAdapter.onCategoryItemClickListener = { categoriesItem, position, view ->

            val extras = FragmentNavigatorExtras(view to categoriesItem.idCategory)
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToMealsFragment(categoriesItem), extras
            )
        }
        val orientation = this.resources.configuration.orientation
        binding.recyclerViewCategories.apply {
            layoutManager =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) LinearLayoutManager(
                    requireContext()
                ) else GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.refreshHomeScreen()
        }
        collectLatestLifeCycleFlow(viewModel.refreshStateFlow) {
            binding.refreshLayout.isRefreshing = when (it) {
                RefreshState.Idle, RefreshState.Initial -> false
                RefreshState.Refreshing -> true
            }
        }
        collectLatestLifeCycleFlow(viewModel.categories) {
            when (it) {
                is Resource.Success -> {
                    categoryAdapter.submitList(it.data.categories)
                    (view?.parent as? ViewGroup)?.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
                is Resource.Failure -> {
                    Timber.d(it.errorBody?.string())
                }
                Resource.Loading -> {}
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
