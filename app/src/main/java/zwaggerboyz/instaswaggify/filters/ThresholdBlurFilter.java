package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    ThresholdBlurFilter.java
 * This file contains the threshold-filter. It links to the required RenderScript-object and
 * stores the values of the sliders.
 */

public class ThresholdBlurFilter extends AbstractFilterClass {

    public ThresholdBlurFilter() {
        mID = FilterID.THRESHOLD;
        mName = "Threshold Blur";
        mNumValues = 3;

        /* slider values */
        mLabels = new String[] {
                "radius",
                "threshold",
                "strength",
                "drop"
        };

        /* slider default values */
        mValues = new int[] {
            5,
            50,
            5,
            5,
        };
    }

    //@Override
/*    public void updateInternalValues() {
        mScript.set_imageHeight(imageHeight);
        mScript.set_imageWidth(imageWidth);
        mScript.set_radius((int) normalizeValue(mValues[0], 1.f, 30));
        mScript.set_threshold((int)normalizeValue(mValues[1], 0, 500));
        mScript.set_strength(5f - normalizeValue(mValues[2], 0, 4.9f));
    }*/

}