package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    GuassianBlurFilter.java
 * This file contains the gaussian-blur-filter. It links to the required RenderScript-object and
 * stores the values of the slider.
 */

public class GaussianBlurFilter extends AbstractFilterClass {

    public GaussianBlurFilter() {
        mID = FilterID.GAUSSIAN;
        mName = "Gaussian Blur";
        mNumValues = 1;

        /* slider label */
        mLabels = new String[] {
                "radius"
        };

        /* slider default value */
        mValues = new int[] {
                0
        };
    }

    //@Override
/*
    public void updateInternalValues() {
        mScript.setRadius(normalizeValue(mValues[0], 1.f, 20.f));
    }
*/

}