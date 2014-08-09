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
        mValues = new int[] {
                0
        };
    }

/*    //@Override
    public void updateInternalValues() {
        mScript.set_rotationAngle(normalizeValue(mValues[0], 0.f, 360.f));
        mScript.set_imageWidth(imageWidth);
        mScript.set_imageHeight(imageHeight);
        mScript.invoke_calculateMatrix();
    }*/

}