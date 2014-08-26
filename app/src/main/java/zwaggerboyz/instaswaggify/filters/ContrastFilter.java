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

import android.opengl.GLES20;

public class ContrastFilter extends AbstractFilterClass {
    private static final String fragmentShaderCode =
            "precision mediump float;" +

            "const vec3 gHalf = vec3(.5, .5, .5);" +
            "uniform float contrastValue ;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "	vec4 texColor = texture2D(u_Texture, TexCoordinate);" +
            "	vec3 result = (texColor.xyz - gHalf) * contrastValue + gHalf;" +
            "	gl_FragColor = vec4(result, 1.0);" +
            "}";

    private static int ProgramStatic;
    private int mContrastValueHandle;

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

        mProgram = ProgramStatic;
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mContrastValueHandle = GLES20.glGetUniformLocation(mProgram, "contrastValue");

        GLES20.glUniform1f(mContrastValueHandle, normalizeValue(mValues[0], 0.75f, 3.f));
    }

}
