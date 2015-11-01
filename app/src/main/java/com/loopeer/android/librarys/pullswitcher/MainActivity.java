package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {

    private FrameLayout containerLayout;
    private DefaultSwitchFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        containerLayout = (FrameLayout) findViewById(R.id.container);
        adapter = new DefaultSwitchFragmentAdapter(getSupportFragmentManager(), containerLayout);
    }

    public void switchView() {
        adapter.switchView(containerLayout);
    }

}
