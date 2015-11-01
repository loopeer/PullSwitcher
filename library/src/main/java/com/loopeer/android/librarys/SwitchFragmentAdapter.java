package com.loopeer.android.librarys;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public abstract class SwitchFragmentAdapter {

    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private int mCurrentPosition;
    private ViewGroup mContainer;
    private boolean first = true;

    public SwitchFragmentAdapter(FragmentManager fm, ViewGroup container) {
        mFragmentManager = fm;
        mContainer = container;
        mCurrentPosition = 0;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    public void initData() {
        replaceContainer(0);
    }

    public void switchView(ViewGroup container) {
        if (mCurrentPosition == 0) {
            replaceContainer(1);
        } else {
            replaceContainer(0);
        }
    }

    private void replaceContainer(int postion) {
        FragmentTransaction curTransaction = mFragmentManager.beginTransaction();

        switch (postion) {
            case 1:
                curTransaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top,
                        R.anim.in_from_top, R.anim.out_to_bottom);
                break;
            case 0:
                curTransaction.setCustomAnimations(R.anim.in_from_top, R.anim.out_to_bottom,
                        R.anim.in_from_top, R.anim.out_to_bottom);
                break;
        }

        Fragment fragment = retrieveFromCache(postion);
        if (null == fragment) {
            try {
                fragment = getItem(postion);
                curTransaction.addToBackStack(null);
            } catch (Exception e) {
                return;
            }
        }
        curTransaction.replace(mContainer.getId(), fragment,
                makeFragmentName(mContainer.getId(), postion));
        curTransaction.commit();
        mCurrentPosition = postion;
    }

    private Fragment retrieveFromCache(int menuItem) {
        if (mFragmentManager.getFragments() == null) return null;
        for (Fragment backFragment : mFragmentManager.getFragments()) {
            String name = makeFragmentName(mContainer.getId(), menuItem);
            if (null != backFragment
                    && name.equals(backFragment.getTag())) {
                return backFragment;
            }
        }
        return null;
    }

    /**
     * Return a unique identifier for the item at the given position.
     * <p/>
     * <p>The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.</p>
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    public long getItemId(int position) {
        return position;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

}
