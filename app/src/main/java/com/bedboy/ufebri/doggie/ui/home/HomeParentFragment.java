package com.bedboy.ufebri.doggie.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bedboy.ufebri.doggie.databinding.FragmentHomeParentBinding;
import com.bedboy.ufebri.doggie.ui.categories.CategoriesFragment;
import com.bedboy.ufebri.doggie.ui.liked.LikedFragment;
import com.bedboy.ufebri.doggie.ui.popular.PopularFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeParentFragment extends Fragment {

    private FragmentHomeParentBinding binding;
    private HomeFragment homeFragment;
    private PopularFragment popularFragment;
    private LikedFragment likedFragment;
    private CategoriesFragment categoriesFragment;

    private final String[] titles = new String[]{"For You", "Most Popular", "Most Liked", "Categories"};

    public HomeParentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeParentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeFragment = new HomeFragment();
        popularFragment = new PopularFragment();
        likedFragment = new LikedFragment();
        categoriesFragment = new CategoriesFragment();

        binding.vpHome.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(binding.tlMain, binding.vpHome,
                ((tab, position) -> tab.setText(titles[position]))).attach();

        populateTabLayout();
    }


    private void populateTabLayout() {

        //Setup Margin TabLayout
        int betweenSpace = 5;
        ViewGroup slidingTabStrip = (ViewGroup) binding.tlMain.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = betweenSpace;
        }
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return homeFragment;
                case 1:
                    return popularFragment;
                case 2:
                    return likedFragment;
                case 3:
                    return categoriesFragment;
            }
            return new HomeFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}
