package com.raylabs.doggie.ui.liked

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
import com.raylabs.doggie.databinding.FragmentLikedBinding
import com.raylabs.doggie.ui.ImagesAdapter
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment
import com.raylabs.doggie.viewmodel.ViewModelFactory
import com.raylabs.doggie.vo.Status

class LikedFragment : Fragment() {

    private val imagesGrid = mutableListOf<DoggieEntity>()
    private var adapter: ImagesAdapter? = null
    private var binding: FragmentLikedBinding? = null
    private lateinit var viewModel: LikedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentLikedBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return
        val factory = ViewModelFactory.getInstance(activity.application)
        viewModel = ViewModelProvider(this, factory)[LikedViewModel::class.java]

        viewModel.image.observe(viewLifecycleOwner) { result ->
            val currentBinding = binding ?: return@observe
            imagesGrid.clear()

            when (result?.status) {
                Status.LOADING -> {
                    currentBinding.pbLiked.visibility = View.VISIBLE
                    currentBinding.rvLiked.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    val data = result.data.orEmpty()
                    imagesGrid.addAll(data)

                    if (adapter == null) {
                        adapter = ImagesAdapter(imagesGrid) { item ->
                            if (!item.link.isNullOrEmpty()) {
                                DetailBottomSheetFragment.newInstance(item.link)
                                    .show(parentFragmentManager, DETAIL_SHEET_TAG)
                            }
                        }
                        currentBinding.rvLiked.apply {
                            layoutManager = StaggeredGridLayoutManager(
                                2,
                                StaggeredGridLayoutManager.VERTICAL
                            )
                            setHasFixedSize(true)
                            adapter = this@LikedFragment.adapter
                        }
                    } else {
                        adapter?.notifyDataSetChanged()
                    }

                    currentBinding.pbLiked.visibility = View.GONE
                    if (data.isEmpty()) {
                        currentBinding.rvLiked.visibility = View.GONE
                        Toast.makeText(
                            context,
                            getString(R.string.empty_liked_images),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        currentBinding.rvLiked.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    currentBinding.pbLiked.visibility = View.GONE
                    currentBinding.rvLiked.visibility = View.GONE
                    Toast.makeText(
                        context,
                        getString(R.string.error_liked_images),
                        Toast.LENGTH_SHORT
                    )
                        .show()
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
