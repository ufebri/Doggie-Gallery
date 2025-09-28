package com.raylabs.doggie.ui.categories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.raylabs.doggie.R;
import com.raylabs.doggie.databinding.ItemImageCategoriesBinding;
import com.raylabs.doggie.vo.BreedCategory;

import java.util.Objects;

public class CategoriesAdapter extends PagingDataAdapter<BreedCategory, CategoriesAdapter.Holder> {
    private static final DiffUtil.ItemCallback<BreedCategory> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull BreedCategory oldItem, @NonNull BreedCategory newItem) {
            if (oldItem.getBreed().equals(newItem.getBreed())) {
                String oldSub = oldItem.getSubBreed();
                String newSub = newItem.getSubBreed();
                return Objects.equals(oldSub, newSub);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull BreedCategory oldItem, @NonNull BreedCategory newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final OnItemClickListener listener;


    public CategoriesAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageCategoriesBinding binding = ItemImageCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BreedCategory item = getItem(position);
        if (item != null) {
            holder.bind(item, listener);
        } else {
            holder.clear();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BreedCategory category);
    }

    public static class Holder extends RecyclerView.ViewHolder {

        final ItemImageCategoriesBinding binding;

        public Holder(@NonNull ItemImageCategoriesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(BreedCategory category, OnItemClickListener listener) {
            Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_launcher_background)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivCategories);

            binding.tvCategories.setText(category.getDisplayName());

            itemView.setOnClickListener(v -> listener.onItemClick(category));
        }

        void clear() {
            binding.tvCategories.setText("");
            binding.ivCategories.setImageResource(R.drawable.ic_launcher_foreground);
            itemView.setOnClickListener(null);
        }
    }
}
