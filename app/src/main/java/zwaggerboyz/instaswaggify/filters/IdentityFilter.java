package zwaggerboyz.instaswaggify.filters;


import zwaggerboyz.instaswaggify.MyGLRenderer;

/**
 * Created by zeta on 8/4/14.
 */
public class IdentityFilter extends AbstractFilterClass {

    public IdentityFilter() {
        super();

        baseScaleX = MyGLRenderer.getWidth() / MyGLRenderer.getHeight();
        baseScaleY = 1;

        mID = FilterID.IDENTITY;
        mName = "Identity Colors";
        mNumValues = 0;
    }

}
