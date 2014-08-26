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

import android.opengl.GLES20;

import zwaggerboyz.instaswaggify.TexturedSquare;

public class SaturationFilter extends AbstractFilterClass {
    private int mSaturationValueHandle;

    public static String fragmentShaderCode =
            "precision mediump float;" +

            "const vec3 gMonoMult = vec3(0.299, 0.587, 0.114);" +

            "uniform float saturationValue;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "	vec4 texColor = texture2D(u_Texture, TexCoordinate);" +
            "	vec3 result = vec3(dot(texColor.xyz, gMonoMult));" +
            "	result = mix(result, texColor.xyz, saturationValue);" +
            "	gl_FragColor = vec4(result, 1.0);" +
            "}";

    public static int ProgramStatic;

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

        mProgram = ProgramStatic;
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mSaturationValueHandle = GLES20.glGetUniformLocation(mProgram, "saturationValue");
        GLES20.glUniform1f(mSaturationValueHandle, normalizeValue(mValues[0], 0.f, 2.f));
    }
/*    public void updateInternalValues() {
        mScript.set_saturationValue(normalizeValue(mValues[0], 0.f, 2.f));
    }*/
}
