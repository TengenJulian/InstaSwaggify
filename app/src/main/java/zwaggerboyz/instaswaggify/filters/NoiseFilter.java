package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    NoiseFilter.java
 * This file contains the noise-filter. It links to the required RenderScript-object and
 * stores the values of the slider.
 */

import android.opengl.GLES20;

import java.util.Random;

public class NoiseFilter extends AbstractFilterClass {
    public final static String fragmentShaderCode =
            "precision mediump float;\n" +

                    "float rand(vec2 co){\n" +
                    "    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
                    "}\n" +

                    "uniform vec3 seed;\n" +
                    "uniform float noiseValue;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    "varying vec2 TexCoordinate;\n" +

                    "void main() {\n" +
                    "    vec4 texColor = texture2D(u_Texture, TexCoordinate);\n" +
                    "    float randF = rand(TexCoordinate.xy + seed.xy);\n" +
                    "	vec3 result = noiseValue * 2.0 * (vec3(randF) - 0.5);\n" +

                    "    gl_FragColor = vec4(texColor.rgb + result, texColor.a);\n" +
                    "}\n";

    public static int ProgramStatic;
    private int mSeedHandle, mNoiseValueHandle;
    private Random random;


    public NoiseFilter() {
        mName = "Noise";
        mNumValues = 1;
        mID = FilterID.NOISE;

        /* slider label */
        mLabels = new String[]{
                "intensity"
        };

        /* slider default value */
        mValues = new float[]{
                0f
        };

        mProgram = ProgramStatic;
        random = new Random();
    }

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void specifyExtraVariables() {
        mNoiseValueHandle = GLES20.glGetUniformLocation(mProgram, "noiseValue");
        mSeedHandle = GLES20.glGetUniformLocation(mProgram, "seed");

        GLES20.glUniform1f(mNoiseValueHandle, normalizeValue(mValues[0], 0.f, 0.25f));
        GLES20.glUniform3f(mSeedHandle, random.nextFloat(), random.nextFloat(), random.nextFloat());

    }

}