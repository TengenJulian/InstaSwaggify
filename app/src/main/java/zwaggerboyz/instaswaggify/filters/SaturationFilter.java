package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    SaturationFilter.java
 * This file contains the saturation-filter. It links to the required RenderScript-object and
 * stores the values of the slider.
 */

public class SaturationFilter extends AbstractFilterClass {

    public SaturationFilter() {
        mID = FilterID.SATURATION;
        mName = "Saturation";
        mNumValues = 1;


        mLabels = new String[] {
                "intensity"
        };
        mValues = new int[] {
            50
        };
    }

/*    public void updateInternalValues() {
        mScript.set_saturationValue(normalizeValue(mValues[0], 0.f, 2.f));
    }*/
}
