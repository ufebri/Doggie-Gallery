package com.raylabs.doggie.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.FragmentCategoriesBinding
import com.raylabs.doggie.ui.categories.detail.BreedGalleryActivity
import com.raylabs.doggie.ui.common.PagingLoadStateAdapter
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.BreedCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private var loadJob: Job? = null
    private lateinit var adapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return
        val factory = ViewModelFactory.getInstance(activity.application)
        val viewModel = ViewModelProvider(this, factory)[CategoriesViewModel::class.java]

        adapter = CategoriesAdapter(
            onItemClick = this::openCategoryDetail,
            onPreviewMissing = viewModel::requestPreview
        )
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.setHasFixedSize(true)
        binding.rvCategories.itemAnimator = null
        binding.rvCategories.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener { loadStates ->
            val refresh = loadStates.refresh
            val isLoading = refresh is androidx.paging.LoadState.Loading
            val isListEmpty = adapter.itemCount == 0
            binding.pbCategories.isVisible = isLoading && isListEmpty
            binding.rvCategories.isVisible = !isListEmpty || !isLoading

            val errorState = loadStates.append as? androidx.paging.LoadState.Error
                ?: loadStates.prepend as? androidx.paging.LoadState.Error
                ?: refresh as? androidx.paging.LoadState.Error
            errorState?.let {
                val message = it.error.localizedMessage
                    ?: getString(R.string.error_image_load)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        collectCategories(viewModel)
    }

    private fun collectCategories(viewModel: CategoriesViewModel) {
        loadJob?.cancel()
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest { pagingData ->
                    adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                }
            }
        }
    }

    private fun openCategoryDetail(category: BreedCategory) {
        val activity = activity ?: return
        startActivity(BreedGalleryActivity.createIntent(activity, category))
    }

    override fun onDestroyView() {
        loadJob?.cancel()
        _binding = null
        super.onDestroyView()
    }
}
