package com.bedboy.ufebri.doggie.ui.categories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bedboy.ufebri.doggie.R;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.databinding.ItemImageCategoriesBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.Holder> {
    private List<DoggieEntity> mData;

    public CategoriesAdapter(List<DoggieEntity> mData) {
        this.mData = mData;
    }


    @NonNull
    @Override
    public CategoriesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageCategoriesBinding binding = ItemImageCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        final ItemImageCategoriesBinding binding;

        public Holder(@NonNull ItemImageCategoriesBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(int position) {
            Glide.with(itemView.getContext())
                    .load(mData.get(position).getLink())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_launcher_background)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.ivCategories);

            binding.tvCategories.setText(mData.get(position).getType());
        }
    }
}
