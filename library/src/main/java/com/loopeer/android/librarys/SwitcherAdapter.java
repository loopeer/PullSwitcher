package com.loopeer.android.librarys;

import android.view.ViewGroup;

public abstract class SwitcherAdapter {

    protected OnPageChangeListener onPageChangeListener;

    public abstract void replaceItem(ViewGroup container, int prePosition, int newPosition);

    public abstract int getCount();

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.onPageChangeListener = listener;
    }

 }

