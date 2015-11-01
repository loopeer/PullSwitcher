package com.loopeer.android.librarys.pullswitcher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.loopeer.android.librarys.SwitchFragmentAdapter;

public class DefaultSwitchFragmentAdapter extends SwitchFragmentAdapter {

    public DefaultSwitchFragmentAdapter(FragmentManager fm, ViewGroup container) {
        super(fm, container);
        initData();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = TestFragment1.newInstance(null);
                break;
            case 1:
                fragment = TestFragment2.newInstance(null);
                break;
            default:
                fragment = null;
        }
        return fragment;
    }
}
