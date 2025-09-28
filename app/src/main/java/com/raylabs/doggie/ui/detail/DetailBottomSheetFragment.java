package com.raylabs.doggie.ui.detail;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raylabs.doggie.R;
import com.raylabs.doggie.databinding.FragmentDetailBottomSheetBinding;
import com.raylabs.doggie.utils.AdsHelper;

public class DetailBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_LINK = "arg_link";
    private FragmentDetailBottomSheetBinding binding;
    private CustomTarget<Bitmap> imageTarget;
    private String link;

    public static DetailBottomSheetFragment newInstance(String link) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(ARG_LINK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Dialog dialog = getDialog();
        if (dialog instanceof BottomSheetDialog bottomSheetDialog) {
            FrameLayout bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

        // Inisialisasi AdsHelper dengan ApplicationContext dari Activity
        // Ini hanya untuk MobileAds.initialize(), loadBanner akan menggunakan Activity secara langsung
        if (getActivity() != null) {
            AdsHelper.init(requireActivity().getApplicationContext());
            AdsHelper.loadBanner(requireActivity(), binding.adViewBs); // Mengirim Activity
        }

        if (link == null || link.isEmpty()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.error_image_link_missing, Toast.LENGTH_SHORT).show();
            }
            dismiss();
            return;
        }
        setImage();
    }

    private void setImage() {
        if (getContext() == null) return;

        final FragmentDetailBottomSheetBinding currentBinding = binding;
        imageTarget = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (currentBinding != null) {
                    currentBinding.ivBsContent.setImageBitmap(resource);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                if (currentBinding != null) {
                    currentBinding.ivBsContent.setImageDrawable(placeholder);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (currentBinding != null) {
                    currentBinding.ivBsContent.setImageResource(R.drawable.outline_broken_image_24);
                }
            }
        };

        Glide.with(getContext())
                .asBitmap()
                .load(link)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageTarget);
    }

    @Override
    public void onDestroyView() {
        if (imageTarget != null) {
            Glide.with(this).clear(imageTarget);
            imageTarget = null;
        }
        super.onDestroyView();
        binding = null;
    }
}
