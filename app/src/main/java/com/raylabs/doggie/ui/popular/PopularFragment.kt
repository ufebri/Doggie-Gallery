package com.raylabs.doggie.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.databinding.FragmentPopularBinding
import com.raylabs.doggie.ui.ImagesAdapter
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.Status

class PopularFragment : Fragment() {

    private val imagesGrid = mutableListOf<DoggieEntity>()
    private var adapter: ImagesAdapter? = null
    private var binding: FragmentPopularBinding? = null
    private lateinit var viewModel: PopularViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentPopularBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(this, factory)[PopularViewModel::class.java]

        viewModel.image.observe(viewLifecycleOwner) { result ->
            val currentBinding = binding ?: return@observe
            when (result?.status) {
                Status.LOADING -> {
                    currentBinding.pbPopular.visibility = View.VISIBLE
                    currentBinding.rvPopular.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    val data = result.data.orEmpty()
                    imagesGrid.clear()
                    imagesGrid.addAll(data)

                    if (adapter == null) {
                        adapter = ImagesAdapter(imagesGrid) { item ->
                            if (!item.link.isNullOrEmpty()) {
                                DetailBottomSheetFragment.newInstance(item.link)
                                    .show(parentFragmentManager, DETAIL_SHEET_TAG)
                            }
                        }
                        currentBinding.rvPopular.apply {
                            layoutManager = StaggeredGridLayoutManager(
                                2,
                                StaggeredGridLayoutManager.VERTICAL
                            )
                            setHasFixedSize(true)
                            adapter = this@PopularFragment.adapter
                        }
                    } else {
                        adapter?.notifyDataSetChanged()
                    }

                    currentBinding.pbPopular.visibility = View.GONE
                    currentBinding.rvPopular.visibility =
                        if (data.isEmpty()) View.GONE else View.VISIBLE
                }

                Status.ERROR -> {
                    currentBinding.pbPopular.visibility = View.GONE
                    currentBinding.rvPopular.visibility = View.GONE
                    Toast.makeText(context, "Failed Get Image", Toast.LENGTH_SHORT).show()
                }

                null -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    companion object {
        private const val DETAIL_SHEET_TAG = "DetailBottomSheetFragmentTag"
    }
}
