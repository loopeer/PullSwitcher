package com.loopeer.android.librarys;

import android.view.ViewGroup;

public abstract class SwitcherAdapter {

    private SwitcherHolder switcherHolder;

    public abstract void replaceItem(ViewGroup container, int prePosition, int newPosition);

    public abstract int getCount();

    public void setOnPageChangeListener(SwitcherHolder holder) {
        this.switcherHolder = holder;
    }

    public SwitcherHolder getSwitcherHolder() {
        return switcherHolder;
    }

 }

