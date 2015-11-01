package com.loopeer.android.librarys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

public class PullSwitchView extends ViewGroup {
    public static final String TAG = "PullSwitchView";

    private View mHeaderView;
    private View mFootView;
    private View mContent;
    private PullIndicator mPullIndicator;
    private ScrollChecker mScrollChecker;
    private PullHandler mPullHandler;

    private int mDurationToCloseHeader = 1000;

    public PullSwitchView(Context context) {
        this(context, null);
    }

    public PullSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPullIndicator = new PullIndicator();
        mScrollChecker = new ScrollChecker();
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 3) {
            throw new IllegalStateException("PtrFrameLayout only can host 3 elements");
        } else if (childCount == 3) {
            if (mContent == null || mHeaderView == null) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                View child3 = getChildAt(2);
                if (mContent == null && mHeaderView == null) {
                    mHeaderView = child1;
                    mContent = child2;
                    mFootView = child3;
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            int headerHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPullIndicator.setHeaderHeight(headerHeight);
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
        }

        if (mFootView != null) {
            measureChildWithMargins(mFootView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mFootView.getLayoutParams();
            int footerHeight = mFootView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mPullIndicator.setFooterHeight(footerHeight);
        }
    }

    private void measureContentView(View content, int widthMeasureSpec, int heightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) content.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);
        content.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        int contentHeight = mContent.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        mPullIndicator.setContentHeight(contentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetY = mPullIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY - mPullIndicator.getHeaderHeight();
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
        }

        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }

        if (mFootView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mFootView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = mContent.getBottom();
            final int right = left + mFootView.getMeasuredWidth();
            final int bottom = top + mFootView.getMeasuredHeight();
            mFootView.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPullIndicator.onRelease();
                if (mPullIndicator.hasLeftStartPosition()) {
                    if (mPullIndicator.hasMovedAfterPressedDown()) {
                        tryScrollBackToTop();
                        tryToSwitch();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }
            case MotionEvent.ACTION_DOWN:
                mPullIndicator.onPressDown(e.getX(), e.getY());
                dispatchTouchEventSupper(e);
                return true;
            case MotionEvent.ACTION_MOVE:
                mPullIndicator.onMove(e.getX(), e.getY());
                float offsetY = mPullIndicator.getOffsetY();

                if (canMovePos(offsetY)) {
                    movePos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(e);
    }

    private boolean canMovePos(float offsetY) {
        boolean moveDown = offsetY > 0;
        boolean moveUp = !moveDown;
        return (moveUp && mPullHandler.checkCanDoPullUp(this, mContent, mHeaderView, mFootView)) ||
                        (moveDown && mPullHandler.checkCanDoPullDown(this, mContent, mHeaderView, mFootView)) ||
                        mPullIndicator.hasLeftStartPosition();
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }
    
    private void movePos(float deltaY) {

        int to = mPullIndicator.getCurrentPosY() + (int) deltaY;

        if (isReDirect(to)){
            to = 0;
        }

        mPullIndicator.setCurrentPos(to);
        int change = to - mPullIndicator.getLastPosY();
        updatePos(change);
    }

    private boolean isReDirect(int to) {
        return Math.abs(to + mPullIndicator.getCurrentPosY()) < Math.abs(to) + Math.abs(mPullIndicator.getCurrentPosY());
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        applySwitchShowText();

        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        mFootView.offsetTopAndBottom(change);
        invalidate();
    }

    private void applySwitchShowText() {
        if (mPullIndicator.isInStartPosition()) return;
        if (mPullIndicator.getCurrentPosY() > mPullIndicator.getStartSwitchOffset()) {
            ((TextView) mHeaderView).setText("松开加载");
        } else if (mPullIndicator.getCurrentPosY() > 0){
            ((TextView) mHeaderView).setText("下拉加载上一页");
        } else if (mPullIndicator.getCurrentPosY() < - mPullIndicator.getStartSwitchOffset()){
            ((TextView) mFootView).setText("松开加载");
        } else if (mPullIndicator.getCurrentPosY() < 0){
            ((TextView) mFootView).setText("上拉加载下一页");
        }
    }

    private void tryToSwitch() {
        if (!mPullIndicator.isUnderTouch() && Math.abs(mPullIndicator.getCurrentPosY()) >= mPullIndicator.getStartSwitchOffset()) {
            if (mPullIndicator.getCurrentPosY() > 0) {
                mPullHandler.pullDownStartSwitch();
            } else {
                mPullHandler.pullUpStartSwitch();
            }
        }
    }

    private void tryScrollBackToTop() {
        if (!mPullIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(mPullIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    public void setPullHandler(PullHandler ptrHandler) {
        mPullHandler = ptrHandler;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;

            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
            //onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }


                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPullIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPullIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }

}
