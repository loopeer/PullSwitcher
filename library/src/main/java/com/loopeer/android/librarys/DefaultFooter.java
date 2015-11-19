package com.loopeer.android.librarys;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class DefaultFooter extends TextView implements Footer {
    private static final String TAG = "DefaultFooter";

    public DefaultFooter(Context context) {
        this(context, null);
    }

    public DefaultFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setGravity(Gravity.CENTER);
        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        setPadding(padding, padding, padding, padding);
        setTextColor(ContextCompat.getColor(context, R.color.switcher_color_hint));
    }

    @Override
    public void onMoveStart(int currentPosY, int startSwitchOffset, final CharSequence string) {
        setText(string.toString());
    }

    @Override
    public void onCanStartSwitch(int currentPosY, int startSwitchOffset, final CharSequence string) {
        setText(string.toString());
    }
}
