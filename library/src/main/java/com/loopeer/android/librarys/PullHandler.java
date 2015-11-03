package com.loopeer.android.librarys;

import android.view.View;

public interface PullHandler {

    boolean checkCanDoPullDown(final View content);

    boolean checkCanDoPullUp(final View content);

}