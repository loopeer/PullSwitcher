package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.librarys.OnPageChangeListener;
import com.loopeer.android.librarys.PullDefaultHandler;
import com.loopeer.android.librarys.PullHandler;
import com.loopeer.android.librarys.PullSwitchView;

public class TestFragment3 extends Fragment implements PullHandler {

    private OnPageChangeListener onPageChangeListener;

    public static TestFragment3 newInstance(OnPageChangeListener onPageChange) {
        TestFragment3 testFragment = new TestFragment3();
        testFragment.onPageChangeListener = onPageChange;
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PullSwitchView pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setPullHandler(this);
    }

    @Override
    public boolean checkCanDoPullDown(PullSwitchView frame, View content, View header, View footer) {
        return PullDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public boolean checkCanDoPullUp(PullSwitchView frame, View content, View header, View footer) {
        return PullDefaultHandler.checkContentCanBePulledUp(frame, content, header);
    }

    @Override
    public void pullDownStartSwitch() {
        onPageChangeListener.onPrePage();
    }

    @Override
    public void pullUpStartSwitch() {
        onPageChangeListener.onNextPage();
    }
}
