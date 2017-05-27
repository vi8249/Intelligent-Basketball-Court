package com.example.vi8249.intelligentbasketballcourt;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private android.support.design.widget.TabLayout mTabs;
    private ViewPager mViewPager;

    public static int lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                lastPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        setupViewPager(mViewPager);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        CourtPagerAdapter adapter = new CourtPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CourtActivity(), "Court 1");
        adapter.addFragment(new CourtActivity2(), "Court 2");

        viewPager.setAdapter(adapter);
    }

    private class CourtPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        CourtPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
