package com.loopeer.android.librarys;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

public abstract class PullDefaultHandler implements PullHandler {

    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

    public static boolean canChildScrollDown(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                //ToDo not finish
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, 1);
        }
    }

    public static boolean checkContentCanBePulledDown(View content) {
        return !canChildScrollUp(content);
    }

    public static boolean checkContentCanBePulledUp(View content) {
        return !canChildScrollDown(content);
    }

    @Override
    public boolean checkCanDoPullDown(View content) {
        return checkContentCanBePulledDown(content);
    }

    @Override
    public boolean checkCanDoPullUp(View content) {
        return checkContentCanBePulledUp(content);
    }
}
