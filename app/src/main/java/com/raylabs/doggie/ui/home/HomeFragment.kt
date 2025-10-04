package com.raylabs.doggie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.raylabs.doggie.R
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.databinding.FragmentHomeBinding
import com.raylabs.doggie.ui.ImagesAdapter
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.Status

class HomeFragment : Fragment() {

    private val imagesGrid = mutableListOf<DoggieEntity>()
    private var imagesAdapter: ImagesAdapter? = null
    private var binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.image.observe(viewLifecycleOwner) { result ->
            val currentBinding = binding ?: return@observe
            when (result?.status) {
                Status.LOADING -> {
                    currentBinding.pbHome.visibility = View.VISIBLE
                    currentBinding.recAnimal.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    val data = result.data.orEmpty()
                    imagesGrid.clear()
                    imagesGrid.addAll(data)

                    if (imagesAdapter == null) {
                        imagesAdapter = ImagesAdapter(imagesGrid) { item ->
                            if (!item.link.isNullOrEmpty()) {
                                DetailBottomSheetFragment.newInstance(item.link)
                                    .show(parentFragmentManager, DETAIL_SHEET_TAG)
                            }
                        }
                        currentBinding.recAnimal.apply {
                            layoutManager = StaggeredGridLayoutManager(
                                2,
                                StaggeredGridLayoutManager.VERTICAL
                            )
                            setHasFixedSize(true)
                            adapter = imagesAdapter
                        }
                    } else {
                        imagesAdapter?.notifyDataSetChanged()
                    }

                    currentBinding.pbHome.visibility = View.GONE
                    currentBinding.recAnimal.visibility =
                        if (data.isEmpty()) View.GONE else View.VISIBLE
                }

                Status.ERROR -> {
                    currentBinding.pbHome.visibility = View.GONE
                    currentBinding.recAnimal.visibility = View.GONE
                    Toast.makeText(
                        context,
                        getString(R.string.error_image_load),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                null -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        imagesAdapter = null
    }

    companion object {
        private const val DETAIL_SHEET_TAG = "DetailBottomSheetFragmentTag"
    }
}
