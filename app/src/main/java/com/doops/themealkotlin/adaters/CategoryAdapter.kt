package com.doops.themealkotlin.adaters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.doops.themealkotlin.data.models.CategoriesItem
import com.doops.themealkotlin.databinding.ItemCategoryRowBinding
import com.doops.themealkotlin.other.toPixels

class CategoryAdapter :
    ListAdapter<CategoriesItem, CategoryAdapter.CategoryViewHolder>(DiffUtils()) {

    inner class CategoryViewHolder(private val binding: ItemCategoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoriesItem) {
            binding.tvCategoryName.text = category.strCategory
            binding.tvCategoryDescription.text = category.strCategoryDescription
            binding.thumbnailCategory.load(category.strCategoryThumb) {
                transformations(
                    RoundedCornersTransformation(
                        topLeft = 8f.toPixels(binding.root.context),
                        bottomLeft = 8f.toPixels(binding.root.context)
                    )
                )
                scale(Scale.FILL)
            }
            binding.thumbnailCategory.transitionName = category.idCategory
            binding.root.setOnClickListener {
                onCategoryItemClickListener?.invoke(
                    category,
                    adapterPosition,
                    binding.thumbnailCategory
                )
            }
        }
    }

    var onCategoryItemClickListener: ((category: CategoriesItem, position: Int,imageView : ImageView) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtils : DiffUtil.ItemCallback<CategoriesItem>() {
        override fun areItemsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem) =
            oldItem.idCategory == newItem.idCategory

        override fun areContentsTheSame(oldItem: CategoriesItem, newItem: CategoriesItem) =
            oldItem == newItem

    }
}
