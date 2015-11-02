package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.librarys.PullHandler;
import com.loopeer.android.librarys.PullSwitchView;
import com.loopeer.android.librarys.SwitcherHolderImpl;

import java.util.List;

public class TestFragment3ViewPager extends Fragment {

    private SwitcherHolderImpl onPageChangeListener;
    private PullSwitchView pullSwitchView;

    private TestPagerAdapter mAdapter;
    private ViewPager mViewpager;
    private TabLayout mTabs;

    public static TestFragment3ViewPager newInstance(SwitcherHolderImpl onPageChange) {
        TestFragment3ViewPager testFragment = new TestFragment3ViewPager();
        testFragment.onPageChangeListener = onPageChange;
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
        mAdapter = new TestPagerAdapter(getChildFragmentManager(), onPageChangeListener);
        mViewpager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewpager);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentPullHandler(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewpager.post(new Runnable() {
            @Override
            public void run() {
                setCurrentPullHandler(0);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setCurrentPullHandler(mViewpager.getCurrentItem());
        }
    }

    private void setCurrentPullHandler(int position) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        Fragment resultFragment = null;
        for (Fragment fragment : fragments) {
            if (((TestFragment4)fragment).getPosition() == position) {
                resultFragment = fragment;
            }
        }
        if (resultFragment == null) resultFragment = fragments.get(0);
        pullSwitchView.setPullHandler((PullHandler) resultFragment);
    }

    private void initSwitchView(View view) {
        pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setSwitcherHolder(onPageChangeListener);
        pullSwitchView.disableWhenHorizontalMove(true);
    }

}
