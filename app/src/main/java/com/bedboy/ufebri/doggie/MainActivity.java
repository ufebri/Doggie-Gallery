package com.bedboy.ufebri.doggie;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bedboy.ufebri.doggie.ui.categories.CategoriesFragment;
import com.bedboy.ufebri.doggie.ui.home.HomeFragment;
import com.bedboy.ufebri.doggie.ui.liked.LikedFragment;
import com.bedboy.ufebri.doggie.ui.popular.PopularFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView navViewHome;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private HomeFragment homeFragment;
    private PopularFragment popularFragment;
    private LikedFragment likedFragment;
    private CategoriesFragment categoriesFragment;

    private final String[] titles = new String[]{"For You", "Most Popular", "Most Liked", "Categories"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tl_main);
        viewPager = findViewById(R.id.vp_home);

        homeFragment = new HomeFragment();
        popularFragment = new PopularFragment();
        likedFragment = new LikedFragment();
        categoriesFragment = new CategoriesFragment();

        viewPager.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager,
                ((tab, position) -> tab.setText(titles[position]))).attach();

        populateTabLayout();
    }

    private void populateTabLayout() {

        //Setup Margin TabLayout
        int betweenSpace = 5;
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = betweenSpace;
        }
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull MainActivity mainActivity) {
            super(mainActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                case 1:
                case 2:
                    return new HomeFragment();
                case 3:
                    return new CategoriesFragment();
            }
            return new HomeFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }


}
