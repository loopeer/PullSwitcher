package com.loopeer.android.librarys;

public interface HeaderFooter {

    void onMoveStart(int currentPosY, int startSwitchOffset, CharSequence string);

    void onCanStartSwitch(int currentPosY, int startSwitchOffset, CharSequence string);

}
