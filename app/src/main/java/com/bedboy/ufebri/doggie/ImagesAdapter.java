package com.bedboy.ufebri.doggie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by user on 5/7/18.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Holder> {
    private List<String> imagesGrid;
    Context context;

    public ImagesAdapter(List<String> imagesGrid) {
        this.imagesGrid = imagesGrid;
    }


    @NonNull
    @Override
    public ImagesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,
                parent, false);
        context = parent.getContext();
        return new Holder(view);
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
        public Holder(View itemView) {
            super(itemView);
        }

        public void bind(int position) {
            String gambar = imagesGrid.get(position);
            ImageView gambarBinatang = itemView.findViewById(R.id.iv_animal);

            Glide.with(context)
                    .load(gambar)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(gambarBinatang);
        }
    }
}
