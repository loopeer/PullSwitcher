package com.loopeer.android.librarys.pullswitcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.loopeer.android.librarys.Footer;

public class TestCustomFooterView extends TextView implements Footer{

    public TestCustomFooterView(Context context) {
        this(context, null);
    }

    public TestCustomFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestCustomFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(20, 20, 20, 20);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onMoveStart(int currentPosY, int startSwitchOffset, CharSequence string) {
        setText("Test custom footer text onMoveStart");
    }

    @Override
    public void onCanStartSwitch(int currentPosY, int startSwitchOffset, CharSequence string) {
        setText("Test custom footer text onCanStartSwitch");

    }
}
