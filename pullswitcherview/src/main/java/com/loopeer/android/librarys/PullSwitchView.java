package com.loopeer.android.librarys;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class PullSwitchView extends ViewGroup implements NestedScrollingParent {

    private View mHeaderView;
    private View mFootView;
    private View mContent;
    private PullIndicator mPullIndicator;
    private ScrollChecker mScrollChecker;
    private SwitcherHolder mSwitcherHolder;
    private boolean mDisableWhenHorizontalMove = false;
    private boolean mPreventForHorizontal = false;
    private int mPagingTouchSlop;
    private MotionEvent mLastMoveEvent;
    private boolean mHasSendCancelEvent = false;
    private int mDurationToCloseHeader = getResources().getInteger(R.integer.pull_switch_millms);
    private View mScrollTarget;

    public PullSwitchView(Context context) {
        this(context, null);
    }

    public PullSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPullIndicator = new PullIndicator();
        initShowText(null);
        mScrollChecker = new ScrollChecker();

        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 3) {
            throw new IllegalStateException("PullSwitchView only can host 3 elements");
        } else {
            createViews();
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }

        super.onFinishInflate();
    }

    private void createViews() {
        final int childCount = getChildCount();
        int contentNumCount = 0;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof Header) {
                mHeaderView = view;
                continue;
            }
            if (view instanceof Footer) {
                mFootView = view;
                continue;
            }
            contentNumCount++;
            mContent = view;
        }

        if (mHeaderView == null) {
            createDefaultHeader();
        }
        if (mFootView == null) {
            createDefaultFooter();
        }

        if (contentNumCount == 0) {
            throw new IllegalStateException("PullSwitchView must have one content view");
        } else if (contentNumCount > 1) {
            throw new IllegalStateException("PullSwitchView can host only one direct child, " +
                    "and the footer and header must implements FooterImpl or HeaderImpl");
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        mScrollTarget = target;
        return false;
    }

    private void createDefaultFooter() {
        setFooterView(new DefaultFooter(getContext()));
    }

    private void createDefaultHeader() {
        setHeaderView(new DefaultHeader(getContext()));
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
                        tryToSwitch();
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }
            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mPullIndicator.onPressDown(e.getX(), e.getY());
                mScrollChecker.abortIfWorking();

                mPreventForHorizontal = false;

                dispatchTouchEventSupper(e);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mPullIndicator.onMove(e.getX(), e.getY());
                float offsetY = mPullIndicator.getOffsetY();
                float offsetX = mPullIndicator.getOffsetX();

                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mPullIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }

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
        return (moveUp && checkCanDoPullUp()) ||
                (moveDown && checkCanDoPullDown()) ||
                mPullIndicator.hasLeftStartPosition();
    }

    private boolean checkCanDoPullUp() {
        if (mScrollTarget != null) {
            return !ViewCompat.canScrollVertically(mScrollTarget, 1);
        }
        return true;
    }

    private boolean checkCanDoPullDown() {
        if (mScrollTarget != null) {
            return !ViewCompat.canScrollVertically(mScrollTarget, -1);
        }
        return true;
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    private void movePos(float deltaY) {

        int to = mPullIndicator.getCurrentPosY() + (int) deltaY;

        if (isReDirect(to)) {
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

        boolean isUnderTouch = mPullIndicator.isUnderTouch();

        if (isUnderTouch && !mHasSendCancelEvent && mPullIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        applySwitchShowText();

        if (isUnderTouch) {
            sendDownEvent();
        }

        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        mFootView.offsetTopAndBottom(change);
        invalidate();
    }

    private void applySwitchShowText() {
        mPullIndicator.applyMoveStatus();
    }

    private void tryToSwitch() {
        if (!mPullIndicator.isUnderTouch() && Math.abs(mPullIndicator.getCurrentPosY()) >= mPullIndicator.getStartSwitchOffset()) {
            if (mPullIndicator.getCurrentPosY() > 0) {
                mSwitcherHolder.onPrePage();
            } else {
                mSwitcherHolder.onNextPage();
            }
            checkFirstOrLastToCancelDelay();
        } else {
            tryScrollBackToTop();
        }
    }

    private void checkFirstOrLastToCancelDelay() {
        if (!mSwitcherHolder.isFirstPage() && !mSwitcherHolder.isLastPage()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryScrollBackToTop();
                }
            }, getResources().getInteger(R.integer.pull_switch_millms));
        } else {
            tryScrollBackToTop();
        }
    }

    private void tryScrollBackToTop() {
        if (!mPullIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(mPullIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    public void setSwitcherHolder(SwitcherHolder switcherHolder) {
        if (mSwitcherHolder == null) initShowText(switcherHolder);
        mSwitcherHolder = switcherHolder;
        mPullIndicator.setSwitchHolderImpl(mSwitcherHolder);
    }

    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    public void setHeaderView(View header) {
        if (!(header instanceof Header)) {
            throw new IllegalStateException("header must implements HeaderImpl");
        }
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        mPullIndicator.setHeaderImpl((Header) mHeaderView);
        addView(header);
    }

    public void setFooterView(View footer) {
        if (!(footer instanceof Footer)) {
            throw new IllegalStateException("footer must implements FooterImpl");
        }
        if (mFootView != null && footer != null && mFootView != footer) {
            removeView(mFootView);
        }
        ViewGroup.LayoutParams lp = footer.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            footer.setLayoutParams(lp);
        }
        mFootView = footer;
        mPullIndicator.setFooterImpl((Footer) mFootView);
        addView(footer);
    }

    public void setShowText(PullIndicator.ShowText showText) {
        mPullIndicator.setShowText(showText);
    }

    private void initShowText(SwitcherHolder switcherHolder) {
        setShowText(new PullIndicator.ShowText(
                getResources().getString(switcherHolder != null && switcherHolder.isFirstPage() ? R.string.pull_header_first : R.string.pull_header_start_show),
                getResources().getString(switcherHolder != null && switcherHolder.isFirstPage() ? R.string.pull_header_first : R.string.pull_header_can_switch_show),
                getResources().getString(switcherHolder != null && switcherHolder.isLastPage() ? R.string.pull_footer_last : R.string.pull_footer_start_show),
                getResources().getString(switcherHolder != null && switcherHolder.isLastPage() ? R.string.pull_footer_last : R.string.pull_footer_can_switch_show)
        ));
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
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
