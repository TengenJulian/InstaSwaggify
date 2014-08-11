package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    AbstractFilterClass.java
 * This file contains the abstract class used to implement the different filters. It contains a
 * number of variables with getter- and setter-functions.
 */

import android.util.Log;
import java.util.Arrays;


import zwaggerboyz.instaswaggify.TexturedSquare;

public abstract class AbstractFilterClass extends TexturedSquare {

    public enum FilterID {
        BRIGHTNESS,
        CONTRAST,
        GAUSSIAN,
        ROTATION,
        SATURATION,
        SEPIA,
        NOISE,
        INVERT,
        COLORIZE,
        THRESHOLD,
        IDENTITY
    }

    protected FilterID mID;
    protected String mName;
    protected int mValues[];
    protected String mLabels[];
    protected int mNumValues;
    protected int imageHeight;
    protected int imageWidth;

    public String getName() {
        return mName;
    }

    public FilterID getID() {
        return  mID;
    }

    public String getLabel(int i) {
        if (i < mNumValues) {
            return mLabels[i];
        } else {
            return "";
        }
    }

    public int getValue(int i) {
        if (i < mNumValues) {
            return mValues[i];
        } else {
            return 0;
        }
    }

    public void setValue(int i, int value) {
        if (i < mNumValues) {
            mValues[i] = value;
        }
    }

    /* set input value to the according value between the min and max value */
    public float normalizeValue(int value, float min, float max) {
        return (float) ((max - min) * (value / 100.0) + min);
    }

    public void setArray(int[] array) {
        if (array.length > 0) {
            System.arraycopy(array, 0, mValues, 0, mNumValues);
        } else {
            mValues = new int[mNumValues];
        }
    }

    public int[] getArray() {
        int output[] = new int[mNumValues];
        System.arraycopy(mValues, 0, output, 0, mNumValues);
        Log.i("filter values", Arrays.toString(output));
        return output;
    }

    public void draw(float[] mvpMatrix, int fboTexture) {
        setTextureDataHandle(fboTexture);
        super.draw(mvpMatrix);
    }

    @Override
    public void allocateAndCompile() {
    }

    @Override
    public AbstractFilterClass clone() {
        return null;
    }

    public void setTextureDataHandle(int texture) {
        super.setTextureDataHandle(texture);
    }

    public int getNumValues() {
        return mNumValues;
    }

    public TexturedSquare getTextureSquare() {
        return (TexturedSquare) this;
    }
}
