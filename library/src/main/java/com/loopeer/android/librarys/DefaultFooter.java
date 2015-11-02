package com.loopeer.android.librarys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class DefaultFooter extends TextView implements FooterImpl {
    private static final String TAG = "DefaultFooter";

    public DefaultFooter(Context context) {
        this(context, null);
    }

    public DefaultFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER);
        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        setPadding(padding, padding, padding, padding);
    }

    @Override
    public void onMoveStart(int currentPosY, final CharSequence string) {
        setText(string.toString());
    }

    @Override
    public void onCanStartSwitch(int currentPosY, final CharSequence string) {
        setText(string.toString());
    }
}
