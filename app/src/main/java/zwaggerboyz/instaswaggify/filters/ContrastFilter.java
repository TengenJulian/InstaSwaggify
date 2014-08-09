package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    ContrastFilter.java
 * This file contains the contrast-filter. It links to the required RenderScript-object and stores
 * the values of the slider.
 */

public class ContrastFilter extends AbstractFilterClass {

    public ContrastFilter() {
        mID = FilterID.CONTRAST;
        mName = "Contrast";
        mNumValues = 1;

        /* slider label */
        mLabels = new String[] {
                "intensity"
        };

        /* slider default value */
        mValues = new int[] {
                11
        };
    }

/*    //@Override
    public void updateInternalValues() {
        mScript.set_contrastValue(normalizeValue(mValues[0], 0.75f, 3.f));
    }*/

}
