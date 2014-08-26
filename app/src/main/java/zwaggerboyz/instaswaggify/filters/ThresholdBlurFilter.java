package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    ThresholdBlurFilter.java
 * This file contains the threshold-filter. It links to the required RenderScript-object and
 * stores the values of the sliders.
 */

import android.opengl.GLES20;

public class ThresholdBlurFilter extends AbstractFilterClass {
    private static int ProgramStatic;
    public static final String fragmentShaderCode =
                    "precision mediump float;" +

                    "uniform float threshold;" +
                    "uniform float strength;" +
                    "uniform float blurSizeW;" +
                    "uniform float blurSizeH;" +
                    "uniform float drop;" +
                    "uniform int radius;" +

                    "uniform sampler2D u_Texture;" +
                    "varying vec2 TexCoordinate;" +

                    "void main() {" +
                    "	vec3 out_color = texture2D(u_Texture, TexCoordinate).rgb;" +
                    "	vec3 neighbour;" +
                    "	float dist;" +
                    "	vec3 diff;" +

                    "	for (int x_offset = -radius; x_offset <= radius; x_offset++) {" +
                    "       float new_x = TexCoordinate.x + x_offset * blurSizeW;" +
                    "       if (abs(new_x) > 1)" +
                    "           continue;" +
                    "		for (int y_offset = -radius; y_offset <= radius; y_offset++) {" +

                    "           vec2 new_pos = vec2(new_x, TexCoordinate.y + y_offset * blurSizeH);"+

                    "           if (abs(new_pos.y * new_pos.y) > 1) {" +
                    "               continue;" +
                    "           }" +

                    "			neighbour = texture2D(u_Texture, new_pos).rgb;" +
                    "			dist = max(abs(x_offset), abs(y_offset)) * drop;" +
                    "			diff = abs(out_color - neighbour);" +

                    "			if (dot(diff, diff) > threshold) {" +
                    "				continue;" +
                    "			}" +
                    "			out_color = (out_color * dist + neighbour * strength) / (dist + strength);" +
                    "		}" +
                    "	}" +

                    "	gl_FragColor = vec4(out_color, 1.0);" +
                    "}";


    private int mThresholdHandle, mDropHandle, mStrengthHandle, mRadiusHandle, mBlurSizeW, mBlurSizeH;

    public ThresholdBlurFilter() {
        mID = FilterID.THRESHOLD;
        mName = "Threshold Blur";
        mNumValues = 3;

        /* slider values */
        mLabels = new String[] {
                "radius",
                "threshold",
                "strength",
                "drop"
        };

        /* slider default values */
        mValues = new int[] {
            5,
            50,
            5,
            5,
        };

        mProgram = ProgramStatic;
    }


    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mRadiusHandle = GLES20.glGetUniformLocation(mProgram, "radius");
        mThresholdHandle = GLES20.glGetUniformLocation(mProgram, "threshold");
        mStrengthHandle = GLES20.glGetUniformLocation(mProgram, "strength");
        mDropHandle = GLES20.glGetUniformLocation(mProgram, "drop");
        mBlurSizeW = GLES20.glGetUniformLocation(mProgram, "blurSizeW");
        mBlurSizeH = GLES20.glGetUniformLocation(mProgram, "blurSizeH");

        GLES20.glUniform1i(mRadiusHandle, (int)normalizeValue(mValues[0], 1.f, 10));
        //GLES20.glUniform1i(mRadiusHandle, 1);
        GLES20.glUniform1f(mThresholdHandle, normalizeValue(mValues[1], 0, 500));
        GLES20.glUniform1f(mStrengthHandle, 10.f - normalizeValue(mValues[2], 0.1f, 9.9f));
        GLES20.glUniform1f(mDropHandle, 1.5f);

        GLES20.glUniform1f(mBlurSizeW, 1.f / imageWidth);
        GLES20.glUniform1f(mBlurSizeH, 1.f / imageHeight);
        GLES20.glUniform1f(mDropHandle, 1.5f);
    }

}