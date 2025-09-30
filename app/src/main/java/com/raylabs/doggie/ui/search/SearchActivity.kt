package com.raylabs.doggie.ui.search

import android.R.id.content
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ActivitySearchBinding
import com.raylabs.doggie.ui.categories.detail.BreedGalleryActivity
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
            scrimColor = TRANSPARENT
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(content)
            duration = DEFAULT_TRANSITION_DURATION
            scrimColor = TRANSPARENT
        }

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.searchRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnFilter.setOnClickListener {
            Toast.makeText(this, R.string.content_desc_filter, Toast.LENGTH_SHORT).show()
        }

        val factory = ViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        setupRecyclerView()

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
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
}