package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.librarys.PullSwitchView;
import com.loopeer.android.librarys.SwitcherHolder;

public class TestFragment3ViewPager extends Fragment {

    private SwitcherHolder switcherHolder;
    private PullSwitchView pullSwitchView;

    private TestPagerAdapter mAdapter;
    private ViewPager mViewpager;
    private TabLayout mTabs;

    public static TestFragment3ViewPager newInstance(SwitcherHolder switcherHolder) {
        TestFragment3ViewPager testFragment = new TestFragment3ViewPager();
        testFragment.switcherHolder = switcherHolder;
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSwitchView(view);
        initPagerTabs(view);
    }

    private void initPagerTabs(View view) {
        mViewpager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabs = (TabLayout) view.findViewById(R.id.tabs);
        mAdapter = new TestPagerAdapter(getChildFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewpager);
    }

    private void initSwitchView(View view) {
        pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setSwitcherHolder(switcherHolder);
        pullSwitchView.disableWhenHorizontalMove(true);
    }

}
