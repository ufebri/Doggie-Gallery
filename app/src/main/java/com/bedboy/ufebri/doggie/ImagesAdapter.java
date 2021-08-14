package com.bedboy.ufebri.doggie;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.databinding.ItemImageBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by user on 5/7/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Holder> {
    private List<DoggieEntity> imagesGrid;

    public ImagesAdapter(List<DoggieEntity> imagesGrid) {
        this.imagesGrid = imagesGrid;
    }


    @NonNull
    @Override
    public ImagesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesAdapter.Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return imagesGrid.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        final ItemImageBinding binding;

        Holder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int position) {
            Glide.with(itemView.getContext())
                    .load(imagesGrid.get(position))
                    .apply(RequestOptions.overrideOf(180,250))
                    .into(binding.ivAnimal);
        }
    }
}
