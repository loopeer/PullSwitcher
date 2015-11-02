package com.loopeer.android.librarys;

import android.view.ViewGroup;

public abstract class SwitcherAdapter {

    private SwitcherHolderImpl switcherHolder;

    public abstract void replaceItem(ViewGroup container, int prePosition, int newPosition);

    public abstract int getCount();

    public void setOnPageChangeListener(SwitcherHolderImpl listener) {
        this.switcherHolder = listener;
    }

    public SwitcherHolderImpl getSwitcherHolder() {
        return switcherHolder;
    }

 }

