package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    ColorizeFilter.java
 * This file contains the colorize-filter. It links to the required RenderScript-object and stores
 * the values of the sliders.
 */

import android.opengl.GLES20;

public class ColorizeFilter extends AbstractFilterClass {
    public static final String fragmentShaderCode =
            "precision mediump float;" +

            "uniform vec3 color;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "	gl_FragColor = texture2D(u_Texture, TexCoordinate) + vec4(color, 0.0);" +
            "}";
    private static int ProgramStatic;
    private int mColorHandle;

    public ColorizeFilter() {
        mID = FilterID.COLORIZE;
        mName = "Colorize";
        mNumValues = 3;

        /* slider labels */
        mLabels = new String[] {
                "red",
                "green",
                "blue"
        };

        /* slider default values */
        mValues = new float[] {
                0f,
                0f,
                0f
        };

        mProgram = ProgramStatic;
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "color");

        GLES20.glUniform3f(mColorHandle,
                mValues[0] / 100f,
                mValues[1] / 100f,
                mValues[2] / 100f
        );
    }

}