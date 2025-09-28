package com.raylabs.doggie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.raylabs.doggie.databinding.FragmentHomeParentBinding
import com.raylabs.doggie.ui.categories.CategoriesFragment
import com.raylabs.doggie.ui.liked.LikedFragment
import com.raylabs.doggie.ui.popular.PopularFragment

class HomeParentFragment : Fragment() {

    private var binding: FragmentHomeParentBinding? = null

    private val titles = arrayOf("For You", "Most Popular", "Most Liked", "Categories")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHomeParentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragments = listOf(
            HomeFragment(),
            PopularFragment(),
            LikedFragment(),
            CategoriesFragment()
        )

        binding?.let { binding ->
            binding.vpHome.adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
            }

            TabLayoutMediator(binding.tlMain, binding.vpHome) { tab, position ->
                tab.text = titles.getOrNull(position)
            }.attach()

            populateTabLayout(binding)
        }
    }

    private fun populateTabLayout(binding: FragmentHomeParentBinding) {
        val slidingTabStrip = binding.tlMain.getChildAt(0) as? ViewGroup ?: return
        val betweenSpace = 5
        repeat(slidingTabStrip.childCount - 1) { index ->
            val child = slidingTabStrip.getChildAt(index)
            val params = child.layoutParams as? ViewGroup.MarginLayoutParams ?: return@repeat
            params.rightMargin = betweenSpace
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
