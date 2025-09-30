package com.raylabs.doggie.ui.detail

import android.Manifest
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.FragmentDetailBottomSheetBinding
import com.raylabs.doggie.utils.AdsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentDetailBottomSheetBinding? = null
    private var imageTarget: CustomTarget<Bitmap>? = null
    private var loadedBitmap: Bitmap? = null
    private var link: String? = null

    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private var pendingAction: PendingAction = PendingAction.NONE

    private enum class PendingAction { NONE, DOWNLOAD }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = arguments?.getString(ARG_LINK)

        storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (pendingAction == PendingAction.DOWNLOAD) {
                    if (isGranted) {
                        val context = context ?: return@registerForActivityResult
                        startDownload(context.applicationContext)
                    } else {
                        showToast(R.string.toast_permission_denied)
                    }
                }
                pendingAction = PendingAction.NONE
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentDetailBottomSheetBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog: Dialog? = dialog
        if (dialog is BottomSheetDialog) {
            val bottomSheetInternal =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        activity?.let { activity ->
            AdsHelper.init(activity.applicationContext)
            binding?.adViewBs?.let { container ->
                AdsHelper.loadBanner(activity, container)
            }
        }

        if (link.isNullOrEmpty()) {
            showToast(R.string.error_image_link_missing)
            dismissAllowingStateLoss()
            return
        }

        setUpActionButtons()
        setImage()
    }

    private fun setUpActionButtons() {
        val actionsBinding = binding?.llBsDetailImage ?: return
        actionsBinding.tvActionDownload.setOnClickListener { onDownloadClicked() }
        actionsBinding.tvActionSetWallpaper.setOnClickListener { onSetWallpaperClicked() }
        actionsBinding.tvActionShare.setOnClickListener { onShareClicked() }
        actionsBinding.tvActionCopy.setOnClickListener { onCopyClicked() }
    }

    private fun onDownloadClicked() {
        if (!ensureImageReady()) return
        val currentContext = context ?: return

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                currentContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            pendingAction = PendingAction.DOWNLOAD
            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        startDownload(currentContext.applicationContext)
    }

    private fun startDownload(appContext: android.content.Context) {
        showToast(R.string.toast_download_start)
        val bitmap = loadedBitmap ?: return
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val success = ImageActionHelper.saveBitmapToGallery(appContext, bitmap)
            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                showToast(if (success) R.string.toast_download_success else R.string.toast_download_failed)
            }
        }
    }

    private fun onSetWallpaperClicked() {
        if (!ensureImageReady()) return
        val currentContext = context ?: return
        val bitmap = loadedBitmap ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val success = ImageActionHelper.setWallpaper(currentContext, bitmap)
            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                showToast(if (success) R.string.toast_wallpaper_success else R.string.toast_wallpaper_failed)
            }
        }
    }

    private fun onShareClicked() {
        if (!ensureImageReady()) return
        val currentContext = context ?: return
        val bitmap = loadedBitmap ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val shareUri = ImageActionHelper.createShareUri(currentContext, bitmap)
            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext
                if (shareUri == null) {
                    showToast(R.string.toast_share_failed)
                    return@withContext
                }

                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(android.content.Intent.EXTRA_STREAM, shareUri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                runCatching {
                    startActivity(
                        android.content.Intent.createChooser(
                            shareIntent,
                            getString(R.string.share)
                        )
                    )
                }.onFailure {
                    showToast(R.string.toast_share_failed)
                }
            }
        }
    }

    private fun onCopyClicked() {
        val currentContext = context ?: return
        val imageLink = link ?: return
        val clipboard =
            currentContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                ?: return
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Doggie image", imageLink))
        showToast(R.string.toast_copy_success)
    }

    private fun ensureImageReady(): Boolean {
        if (loadedBitmap == null) {
            showToast(R.string.toast_image_not_ready)
            return false
        }
        return true
    }

    private fun setImage() {
        val currentContext = context ?: return
        val currentBinding = binding ?: return

        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                loadedBitmap = resource
                currentBinding.ivBsContent.setImageBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                loadedBitmap = null
                currentBinding.ivBsContent.setImageDrawable(placeholder)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                loadedBitmap = null
                currentBinding.ivBsContent.setImageResource(R.drawable.outline_broken_image_24)
            }
        }

        imageTarget = target

        Glide.with(currentContext)
            .asBitmap()
            .load(link)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(target)
    }

    override fun onDestroyView() {
        imageTarget?.let { Glide.with(this).clear(it) }
        imageTarget = null
        loadedBitmap = null
        binding = null
        super.onDestroyView()
    }

    private fun showToast(@StringRes messageRes: Int) {
        val currentContext = context ?: return
        Toast.makeText(currentContext, messageRes, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_LINK = "arg_link"

        fun newInstance(link: String): DetailBottomSheetFragment {
            return DetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LINK, link)
                }
            }
        }
    }
}
