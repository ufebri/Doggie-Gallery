package com.raylabs.doggie.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.raylabs.doggie.R;
import com.raylabs.doggie.ui.categories.CategoriesFragment;
import com.raylabs.doggie.ui.home.HomeFragment;
import com.raylabs.doggie.ui.liked.LikedFragment;
import com.raylabs.doggie.ui.popular.PopularFragment;
import com.raylabs.doggie.utils.AdsHelper;
import com.raylabs.doggie.utils.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tl_main);
        ViewPager2 viewPager = findViewById(R.id.vp_home);
        FrameLayout adContainer = findViewById(R.id.adView); // Assign to FrameLayout

        AdsHelper.init(this);
        AdsHelper.loadBanner(adContainer); // Pass FrameLayout to loadBanner

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new PopularFragment());
        fragments.add(new LikedFragment());
        fragments.add(new CategoriesFragment());

        viewPager.setAdapter(new ViewPagerAdapter(this, fragments));
        String[] titles = getResources().getStringArray(R.array.tab_title_main);
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

}
