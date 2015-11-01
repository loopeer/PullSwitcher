package com.loopeer.android.librarys.pullswitcher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.loopeer.android.librarys.SwitchFragmentAdapter;
import com.loopeer.android.librarys.SwitcherHolder;

public class TestSwitcherAdapter extends SwitchFragmentAdapter {

    public TestSwitcherAdapter(FragmentManager fm, SwitcherHolder mSwitchHolder) {
        super(fm);
        setOnPageChangeListener(mSwitchHolder);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = TestFragment1.newInstance(onPageChangeListener);
                break;
            case 1:
                fragment = TestFragment2.newInstance(onPageChangeListener);
                break;
            case 2:
                fragment = TestFragment3.newInstance(onPageChangeListener);
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
