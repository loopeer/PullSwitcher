package com.loopeer.android.librarys.pullswitcher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.loopeer.android.librarys.SwitcherHolderImpl;

public class TestPagerAdapter extends FragmentPagerAdapter {
    private SwitcherHolderImpl onPageChangeListener;

    public TestPagerAdapter(FragmentManager fm, SwitcherHolderImpl onPageChangeListener) {
        super(fm);
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment4.newInstance(onPageChangeListener, position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab: " + (position + 1);
    }

}

