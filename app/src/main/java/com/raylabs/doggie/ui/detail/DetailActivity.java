package com.raylabs.doggie.ui.detail;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.raylabs.doggie.doggie.databinding.ActivityDetailBinding;
import com.raylabs.doggie.doggie.databinding.ItemDetailImageBinding;
import com.raylabs.doggie.utils.GeneralHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ItemDetailImageBinding itemSheet;
    private String link;
    BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        itemSheet = binding.llDetailImage;
        View view = binding.getRoot();
        setContentView(view);

        //Handle Intent
        link = getIntent().getStringExtra("link");
        setImage();
    }

    private void setImage() {
        Glide.with(this)
                .asBitmap()
                .load(link)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int heightValue = GeneralHelper.pxToDp(resource.getHeight());
                        binding.ivContent.setImageBitmap(resource);
                        populateBottomSheet(heightValue);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void populateBottomSheet(int heightValue) {
        bottomSheetBehavior = BottomSheetBehavior.from(itemSheet.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //handle height
        if (heightValue < 250) {
            bottomSheetBehavior.setPeekHeight(450);
        } else {
            bottomSheetBehavior.setPeekHeight(500);
        }
    }
}