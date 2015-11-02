package com.loopeer.android.librarys;

import android.graphics.PointF;

public class PullIndicator {
    public final static String TAG = "PullIndicator";

    public final static int POS_START = 0;
    private boolean mIsUnderTouch = false;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;
    private int mCurrentPosY = 0;
    private int mLastPos = 0;
    private int mHeaderHeight;
    private int mFooterHeight;
    private int mStartSwitchOffset = 200;
    private HeaderImpl mHeaderImpl;
    private FooterImpl mFooterImpl;
    private ShowText mShowText;

    public int getContentHeight() {
        return mContentHeight;
    }

    public void setContentHeight(int contentHeight) {
        this.mContentHeight = contentHeight;
    }

    private int mContentHeight;
    private int mPressedPos = 0;
    private float mResistance = 1.7f;

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.mHeaderHeight = headerHeight;
    }

    public int getFooterHeight() {
        return mFooterHeight;
    }

    public void setFooterHeight(int footerHeight) {
        this.mFooterHeight = footerHeight;
    }

    public int getCurrentPosY() {
        return mCurrentPosY;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPosY;
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / mResistance);
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPosY;
        mCurrentPosY = current;
    }

    public boolean hasMovedAfterPressedDown() {
        return mCurrentPosY != mPressedPos;
    }

    public boolean hasLeftStartPosition() {
        return mCurrentPosY != POS_START;
    }

    public boolean hasOverStartPosition() {
        return mCurrentPosY <= POS_START;
    }

    public boolean hasBelowStartPosition() {
        return mCurrentPosY >= POS_START;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public boolean isInStartPosition() {
        return mCurrentPosY == POS_START;
    }

    public boolean willOverTop(int to) {
        return to < POS_START;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPosY == to;
    }

    public int getStartSwitchOffset() {
        return mStartSwitchOffset;
    }

    public void setStartSwitchOffset(int startSwitchOffset) {
        this.mStartSwitchOffset = startSwitchOffset;
    }

    public void applyMoveStatus() {
        if (isInStartPosition()) return;
        if (getCurrentPosY() > getStartSwitchOffset()) {
            mHeaderImpl.onCanStartSwitch(getCurrentPosY(), mShowText.headerSwitchTiptext);
        } else if (getCurrentPosY() > 0){
            mHeaderImpl.onMoveStart(getCurrentPosY(), mShowText.headerStarttext);
        } else if (getCurrentPosY() < - getStartSwitchOffset()){
            mFooterImpl.onCanStartSwitch(getCurrentPosY(), mShowText.footerSwitchTiptext);
        } else if (getCurrentPosY() < 0){
            mFooterImpl.onMoveStart(getCurrentPosY(), mShowText.footerStarttext);
        }
    }

    public FooterImpl getFooterImpl() {
        return mFooterImpl;
    }

    public void setFooterImpl(FooterImpl footerImpl) {
        this.mFooterImpl = footerImpl;
    }

    public HeaderImpl getHeaderImpl() {
        return mHeaderImpl;
    }

    public void setHeaderImpl(HeaderImpl headerImpl) {
        this.mHeaderImpl = headerImpl;
    }

    public void setShowText(ShowText showText) {
        mShowText = showText;
    }

    public static class ShowText {
        String headerStarttext;
        String headerSwitchTiptext;
        String footerStarttext;
        String footerSwitchTiptext;

        public ShowText(String headerStarttext, String headerSwitchTiptext,
                        String footerStarttext, String footerSwitchTiptext) {
            this.headerStarttext = headerStarttext;
            this.headerSwitchTiptext = headerSwitchTiptext;
            this.footerStarttext = footerStarttext;
            this.footerSwitchTiptext = footerSwitchTiptext;
        }
    }
}
