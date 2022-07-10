package com.doops.themealkotlin.adaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView.BufferType
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import coil.size.Scale
import com.doops.themealkotlin.R
import com.doops.themealkotlin.data.models.MealFragmentRecyclerModel
import com.doops.themealkotlin.databinding.ItemCategoryDescriptionRowBinding
import com.doops.themealkotlin.databinding.ItemMealGridBinding

class MealsAdapter :
    ListAdapter<MealFragmentRecyclerModel, MealsViewHolder>(DiffUtils()) {

    /*  inner class MealViewHolder(private val binding: ItemMealGridBinding) :
          RecyclerView.ViewHolder(binding.root) {
          fun bind(meal: MealFragmentRecyclerModel) {
              binding.tvMealName.text = strMeal

              binding.thumbnailMeal.load(strMealThumb) {
                  scale(Scale.FILL)
              }

              binding.root.setOnClickListener {
                  onMealItemClickListener?.invoke(
                      meal,
                      adapterPosition
                  )
              }
          }
      }*/

    var onMealItemClickListener: ((meal: MealFragmentRecyclerModel, position: Int, view: ImageView) -> Unit)? =
        null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealsViewHolder {
        return when (viewType) {
            R.layout.item_category_description_row -> MealsViewHolder.CategoryViewHolder(
                ItemCategoryDescriptionRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_meal_grid -> MealsViewHolder.MealViewHolder(
                ItemMealGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalArgumentException("Invalid View Type Provided")
        }
        /* return MealsViewHolder(
             ItemMealGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
         )*/
    }

    override fun onBindViewHolder(holder: MealsViewHolder, position: Int) {
        holder.onMealItemClickListener = onMealItemClickListener
        when (holder) {
            is MealsViewHolder.CategoryViewHolder -> holder.bind(getItem(position) as MealFragmentRecyclerModel.Description)
            is MealsViewHolder.MealViewHolder -> holder.bind(getItem(position) as MealFragmentRecyclerModel.Meal)
        }

    }

    class DiffUtils : DiffUtil.ItemCallback<MealFragmentRecyclerModel>() {
        override fun areItemsTheSame(
            oldItem: MealFragmentRecyclerModel,
            newItem: MealFragmentRecyclerModel
        ) = when {
            oldItem is MealFragmentRecyclerModel.Meal && newItem is MealFragmentRecyclerModel.Meal -> oldItem.idMeal == newItem.idMeal
            oldItem is MealFragmentRecyclerModel.Description && newItem is MealFragmentRecyclerModel.Description -> oldItem.categoryDescription == newItem.categoryDescription
            else -> throw IllegalArgumentException("Invalid View Provided")
        }

        override fun areContentsTheSame(
            oldItem: MealFragmentRecyclerModel,
            newItem: MealFragmentRecyclerModel
        ) = when {
            oldItem is MealFragmentRecyclerModel.Meal && newItem is MealFragmentRecyclerModel.Meal -> oldItem == newItem
            oldItem is MealFragmentRecyclerModel.Description && newItem is MealFragmentRecyclerModel.Description -> oldItem.categoryDescription == newItem.categoryDescription
            else -> throw IllegalArgumentException("Invalid View Provided")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MealFragmentRecyclerModel.Description -> R.layout.item_category_description_row
            is MealFragmentRecyclerModel.Meal -> R.layout.item_meal_grid
        }
    }
}

sealed class MealsViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    var onMealItemClickListener: ((meal: MealFragmentRecyclerModel, position: Int, view: ImageView) -> Unit)? =
        null


    class CategoryViewHolder(private val binding: ItemCategoryDescriptionRowBinding) :
        MealsViewHolder(binding) {
        fun bind(description: MealFragmentRecyclerModel.Description) {
            binding.tvCategoryDescription.apply {
                text = description.categoryDescription
            }
            /*binding.tvCategoryDescription.setOnExpandableClickListener(
                onExpand = {

                },
                onCollapse = {

                }
            )*/

            /* binding.tvShowMore.setOnClickListener {
                 binding.tvCategoryDescription.performClick()
             }
             binding.tvCategoryDescription.setOnClickListener {
                 binding.tvCategoryDescription.onClick(it)
                 binding.tvShowMore.text =
                     if (binding.tvCategoryDescription.isCollapsing) "Show More" else "Hide"
             }*/
        }
    }

    class MealViewHolder(private val binding: ItemMealGridBinding) :
        MealsViewHolder(binding) {


        fun bind(meal: MealFragmentRecyclerModel.Meal) {

            binding.tvMealName.text = meal.strMeal

            binding.thumbnailMeal.load(meal.strMealThumb) {
                scale(Scale.FILL)
            }
            binding.thumbnailMeal.transitionName = meal.idMeal

            binding.root.setOnClickListener {
                onMealItemClickListener?.invoke(
                    meal,
                    adapterPosition,
                    binding.thumbnailMeal
                )
            }
        }

    }

}
