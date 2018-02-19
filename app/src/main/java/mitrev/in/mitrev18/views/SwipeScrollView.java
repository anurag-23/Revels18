package mitrev.in.mitrev18.views;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by skvrahul on 11/12/17.
 */

public class SwipeScrollView extends NestedScrollView {
    private String TAG = "SwipeScrollView";
    GestureDetector gestureDetector;
    public void setGestureDetector(GestureDetector gestureDetector){
        this.gestureDetector = gestureDetector;
    }
    public SwipeScrollView(Context context){
        super(context);
    }
    public SwipeScrollView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev))
            return true;
        else
            return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: ad");
        if (gestureDetector.onTouchEvent(ev))
            return true;
        else
            return super.onInterceptTouchEvent(ev);
    }
}
