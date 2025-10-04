package com.raylabs.doggie.ui.categories.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ActivityBreedGalleryBinding
import com.raylabs.doggie.ui.common.PagingLoadStateAdapter
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment
import com.raylabs.doggie.utils.AdsHelper
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BreedGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreedGalleryBinding
    private lateinit var viewModel: BreedGalleryViewModel
    private lateinit var adapter: BreedImagesAdapter

    private lateinit var breed: String
    private var subBreed: String? = null
    private var displayName: String = ""
    private var collectJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityBreedGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft,
                systemBars.top,
                binding.toolbar.paddingRight,
                binding.toolbar.paddingBottom
            )
            binding.root.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        applySystemBarsStyle(binding.root, R.color.colorPrimaryDark, lightIcons = false)

        breed = intent.getStringExtra(EXTRA_BREED).orEmpty()
        subBreed = intent.getStringExtra(EXTRA_SUB_BREED)
        displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME).orEmpty()

        if (breed.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_missing_breed), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupList()
        setupAds()
        setupViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = displayName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.text_inverse))
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.text_inverse))
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun setupList() {
        adapter = BreedImagesAdapter { url ->
            if (url.isNotEmpty()) {
                DetailBottomSheetFragment.newInstance(url)
                    .show(supportFragmentManager, "DetailBottomSheetFragmentTag_BreedGallery")
            }
        }
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvBreedImages.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvBreedImages.setHasFixedSize(true)
        binding.rvBreedImages.itemAnimator = null
        binding.rvBreedImages.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadStates ->
            val refresh = loadStates.refresh
            val isLoading = refresh is androidx.paging.LoadState.Loading
            val isListEmpty = adapter.itemCount == 0
            binding.pbLoading.isVisible = isLoading && isListEmpty
            binding.rvBreedImages.isVisible = !isListEmpty || !isLoading

            val errorState = loadStates.append as? androidx.paging.LoadState.Error
                ?: loadStates.prepend as? androidx.paging.LoadState.Error
                ?: refresh as? androidx.paging.LoadState.Error
            errorState?.let {
                val message = it.error.localizedMessage ?: getString(R.string.error_image_load)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[BreedGalleryViewModel::class.java]
        viewModel.setBreed(breed, subBreed)
        collectImages()
    }

    private fun setupAds() {
        AdsHelper.init(applicationContext)
        AdsHelper.loadBanner(this, binding.adViewGallery)
    }

    private fun collectImages() {
        collectJob?.cancel()
        collectJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collectLatest { pagingData ->
                    adapter.submitData(lifecycle, pagingData)
                }
            }
        }
    }

    override fun onDestroy() {
        collectJob?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_BREED = "extra_breed"
        private const val EXTRA_SUB_BREED = "extra_sub_breed"
        private const val EXTRA_DISPLAY_NAME = "extra_display_name"

        fun createIntent(context: Context, category: BreedCategory): Intent {
            return Intent(context, BreedGalleryActivity::class.java).apply {
                putExtra(EXTRA_BREED, category.breed)
                putExtra(EXTRA_SUB_BREED, category.subBreed)
                putExtra(EXTRA_DISPLAY_NAME, category.displayName)
            }
        }
    }

    private fun applySystemBarsStyle(root: View, colorRes: Int, lightIcons: Boolean) {
        val controller = WindowCompat.getInsetsController(window, root)
        controller.isAppearanceLightStatusBars = lightIcons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = lightIcons
        }
        @Suppress("DEPRECATION")
        run {
            val color = ContextCompat.getColor(this, colorRes)
            window.statusBarColor = color
            window.navigationBarColor = color
        }
    }
}
