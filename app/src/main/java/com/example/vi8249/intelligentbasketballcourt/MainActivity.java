package com.example.vi8249.intelligentbasketballcourt;

import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private android.support.design.widget.TabLayout mTabs;
    private ViewPager mViewPager;

    private CourtActivity courtActivity1 = null;
    private CourtActivity2 courtActivity2 = null;
    private DataChartActivity dataChartActivity = null;

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
        mViewPager.setOffscreenPageLimit(5);
        setupViewPager(mViewPager);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);
    }

    // Add TabView
    private void setupViewPager(ViewPager viewPager) {
        CourtPagerAdapter adapter = new CourtPagerAdapter(getSupportFragmentManager());
        if(courtActivity1 == null) {
            courtActivity1 = new CourtActivity();
            adapter.addFragment(courtActivity1, "Court 1");
        }
        if(courtActivity2 == null) {
            courtActivity2 = new CourtActivity2();
            adapter.addFragment(courtActivity2, "Court 2");
        }
        if(dataChartActivity == null) {
            dataChartActivity = new DataChartActivity();
            adapter.addFragment(dataChartActivity, "Data Chart");
        }
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
