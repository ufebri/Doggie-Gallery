package com.raylabs.doggie.ui.categories.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ItemImageBinding

class BreedImagesAdapter(
    private val onItemClick: (String) -> Unit
) : PagingDataAdapter<String, BreedImagesAdapter.BreedHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BreedHolder(binding)
    }

    override fun onBindViewHolder(holder: BreedHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        } else {
            holder.clear()
        }
    }

    inner class BreedHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(itemView.context)
                .load(url)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivAnimal)

            itemView.setOnClickListener { onItemClick(url) }
        }

        fun clear() {
            binding.ivAnimal.setImageResource(R.drawable.ic_launcher_foreground)
            itemView.setOnClickListener(null)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}
