/*
 Created by Nikola on 11/22/2015.
 */
package pl.selvin.android.popularmovies.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("WeakerAccess,unused")
public abstract class VerticalScrollingBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private int mTotalDyUnconsumed = 0;
    private int mTotalDy = 0;
    @ScrollDirection
    private int mOverScrollDirection = ScrollDirection.SCROLL_NONE;
    @ScrollDirection
    private int mScrollDirection = ScrollDirection.SCROLL_NONE;

    public VerticalScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollingBehavior() {
        super();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ScrollDirection.SCROLL_DIRECTION_UP, ScrollDirection.SCROLL_DIRECTION_DOWN, ScrollDirection.SCROLL_NONE})
    public @interface ScrollDirection {
        int SCROLL_DIRECTION_UP = 1;
        int SCROLL_DIRECTION_DOWN = -1;
        int SCROLL_NONE = 0;
    }


    /**
     * @return Overscroll direction: SCROLL_DIRECTION_UP, CROLL_DIRECTION_DOWN, SCROLL_NONE
     */
    @ScrollDirection
    public int getOverScrollDirection() {
        return mOverScrollDirection;
    }


    /**
     * @return Scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE
     */

    @ScrollDirection
    public int getScrollDirection() {
        return mScrollDirection;
    }


    /**
     * @param coordinatorLayout coordinatorLayout
     * @param child             child
     * @param direction         Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     * @param currentOverScroll Unconsumed value, negative or positive based on the direction;
     * @param totalOverScroll   Cumulative value for current direction
     */
    @SuppressWarnings("EmptyMethod")
    public abstract void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll);

    /**
     * @param scrollDirection Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */
    public abstract void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection);

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild,
                                       @NonNull View target, int nestedScrollAxes, int type) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyUnconsumed > 0 && mTotalDyUnconsumed < 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        mTotalDyUnconsumed += dyUnconsumed;
        onNestedVerticalOverScroll(coordinatorLayout, child, mOverScrollDirection, dyConsumed, mTotalDyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target,
                                  int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (dy > 0 && mTotalDy < 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dy < 0 && mTotalDy > 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        mTotalDy += dy;
        onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, mScrollDirection);
    }


    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        mScrollDirection = velocityY > 0 ? ScrollDirection.SCROLL_DIRECTION_UP : ScrollDirection.SCROLL_DIRECTION_DOWN;
        return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, mScrollDirection);
    }

    @SuppressWarnings("SameReturnValue")
    protected abstract boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection);
}