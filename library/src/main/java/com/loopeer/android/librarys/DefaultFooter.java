package com.loopeer.android.librarys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class DefaultFooter extends TextView implements FooterImpl {

    public DefaultFooter(Context context) {
        super(context);
    }

    public DefaultFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER);
        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        setPadding(padding, padding, padding, padding);
    }

    @Override
    public void onMoveStart(int currentPosY, CharSequence string) {
        this.setText(string);
    }

    @Override
    public void onCanStartSwitch(int currentPosY, CharSequence string) {
        this.setText(string);
    }
}
