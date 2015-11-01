package com.loopeer.android.librarys;

import android.view.View;

public interface PullHandler {

    public boolean checkCanDoPullDown(final PullSwitchView frame, final View content, final View header, final View footer);

    public boolean checkCanDoPullUp(final PullSwitchView frame, final View content, final View header, final View footer);

    public void pullDownStartSwitch();

    public void pullUpStartSwitch();

}