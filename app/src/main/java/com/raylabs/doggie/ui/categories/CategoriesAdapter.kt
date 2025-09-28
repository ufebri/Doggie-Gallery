package com.raylabs.doggie.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ItemImageCategoriesBinding
import com.raylabs.doggie.vo.BreedCategory

class CategoriesAdapter(
    private val onItemClick: (BreedCategory) -> Unit,
    private val onPreviewMissing: (BreedCategory) -> Unit
) : PagingDataAdapter<BreedCategory, CategoriesAdapter.Holder>(DIFF_CALLBACK) {

    private val requestedPreviewKeys = mutableMapOf<String, Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemImageCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = runCatching { getItem(position) }.getOrNull()
        if (item != null) {
            holder.bind(item)
            val key = previewKey(item)
            if (item.imageUrl.isBlank()) {
                val now = System.currentTimeMillis()
                val lastRequest = requestedPreviewKeys[key] ?: 0L
                if (now - lastRequest > REQUEST_COOLDOWN_MS) {
                    requestedPreviewKeys[key] = now
                    onPreviewMissing(item)
                }
            } else {
                requestedPreviewKeys.remove(key)
            }
        } else {
            holder.clear()
        }
    }

    private fun previewKey(category: BreedCategory): String {
        return "${category.breed}:${category.subBreed ?: ""}"
    }

    inner class Holder(private val binding: ItemImageCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: BreedCategory) {
            if (category.imageUrl.isBlank()) {
                binding.ivCategories.setImageResource(R.drawable.ic_launcher_foreground)
            } else {
                Glide.with(itemView.context)
                    .load(category.imageUrl)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.outline_broken_image_24)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivCategories)
            }

            binding.tvCategories.text = category.displayName
            itemView.setOnClickListener { onItemClick(category) }
        }

        fun clear() {
            binding.tvCategories.text = ""
            binding.ivCategories.setImageResource(R.drawable.ic_launcher_foreground)
            itemView.setOnClickListener(null)
        }
    }

    companion object {
        private const val REQUEST_COOLDOWN_MS = 15_000L
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BreedCategory>() {
            override fun areItemsTheSame(oldItem: BreedCategory, newItem: BreedCategory): Boolean {
                if (oldItem.breed != newItem.breed) return false
                return oldItem.subBreed == newItem.subBreed
            }

            override fun areContentsTheSame(
                oldItem: BreedCategory,
                newItem: BreedCategory
            ): Boolean =
                oldItem == newItem
        }
    }
}
