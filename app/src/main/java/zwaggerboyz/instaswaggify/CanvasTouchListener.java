package zwaggerboyz.instaswaggify;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Peter on 19-6-2014.
 */

public class CanvasTouchListener implements View.OnTouchListener {
    private CanvasView mCanvasView;

    public CanvasTouchListener(CanvasView creator) {
        mCanvasView = creator;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("Touch", "Touchevent");
        mCanvasView.mScaleDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mCanvasView.onTouchDown((int)event.getX(), (int)event.getY());
                return true;

            case MotionEvent.ACTION_MOVE: {
                if (mCanvasView.mScaleDetector.isInProgress()) {
                    mCanvasView.onTouchMove(
                            (int) mCanvasView.mScaleDetector.getFocusX(),
                            (int) mCanvasView.mScaleDetector.getFocusY()
                    );
                } else {
                    if (mCanvasView.mScaleDetector.isInProgress()) {
                        mCanvasView.onTouchMove((int) event.getX(),
                                (int) event.getY());
                    }
                    return true;

                }
            }

            case MotionEvent.ACTION_UP:
                mCanvasView.onTouchUp((int)event.getX(), (int)event.getY());
                return true;

            default:
                return false;
        }
    }
}
