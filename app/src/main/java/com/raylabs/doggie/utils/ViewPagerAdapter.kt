package com.raylabs.doggie.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    fragments: List<Fragment>,
    fragmentTitles: List<String>? = null
) : FragmentStateAdapter(fragmentActivity) {

    private val delegate = PagerDelegate.of(fragments, fragmentTitles)

    override fun getItemCount(): Int = delegate.count

    override fun createFragment(position: Int): Fragment = delegate.requireAt(position)

    fun getPageTitle(position: Int): String? = delegate.getTitleOrNull(position)
}
