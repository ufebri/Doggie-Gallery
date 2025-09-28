package com.raylabs.doggie.ui.detail;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.raylabs.doggie.R;
import com.raylabs.doggie.databinding.FragmentDetailBottomSheetBinding;
import com.raylabs.doggie.databinding.ItemDetailImageBinding;
import com.raylabs.doggie.utils.AdsHelper;
import com.raylabs.doggie.utils.image.ClipboardHelper;
import com.raylabs.doggie.utils.image.ImageSaver;
import com.raylabs.doggie.utils.image.ShareHelper;
import com.raylabs.doggie.utils.image.WallpaperHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_LINK = "arg_link";
    private FragmentDetailBottomSheetBinding binding;
    private CustomTarget<Bitmap> imageTarget;
    private String link;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    // helpers
    private final ImageSaver imageSaver = new ImageSaver();
    private final ShareHelper shareHelper = new ShareHelper();
    private Bitmap loadedBitmap;
    private ActivityResultLauncher<String> storagePermissionLauncher;
    private PendingAction pendingAction = PendingAction.NONE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(ARG_LINK);
        }

        storagePermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (pendingAction == PendingAction.DOWNLOAD) {
                        if (isGranted && getContext() != null) {
                            performDownload(requireContext().getApplicationContext());
                        } else {
                            showToast(R.string.toast_permission_denied);
                        }
                    }
                    pendingAction = PendingAction.NONE;
                });
    }

    public static DetailBottomSheetFragment newInstance(String link) {
        DetailBottomSheetFragment fragment = new DetailBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Dialog dialog = getDialog();
        if (dialog instanceof BottomSheetDialog bottomSheetDialog) {
            FrameLayout bottomSheetInternal =
                    bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

        if (getActivity() != null) {
            AdsHelper.init(requireActivity().getApplicationContext());
            AdsHelper.loadBanner(requireActivity(), binding.adViewBs);
        }

        if (link == null || link.isEmpty()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), R.string.error_image_link_missing, Toast.LENGTH_SHORT).show();
            }
            dismiss();
            return;
        }
        setUpActionButtons();
        setImage();
    }

    private void setImage() {
        if (getContext() == null) return;
        imageTarget = new CustomTarget<>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource,
                                        @Nullable Transition<? super Bitmap> transition) {
                if (binding != null) {
                    binding.ivBsContent.setImageBitmap(resource);
                }
                loadedBitmap = resource;
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                if (binding != null) {
                    binding.ivBsContent.setImageDrawable(placeholder);
                }
                loadedBitmap = null;
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (binding != null) {
                    binding.ivBsContent.setImageResource(R.drawable.outline_broken_image_24);
                }
                loadedBitmap = null;
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
        loadedBitmap = null;
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        ioExecutor.shutdownNow();
        super.onDestroy();
    }

    private void setUpActionButtons() {
        if (binding == null) return;
        ItemDetailImageBinding actions = binding.llBsDetailImage;
        actions.tvActionDownload.setOnClickListener(v -> onDownloadClicked());
        actions.tvActionSetWallpaper.setOnClickListener(v -> onSetWallpaperClicked());
        actions.tvActionShare.setOnClickListener(v -> onShareClicked());
        actions.tvActionCopy.setOnClickListener(v -> onCopyClicked());
    }

    private void onDownloadClicked() {
        if (ensureImageReady()) return;
        Context context = getContext();
        if (context == null) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            pendingAction = PendingAction.DOWNLOAD;
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        performDownload(context.getApplicationContext());
    }

    private void performDownload(Context context) {
        showToast(R.string.toast_download_start);
        ioExecutor.execute(() -> {
            boolean success = imageSaver.saveToGallery(context, loadedBitmap);
            runOnMain(() -> {
                if (!isAdded()) return;
                showToast(success ? R.string.toast_download_success : R.string.toast_download_failed);
            });
        });
    }

    private void onSetWallpaperClicked() {
        if (ensureImageReady()) return;
        Context context = getContext();
        if (context == null) return;

        ioExecutor.execute(() -> {
            boolean success = WallpaperHelper.INSTANCE.setWallpaper(context, loadedBitmap);
            runOnMain(() -> {
                if (!isAdded()) return;
                showToast(success ? R.string.toast_wallpaper_success : R.string.toast_wallpaper_failed);
            });
        });
    }

    private void onShareClicked() {
        if (ensureImageReady()) return;
        Context context = getContext();
        if (context == null) return;

        ioExecutor.execute(() -> {
            Uri uri = shareHelper.createShareUri(context, loadedBitmap);
            runOnMain(() -> {
                if (!isAdded()) return;
                if (uri == null) {
                    showToast(R.string.toast_share_failed);
                    return;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                } catch (Exception e) {
                    showToast(R.string.toast_share_failed);
                }
            });
        });
    }

    private void onCopyClicked() {
        Context context = getContext();
        if (context == null || link == null || link.isEmpty()) return;
        if (ClipboardHelper.INSTANCE.copyText(context, "Doggie image", link)) {
            showToast(R.string.toast_copy_success);
        }
    }

    private boolean ensureImageReady() {
        if (loadedBitmap == null) {
            showToast(R.string.toast_image_not_ready);
            return true;
        }
        return false;
    }

    private void runOnMain(Runnable r) {
        requireActivity().runOnUiThread(r);
    }

    private void showToast(@StringRes int res) {
        Context ctx = getContext();
        if (ctx != null) Toast.makeText(ctx, res, Toast.LENGTH_SHORT).show();
    }

    private enum PendingAction {NONE, DOWNLOAD}
}