package com.example.vi8249.intelligentbasketballcourt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int lastPosition = 0;
    private android.support.design.widget.TabLayout mTabs;
    private CustomViewPager mViewPager;
    private CourtActivity courtActivity1 = null;
    private CourtActivity2 courtActivity2 = null;
    private CourtAvailableChartActivity courtAvailableChartActivity = null;
    private CourtTemperatureChartActivity temperatureChartActivity = null;

    private String court1Temperature, court1Humidity, court2Temperature, court2Humidity, court1Battery;
    private boolean court1LeftCourt, court1RightCourt;
    private ArrayList<LeftCourtData> lDataList = null;
    private ArrayList<RightCourtData> rDataList = null;
    private ArrayList<TemperatureData> tDataList = null;
    private ArrayList<HumidityData> hDataList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent it = getIntent();
        court1Temperature = it.getStringExtra("court1Temperature");
        court1Humidity = it.getStringExtra("court1Humidity");
        court1LeftCourt = it.getBooleanExtra("court1LeftCourt", true);
        court1RightCourt = it.getBooleanExtra("court1RightCourt", true);
        court2Temperature = it.getStringExtra("court2Temperature");
        court2Humidity = it.getStringExtra("court2Humidity");
        lDataList = it.getParcelableArrayListExtra("leftCourtList");
        rDataList = it.getParcelableArrayListExtra("rightCourtList");
        tDataList = it.getParcelableArrayListExtra("temperatureList");
        hDataList = it.getParcelableArrayListExtra("humidityList");
        court1Battery = it.getStringExtra("court1Battery");

        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);

        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);
    }

    // Add TabView
    private void setupViewPager(CustomViewPager viewPager) {
        CourtPagerAdapter adapter = new CourtPagerAdapter(getSupportFragmentManager());
        if (courtActivity1 == null) {
            courtActivity1 = new CourtActivity();
            courtActivity1.Initialize(court1Temperature, court1Humidity, court1LeftCourt, court1RightCourt, court1Battery);
            adapter.addFragment(courtActivity1, "Court 1");
        }
        if (courtActivity2 == null) {
            courtActivity2 = new CourtActivity2();
            courtActivity2.Initialize(court2Temperature, court2Humidity, true, false);
            adapter.addFragment(courtActivity2, "Court 2");
        }
        if (courtAvailableChartActivity == null) {
            courtAvailableChartActivity = new CourtAvailableChartActivity();
            courtAvailableChartActivity.Initialize(lDataList, rDataList);
            adapter.addFragment(courtAvailableChartActivity, "Court Chart");
        }
        if (temperatureChartActivity == null) {
            temperatureChartActivity = new CourtTemperatureChartActivity();
            //Log.d("temp", tDataList.size() + " " + hDataList.size());
            temperatureChartActivity.Initialize(tDataList, hDataList);
            adapter.addFragment(temperatureChartActivity, "Weather Chart");
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
