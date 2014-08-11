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
    private float width;
    private float height;
    private Boolean boundingBoxEnabled = false;
    private BoundingBox bbox;
    private int mTextureId;
    private String mName;
    private boolean flipped;

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
    public Overlay(int textureId, String name) {
        super();
        mTextureId = textureId;
        mName = name;
    }

    public void calcBaseScale(float width, float height) {
        this.width = width;
        this.height = height;
        if (width > height) {
            baseScaleX = 0.45f;
            baseScaleY = 0.45f * height / width;

        }
        else {
            baseScaleX = 0.45f * width / height;
            baseScaleY = 0.45f;
        }

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

    public void setValues(float values[]) {
        this.angle = values[0];
        this.scaleX = values[1];
        this.scaleY = values[2];
        this.centerX = values[3];
        this.centerY = values[4];
    }

    public float[] getValues() {
        float values[] = new float[5];
        values[0] = this.angle;
        values[1] = this.scaleX;
        values[2] = this.scaleY;
        values[3] = this.centerX;
        values[4] = this.centerY;
        return values;
    }

    public int getTextureId() {
        return mTextureId;
    }
}