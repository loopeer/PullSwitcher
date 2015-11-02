package com.loopeer.android.librarys;

import android.view.View;

public interface PullHandler {

    public boolean checkCanDoPullDown(final View content);

    public boolean checkCanDoPullUp(final View content);

    public void pullDownStartSwitch();

    public void pullUpStartSwitch();

}