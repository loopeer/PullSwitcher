package com.loopeer.android.librarys.pullswitcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.loopeer.android.librarys.PullDefaultHandler;
import com.loopeer.android.librarys.PullHandler;
import com.loopeer.android.librarys.PullSwitchView;


public class MainActivity extends AppCompatActivity implements PullHandler {

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PullSwitchView pullSwitchView = (PullSwitchView) findViewById(R.id.switcher);

        scrollView = (ScrollView) findViewById(R.id.scroll);

        pullSwitchView.setPullHandler(this);
    }

    @Override
    public boolean checkCanDoPullDown(PullSwitchView frame, View content, View header, View footer) {
        return PullDefaultHandler.checkContentCanBePulledDown(frame, scrollView, header);
    }

    @Override
    public boolean checkCanDoPullUp(PullSwitchView frame, View content, View header, View footer) {
        return PullDefaultHandler.checkContentCanBePulledUp(frame, scrollView, header);
    }
}
