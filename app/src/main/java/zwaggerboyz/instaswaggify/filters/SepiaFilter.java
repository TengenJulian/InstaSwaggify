package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    SepiaFilter.java
 * This file contains the sepia-filter. It links to the required RenderScript-object and
 * stores the values of the sliders.
 */

import android.opengl.GLES20;
import android.util.Log;

import zwaggerboyz.instaswaggify.GLHelper;

public class SepiaFilter extends AbstractFilterClass {
    public final static String fragmentShaderCode =
            "precision mediump float;" +

            "const vec3 gMonoMult = vec3(0.299, 0.587, 0.114);" +

            "uniform float depth;" +
            "uniform float intensity;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "	vec4 texColor = texture2D(u_Texture, TexCoordinate);" +
            "	vec3 luma = vec3(dot(texColor.xyz, gMonoMult));" +
            "	gl_FragColor = vec4(luma + vec3(depth * 2, depth, -intensity), 1.0);" +
            "}";

    private static int ProgramStatic;
    private int mIntensityHandle, mDepthHandle;

    public SepiaFilter() {
        mID = FilterID.SEPIA;
        mName = "Sepia";
        mNumValues = 2;

        /* slider labels */
        mLabels = new String[] {
                "intensity",
                "depth"
        };

        /* slider default values */
        mValues = new float[] {
                10f,
                20f
        };

        mProgram = ProgramStatic;
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mIntensityHandle = GLES20.glGetUniformLocation(mProgram, "intensity");
        mDepthHandle = GLES20.glGetUniformLocation(mProgram, "depth");

        GLES20.glUniform1f(mDepthHandle, normalizeValue(mValues[1], 0.0f, 0.5f));
        GLES20.glUniform1f(mIntensityHandle, normalizeValue(mValues[0], 0.05f, 0.4f));
    }
}