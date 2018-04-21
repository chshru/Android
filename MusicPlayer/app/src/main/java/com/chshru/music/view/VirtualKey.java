package com.chshru.music.view;

/**
 * Created by chshru on 2017/5/1.
 */

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by win7 on 2016/12/14.
 */

public class VirtualKey {

    public static void assistActivity(View viewObserving) {
        new VirtualKey(viewObserving);
    }

    private View mViewObserved;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;

    private VirtualKey(View viewObserving) {
        mViewObserved = viewObserving;
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                resetLayoutByUsableHeight(computeUsableHeight());
            }
        });
        frameLayoutParams = mViewObserved.getLayoutParams();
    }

    private void resetLayoutByUsableHeight(int usableHeightNow) {
        if (usableHeightNow != usableHeightPrevious) {
            frameLayoutParams.height = usableHeightNow;
            mViewObserved.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }
    
    private int computeUsableHeight() {
        Rect r = new Rect();
        mViewObserved.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}
