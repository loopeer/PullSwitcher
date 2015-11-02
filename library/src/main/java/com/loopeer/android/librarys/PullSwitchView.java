package com.loopeer.android.librarys;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class PullSwitchView extends ViewGroup {
    public static final String TAG = "PullSwitchView";

    private View mHeaderView;
    private View mFootView;
    private View mContent;
    private PullIndicator mPullIndicator;
    private ScrollChecker mScrollChecker;
    private PullHandler mPullHandler;
    private SwitcherHolderImpl mSwitcherHolder;

    private int mDurationToCloseHeader = 500;

    public PullSwitchView(Context context) {
        this(context, null);
    }

    public PullSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPullIndicator = new PullIndicator();
        initShowText();
        mScrollChecker = new ScrollChecker();
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
            if (view instanceof HeaderImpl) {
                mHeaderView = view;
                continue;
            }
            if (view instanceof FooterImpl) {
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
        if (mPullHandler == null) return false;
        boolean moveDown = offsetY > 0;
        boolean moveUp = !moveDown;
        return (moveUp && mPullHandler.checkCanDoPullUp(mContent)) ||
                        (moveDown && mPullHandler.checkCanDoPullDown(mContent)) ||
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
        mPullIndicator.applyMoveStatus();
    }

    private void tryToSwitch() {
        if (!mPullIndicator.isUnderTouch() && Math.abs(mPullIndicator.getCurrentPosY()) >= mPullIndicator.getStartSwitchOffset()) {
            if (mPullIndicator.getCurrentPosY() > 0) {
                mPullHandler.pullDownStartSwitch();
            } else {
                mPullHandler.pullUpStartSwitch();
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

    public void setSwitcherHolder(SwitcherHolderImpl switcherHolder) {
        mSwitcherHolder = switcherHolder;
        mPullIndicator.setSwitchHolderImpl(mSwitcherHolder);
    }

    public void setPullHandler(PullHandler ptrHandler) {
        mPullHandler = ptrHandler;
    }

    public void setHeaderView(View header) {
        if (!(header instanceof HeaderImpl)) {
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
        mPullIndicator.setHeaderImpl((HeaderImpl) mHeaderView);
        addView(header);
    }

    public void setFooterView(View footer) {
        if (!(footer instanceof FooterImpl)) {
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
        mPullIndicator.setFooterImpl((FooterImpl) mFootView);
        addView(footer);
    }

    public void setShowText(PullIndicator.ShowText showText) {
        mPullIndicator.setShowText(showText);
    }

    private void initShowText() {
        setShowText(new PullIndicator.ShowText(
                getResources().getString(R.string.pull_header_start_show),
                getResources().getString(R.string.pull_header_can_switch_show),
                getResources().getString(R.string.pull_footer_start_show),
                getResources().getString(R.string.pull_footer_can_switch_show)
                ));
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
