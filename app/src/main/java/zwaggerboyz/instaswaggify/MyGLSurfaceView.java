/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zwaggerboyz.instaswaggify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.List;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView implements RotationGestureDetector.OnRotationGestureListener{
    private RotationGestureDetector mRotationGesture;
    private ScaleGestureDetector mScaleDetector;
    private HistoryBuffer historyBuffer;

    private MyGLRenderer mRenderer;
    private Context mContext;
    private Overlay mSelected;
    private int mSelectedIndex;

    private float oldX;
    private float oldY;

    public MyGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    void init(Context context) {
        mContext = context;
        // Create an OpenGL ES 2.0 mContext.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();

        //mRenderer.setActivity(mContext);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mRotationGesture = new RotationGestureDetector(this);
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.instacap);

    }

    private float calcGLX(float x) {
        float halfWidth = getWidth() / 2.f;
        return mRenderer.getRatio() * (x / halfWidth - 1.f);
    }

    private float calcGLY(float y) {
        float halfHeight = getHeight() / 2.f;
        return ((getHeight() - y) / halfHeight - 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mRotationGesture.onTouchEvent(event);

        float newX = calcGLX(event.getX());
        float newY = calcGLY(event.getY());
        //Log.i("MotionEvent", "glx: " + glX +" gly: "+ glY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                oldX = newX;
                oldY = newY;

                mSelectedIndex = mRenderer.getSelectionIndex(-newX, newY);
                mSelected = mRenderer.getSelection(-newX, newY);
                if (mSelected != null) {
                    Log.i("Action Down", "selection found");
                    historyBuffer.recordValueChange(new DataContainer(mSelected.getValues()), mSelectedIndex);
                }

                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (event.getPointerCount() == 1) {

                    float deltaX = newX - oldX;
                    float deltaY = newY - oldY;

                    oldX = newX;
                    oldY = newY;

                    /* The bitmap is moved based on the coordinates of the firstfinger */
                    if (mSelected != null) {
                        mSelected.moveCenter(deltaX, deltaY);
                        requestRender();
                    }

                }
                return true;
            }

            case MotionEvent.ACTION_UP: {
                oldX = oldY = 0;
                mSelected = null;
                return true;
            }

            default:
                Log.i("MotionEvent", "" + event.getAction());
                return false;
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if(mSelected == null) {
                return true;
            }

            /* Don't let the object get too small or too large. */
            mSelected.setScaleFactor(detector.getScaleFactor());
            return true;
        }
    }

    @Override
    public boolean OnRotation(RotationGestureDetector rotationDetector) {
        if (mSelected == null)
            return true;

        mSelected.rotate(rotationDetector.getAngle());
        return false;
    }

    public void setOverlays(List<Overlay> overlays) {
        mRenderer.setOverlays(overlays);
    }

    public void setHistoryBuffer(HistoryBuffer historyBuffer) {
        this.historyBuffer = historyBuffer;
    }

    public void addToCompileQueue(Overlay overlay) {
        mRenderer.addToCompileQueue(overlay);
    }

    public MyGLRenderer getRenderer() {
        return mRenderer;
    }

}
