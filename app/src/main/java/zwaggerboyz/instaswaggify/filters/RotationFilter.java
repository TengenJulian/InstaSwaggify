package zwaggerboyz.instaswaggify.filters;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    RotationFilter.java
 * This file contains the rotation-filter. It links to the required RenderScript-object and
 * stores the values of the slider.
 */

import android.opengl.GLES20;

public class RotationFilter extends AbstractFilterClass {

    public RotationFilter() {
        mID = FilterID.ROTATION;
        mName = "Rotation";
        mNumValues = 1;

        /* slider label */
        mLabels = new String[] {
                "angle"
        };

        /* slider default value */
        mValues = new float[] {
                0f
        };
    }

    @Override
    public void specifyExtraVariables() {
        setAngle(normalizeValue(mValues[0], 0.f, 360.f));
    }

    @Override
    public void draw(float[] mvpMatrix, int fboTexture) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        setTextureDataHandle(fboTexture);
        super.draw(mvpMatrix);
    }

/*    //@Override
    public void updateInternalValues() {
        mScript.set_rotationAngle(normalizeValue(mValues[0], 0.f, 360.f));
        mScript.set_imageWidth(imageWidth);
        mScript.set_imageHeight(imageHeight);
        mScript.invoke_calculateMatrix();
    }*/

}