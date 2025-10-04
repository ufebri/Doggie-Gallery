package com.raylabs.doggie.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final PagerDelegate<Fragment> delegate;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.delegate = PagerDelegate.of(fragments, null);
    }

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments, List<String> fragmentTitles) {
        super(fragmentActivity);
        this.delegate = PagerDelegate.of(fragments, fragmentTitles);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return delegate.requireAt(position);
    }

    @Override
    public int getItemCount() {
        return delegate.getCount();
    }

    public String getPageTitle(int position) {
        return delegate.getTitleOrNull(position);
    }
}
