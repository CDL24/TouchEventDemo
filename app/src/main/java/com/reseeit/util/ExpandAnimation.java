package com.reseeit.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * This animation class is animating the expanding and reducing the size of a view. The animation toggles between the Expand and Reduce, depending on the current state of the view
 *
 * @author Vinay Rathod
 */
public class ExpandAnimation extends Animation {
    private View mAnimatedView;
    private LayoutParams mViewLayoutParams;
    private int mMarginStart, mMarginEnd;
    private boolean mIsVisibleAfter = false;
    private boolean mWasEndedAlready = false;
    private OnScrollListener listener;
    private View v;

    public interface OnScrollListener {
        public void onCollapsed(View animateView);

        public void onExpanded(View animateView);
    }

    public ExpandAnimation(final View view, int duration) {

        setDuration(duration);
        mAnimatedView = view;
        mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        // decide to show or hide the view
        mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

        mMarginStart = mViewLayoutParams.bottomMargin;
        mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);
        view.setVisibility(View.VISIBLE);
    }

    public ExpandAnimation(View view, int duration, View animateView, OnScrollListener onScrollListener) {
        this.listener = onScrollListener;
        this.v = animateView;
        setDuration(duration);
        mAnimatedView = view;
        mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        // decide to show or hide the view
        mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

        mMarginStart = mViewLayoutParams.bottomMargin;
        mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);

    }

    public boolean getIsVisible() {
        return mIsVisibleAfter;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if (interpolatedTime < 1.0f) {
            // Calculating the new bottom margin, and setting it
            mViewLayoutParams.bottomMargin = mMarginStart + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout();

            // Making sure we didn't run the ending before (it happens!)
        } else if (!mWasEndedAlready) {
            mViewLayoutParams.bottomMargin = mMarginEnd;
            mAnimatedView.requestLayout();

            if (mIsVisibleAfter) {
                mAnimatedView.setVisibility(View.GONE);
                if (listener != null && v != null)
                    listener.onCollapsed(v);
            } else {
                if (listener != null && v != null)
                    listener.onExpanded(v);
            }

            mWasEndedAlready = true;
        }
    }
}
