package zwaggerboyz.instaswaggify.filters;



/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    BrightnessFilter.java
 * This file contains the brightness-filter. It links to the required RenderScript-object and stores
 * the values of the slider.
 */

public class BrightnessFilter extends AbstractFilterClass {
    public BrightnessFilter() {
        mID = FilterID.BRIGHTNESS;
        mName = "Brightness";
        mNumValues = 1;

        /* slider label */
        mLabels = new String[] {
                "intensity"
        };

        /* slider default value */
        mValues = new int[] {
                33
        };
    }

    //@Override
    public void updateInternalValues() {
        //mScript.set_brightnessValue(normalizeValue(mValues[0], 0.5f, 2.f));
    }

}
