package com.raylabs.doggie.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.raylabs.doggie.R
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.databinding.ItemImageBinding

class ImagesAdapter(
    private val imagesGrid: List<DoggieEntity>,
    private val listener: (DoggieEntity) -> Unit
) : RecyclerView.Adapter<ImagesAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(imagesGrid[position], listener)
    }

    override fun getItemCount(): Int = imagesGrid.size

    class Holder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DoggieEntity, listener: (DoggieEntity) -> Unit) {
            Glide.with(itemView.context)
                .load(item.link)
                .error(R.drawable.outline_broken_image_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivAnimal)

            itemView.setOnClickListener { listener(item) }
        }
    }
}
