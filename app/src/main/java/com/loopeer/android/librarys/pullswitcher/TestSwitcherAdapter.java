package com.loopeer.android.librarys.pullswitcher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.loopeer.android.librarys.SwitchFragmentAdapter;
import com.loopeer.android.librarys.SwitcherHolder;

public class TestSwitcherAdapter extends SwitchFragmentAdapter {

    public TestSwitcherAdapter(FragmentManager fm, SwitcherHolder switchHolder) {
        super(fm, switchHolder);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = TestFragment1Recycler.newInstance(getSwitcherHolder());
                break;
            case 1:
                fragment = TestFragment2Scroll.newInstance(getSwitcherHolder());
                break;
            case 2:
                fragment = TestFragment3ViewPager.newInstance(getSwitcherHolder());
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
