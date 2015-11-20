package com.loopeer.android.librarys;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public abstract class SwitchFragmentAdapter extends SwitcherAdapter {
    private static final String TAG = "FragmentPagerAdapter";

    private final FragmentManager mFragmentManager;

    public SwitchFragmentAdapter(FragmentManager fm, SwitcherHolder switcherHolder) {
        mFragmentManager = fm;
        setOnPageChangeListener(switcherHolder);
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void replaceItem(ViewGroup container, int prePosition, int newPosition) {
        FragmentTransaction mCurTransaction = mFragmentManager.beginTransaction();

        if (prePosition == newPosition) {
            Fragment fragment = createFragment(container, newPosition);
            mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), newPosition)).commit(); // 隐藏当前的fragment，add下一个到Activity中
            return;
        }
        Fragment preFragment = createFragment(container, prePosition);
        Fragment newFragment = createFragment(container, newPosition);
        switchContent(container, preFragment, newFragment, prePosition, newPosition);
    }

    private Fragment createFragment(ViewGroup container, int newPosition) {
        Fragment fragment = retrieveFromCache(container, newPosition);
        if (null == fragment) {
            try {
                return getItem(newPosition);
            } catch (Exception e) {
                return null;
            }
        }
        return fragment;
    }

    public void switchContent(ViewGroup content, Fragment from, Fragment to, int prePosition, int newPosition) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (newPosition >= prePosition) {
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top,
                    R.anim.in_from_top, R.anim.out_to_bottom);
        } else {
            transaction.setCustomAnimations(R.anim.in_from_top, R.anim.out_to_bottom,
                    R.anim.in_from_top, R.anim.out_to_bottom);
        }
        if (!to.isAdded()) {
            transaction.hide(from).add(content.getId(), to, makeFragmentName(content.getId(), newPosition)).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
        checkAndDispatchSwitchListener(from, to);

    }

    private void checkAndDispatchSwitchListener(Fragment from, Fragment to) {
        if (from instanceof SwitchListener) {
            ((SwitchListener) from).onPagePause();
        }

        if (to instanceof SwitchListener) {
            ((SwitchListener) to).onPageResume();
        }
    }

    private Fragment retrieveFromCache(ViewGroup view, int menuItem) {
        if (mFragmentManager.getFragments() == null) return null;
        for (Fragment backFragment : mFragmentManager.getFragments()) {
            String name = makeFragmentName(view.getId(), menuItem);
            if (null != backFragment
                    && name.equals(backFragment.getTag())) {
                return backFragment;
            }
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    private static String makeFragmentName(int viewId, int id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
