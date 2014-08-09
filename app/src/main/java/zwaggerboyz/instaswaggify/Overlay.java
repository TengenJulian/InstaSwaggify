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

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Overlay extends TexturedSquare{
    private final float width;
    private final float height;
    private Boolean boundingBoxEnabled = false;
    private BoundingBox bbox;
    private String mName;
    private boolean flipped;
    private onOverlayChangeListener mListener;
    private Bitmap mBitmap;

    public static void printSquare4(String name, float matrix[]) {
        for (int i = 0; i < 16; i += 4){
            Log.i(name, "" + matrix[i] + ", " + matrix[i + 1] + ", " + matrix[i + 2] + ", " + matrix[i+3]);
        }
    }

    public static void printSquare3(String name, float matrix[]) {
        for (int i = 0; i < 9; i += 3){
            Log.i(name, "" + matrix[i] + ", " + matrix[i + 1] + ", " + matrix[i + 2]);
        }
    }

    /**
     * Sets up the drawing object data for use in an OpenGL ES mContext.
     */
    public Overlay(Bitmap bitmap, String name) {
        super();

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        baseScaleY = baseScaleX = 0.45f;

        if (width > height) {
            scaleX = 1;
            scaleY = height / width;

        }
        else {
            scaleX = width / height;
            scaleY = 1;
        }
        mBitmap = bitmap;
        mName = name;
    }

    @Override
    public void allocateAndCompile() {
        mTextureDataHandle = GLHelper.loadGLTexture(mBitmap);
        mBitmap.recycle();
    }

    public void showBoundingBox(boolean bool) {
        boundingBoxEnabled = bool;

        if (bool && bbox != null) {
            bbox = new BoundingBox(width, height);
            bbox.setTextureDataHandle(mTextureDataHandle);
        }
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
       if (boundingBoxEnabled) {
           bbox.setScaleFactor(scaleX, scaleY);
           bbox.setCenter(centerX, centerY);
           bbox.rotate(angle);
           bbox.draw(mvpMatrix);
       }

       super.draw(mvpMatrix);
    }

    public void flip() {
        flipped ^= true;
    }

    public String getName() {
        return mName;
    }

    @Override
    public Overlay clone() {
        try {
            Overlay temp = (Overlay) super.clone();
            return temp;
        } catch (CloneNotSupportedException e) {
            //e.printStackTrace();
        }

        return null;
    }

    public void setOnOverlayChangeListener(onOverlayChangeListener listener) {
        mListener = listener;
    }

    public interface onOverlayChangeListener {
        public void updateBuffer();
    }

    @Override
    public void close() {
/*        Log.i("closing overlay", "");
        int temp[] = {mTextureDataHandle};
        GLES20.glDeleteTextures(1, temp, 0);
        super.close();*/
    }

}