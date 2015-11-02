package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.loopeer.android.librarys.SwitcherAdapter;
import com.loopeer.android.librarys.SwitcherHolder;


public class MainActivity extends AppCompatActivity {

    private FrameLayout containerLayout;
    private SwitcherAdapter adapter;
    private SwitcherHolder mSwitchHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        containerLayout = (FrameLayout) findViewById(R.id.container);

        mSwitchHolder = new SwitcherHolder(containerLayout);
        adapter = new TestSwitcherAdapter(getSupportFragmentManager(), mSwitchHolder);
        mSwitchHolder.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        mSwitchHolder.doBack();
    }
}
