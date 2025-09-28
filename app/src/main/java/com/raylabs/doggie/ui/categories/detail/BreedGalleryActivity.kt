package com.raylabs.doggie.ui.categories.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        binding = ActivityBreedGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        breed = intent.getStringExtra(EXTRA_BREED).orEmpty()
        subBreed = intent.getStringExtra(EXTRA_SUB_BREED)
        displayName = intent.getStringExtra(EXTRA_DISPLAY_NAME).orEmpty()

        if (breed.isEmpty()) {
            Toast.makeText(this, "Breed not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupList()
        setupAds()
        setupViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = displayName
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
                Toast.makeText(
                    this,
                    it.error.localizedMessage ?: "Failed load images",
                    Toast.LENGTH_SHORT
                ).show()
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
}
