package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.librarys.PullSwitchView;
import com.loopeer.android.librarys.SwitcherHolder;

public class TestFragment2Scroll extends Fragment {

    private SwitcherHolder switcherHolder;

    public static TestFragment2Scroll newInstance(SwitcherHolder switcherHolder) {
        TestFragment2Scroll testFragment = new TestFragment2Scroll();
        testFragment.switcherHolder = switcherHolder;
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PullSwitchView pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setSwitcherHolder(switcherHolder);
    }
}
