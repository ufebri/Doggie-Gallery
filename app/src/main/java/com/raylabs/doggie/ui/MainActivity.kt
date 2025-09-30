package com.raylabs.doggie.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.raylabs.doggie.R
import com.raylabs.doggie.databinding.ActivityMainBinding
import com.raylabs.doggie.ui.categories.CategoriesFragment
import com.raylabs.doggie.ui.home.HomeFragment
import com.raylabs.doggie.ui.liked.LikedFragment
import com.raylabs.doggie.ui.popular.PopularFragment
import com.raylabs.doggie.ui.search.SearchActivity
import com.raylabs.doggie.utils.AdsHelper
import com.raylabs.doggie.utils.ViewPagerAdapter
import com.raylabs.doggie.utils.tab.AndroidDividerController
import com.raylabs.doggie.utils.tab.TabDividerDelegate
import com.raylabs.doggie.utils.tab.TabTitleDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdsHelper.init(this)
        AdsHelper.loadBanner(this, binding.nativeAdContainer)

        val fragments: List<Fragment> = listOf(
            HomeFragment(),
            PopularFragment(),
            LikedFragment(),
            CategoriesFragment()
        )

        binding.vpHome.adapter = ViewPagerAdapter(this, fragments)
        val titlesArray = resources.getStringArray(R.array.tab_title_main)
        val titleDelegate = TabTitleDelegate(titlesArray)

        TabLayoutMediator(binding.tlMain, binding.vpHome) { tab, position ->
            tab.text = titleDelegate.requireAt(position)
        }.attach()

        populateTabLayout()

        binding.searchBarContainer.setOnClickListener { view ->
            val intent = Intent(this, SearchActivity::class.java)
            val options = SearchActivity.buildOptions(this, view)
            startActivity(intent, options)
        }
    }

    private fun populateTabLayout() {
        val slidingTabStrip = binding.tlMain.getChildAt(0) as? ViewGroup ?: return
        TabDividerDelegate.apply(AndroidDividerController(slidingTabStrip))
    }
}
