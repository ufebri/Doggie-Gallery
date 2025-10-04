package com.raylabs.doggie.ui.search

import android.R.id.content
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ActivitySearchBinding
import com.raylabs.doggie.ui.categories.detail.BreedGalleryActivity
import com.raylabs.doggie.utils.AdsHelper
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.BreedCategory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchCategoriesAdapter
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(content)
            duration = DEFAULT_TRANSITION_DURATION
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(content)
            duration = DEFAULT_TRANSITION_DURATION
        }

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.searchRoot) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBar.updatePadding(top = systemBars.top)
            binding.contentContainer.updatePadding(bottom = systemBars.bottom)
            insets
        }

        applySystemBarsStyle(binding.root, R.color.colorPrimaryDark, lightIcons = false)

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnClear.setOnClickListener { binding.etSearch.text?.clear() }

        AdsHelper.init(applicationContext)
        AdsHelper.loadBanner(this, binding.searchAdContainer)

        val factory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        setupRecyclerView()

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            binding.btnClear.isVisible = query.isNotEmpty()
            viewModel.onQueryChanged(query)
        }

        viewModel.state.observe(this) { state ->
            binding.pbLoading.visibility =
                if (state.isLoading) View.VISIBLE else View.GONE
            binding.tvEmptyState.visibility =
                if (state.showEmptyState) View.VISIBLE else View.GONE

            adapter.submitCategories(state.results)

            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.etSearch.requestFocus()
        showKeyboard()
    }

    private fun setupRecyclerView() {
        adapter = SearchCategoriesAdapter(
            onItemClick = this::openCategoryDetail,
            onPreviewMissing = viewModel::onPreviewRequested
        )
        binding.rvResults.adapter = adapter
        binding.rvResults.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvResults.setHasFixedSize(true)
    }

    private fun openCategoryDetail(category: BreedCategory) {
        val intent = BreedGalleryActivity.createIntent(this, category)
        startActivity(intent)
    }

    private fun showKeyboard() {
        binding.etSearch.post {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    companion object {
        private const val DEFAULT_TRANSITION_DURATION = 300L

        fun buildOptions(activity: AppCompatActivity, sharedView: View): Bundle? {
            return ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                sharedView,
                sharedView.transitionName
            ).toBundle()
        }
    }

    private fun applySystemBarsStyle(root: View, colorRes: Int, lightIcons: Boolean) {
        val controller = WindowCompat.getInsetsController(window, root)
        controller?.isAppearanceLightStatusBars = lightIcons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller?.isAppearanceLightNavigationBars = lightIcons
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @Suppress("DEPRECATION")
            run {
                val color = ContextCompat.getColor(this, colorRes)
                window.statusBarColor = color
                window.navigationBarColor = color
            }
        }
    }
}
