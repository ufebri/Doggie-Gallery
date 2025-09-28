package com.raylabs.doggie.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.raylabs.doggie.R
import com.raylabs.doggie.ui.categories.CategoriesFragment
import com.raylabs.doggie.ui.home.HomeFragment
import com.raylabs.doggie.ui.liked.LikedFragment
import com.raylabs.doggie.ui.popular.PopularFragment
import com.raylabs.doggie.utils.AdsHelper
import com.raylabs.doggie.utils.ViewPagerAdapter
import com.raylabs.doggie.utils.tab.AndroidDividerController
import com.raylabs.doggie.utils.tab.TabDividerDelegate
import com.raylabs.doggie.utils.tab.TabTitleDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tabLayout = findViewById(R.id.tl_main)
        val viewPager: ViewPager2 = findViewById(R.id.vp_home)
        val adContainer: FrameLayout = findViewById(R.id.adView)

        AdsHelper.init(this)
        AdsHelper.loadBanner(this, adContainer)

        val fragments: List<Fragment> = listOf(
            HomeFragment(),
            PopularFragment(),
            LikedFragment(),
            CategoriesFragment()
        )

        viewPager.adapter = ViewPagerAdapter(this, fragments)
        val titlesArray = resources.getStringArray(R.array.tab_title_main)
        val titleDelegate = TabTitleDelegate(titlesArray)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titleDelegate.requireAt(position)
        }.attach()

        populateTabLayout()
    }

    private fun populateTabLayout() {
        val slidingTabStrip = tabLayout.getChildAt(0) as? ViewGroup ?: return
        TabDividerDelegate.apply(AndroidDividerController(slidingTabStrip))
    }
}
