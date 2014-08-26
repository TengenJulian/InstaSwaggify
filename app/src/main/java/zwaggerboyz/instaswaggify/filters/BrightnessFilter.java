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

import android.opengl.GLES20;

public class BrightnessFilter extends AbstractFilterClass {
    public final static String fragmentShaderCode =
            "precision mediump float;" +

            "uniform float brightness = 0;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "	vec4 texColor = texture2D(u_Texture, TexCoordinate);" +
            "	gl_FragColor = texColor * brightness + vec4(0, 0, 0, 1.0);" +
            "}";

    private static int ProgramStatic;
    private int mBrightnessHandle;

    public BrightnessFilter() {
        mID = FilterID.BRIGHTNESS;
        mName = "Brightness";
        mNumValues = 1;

        /* slider label */
        mLabels = new String[] {
                "intensity"
        };

        /* slider default value */
        mValues = new float[] {
                33f
        };

        mProgram = ProgramStatic;
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mBrightnessHandle = GLES20.glGetUniformLocation(mProgram, "brightness");

        GLES20.glUniform1f(mBrightnessHandle, normalizeValue(mValues[0], 0.5f, 2.f));
    }

}
