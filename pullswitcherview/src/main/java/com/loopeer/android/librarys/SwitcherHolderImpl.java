package com.loopeer.android.librarys;

import android.app.Activity;
import android.view.ViewGroup;


public class SwitcherHolderImpl implements SwitcherHolder {

    private ViewGroup mContainer;
    private SwitcherAdapter mAdapter;
    private int mPreCurrentItem;
    private int mCurItem;   // Index of currently displayed page.

    public SwitcherHolderImpl(ViewGroup mContainer) {
        this.mContainer = mContainer;
    }

    public void setAdapter(SwitcherAdapter adapter) {
        mAdapter = adapter;
        mPreCurrentItem = 0;
        adapter.setOnPageChangeListener(this);
        setCurrentItem(0);
    }

    public void setCurrentItem(int i) {
        mCurItem = i;
        if (mCurItem == mPreCurrentItem && mCurItem != 0) return;
        mAdapter.replaceItem(mContainer, mPreCurrentItem, mCurItem);
        mAdapter.setOnPageChangeListener(this);
        mPreCurrentItem = i;
    }

    public int getCurrentPosition() {
        return mCurItem;
    }

    public void nextPage() {
        if (mCurItem == mAdapter.getCount() - 1) return;
        mPreCurrentItem = mCurItem++;
        nextPage(mCurItem);
    }

    public void nextPage(int position) {
        setCurrentItem(position);
    }

    public void prePage() {
        if (mCurItem == 0) return;
        mPreCurrentItem = mCurItem --;
        prePage(mCurItem);
    }

    private void prePage(int position) {
        setCurrentItem(position);
    }

    @Override
    public void onNextPage() {
        nextPage();
    }

    @Override
    public void onPrePage() {
        prePage();
    }

    @Override
    public boolean isLastPage() {
        return mCurItem == mAdapter.getCount() - 1;
    }

    @Override
    public boolean isFirstPage() {
        return mCurItem == 0;
    }

    public void doBack() {
        if (mCurItem == 0) {
            ((Activity) mContainer.getContext()).finish();
        } else {
            onPrePage();
        }
    }
}
