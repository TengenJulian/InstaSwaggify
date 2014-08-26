package zwaggerboyz.instaswaggify.filters;

/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    InverColorsFilter.java
 * This file contains the invert-colors-filter. It links to the required RenderScript-object.
 */

import android.util.Log;

import zwaggerboyz.instaswaggify.MyGLRenderer;

public class InvertColorsFilter extends AbstractFilterClass {
    public static final String vertexShaderCode =
            "attribute vec2 texCoordinate;" +
                    "attribute vec4 position;" +
                    "varying vec2 TexCoordinate;" +
                    "uniform mat4 uMVPMatrix;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * position;" +
                    "  TexCoordinate = texCoordinate;" +
                    "}";


    public static final String fragmentShaderCode =
            "precision mediump float;" +

            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +

            "  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0) - texture2D(u_Texture, TexCoordinate) + vec4(0, 0, 0, 1.0);" +

            "}";


    public InvertColorsFilter() {

        mID = FilterID.INVERT;
        mName = "Invert Colors";
        mNumValues = 0;
        mProgram = ProgramStatic;
        Log.i("mProgram", mProgram + "");
    }

    public static  int ProgramStatic;

    public static void compileProgram() {
        ProgramStatic = compileProgramHelper(vertexShaderCode, fragmentShaderCode);
    }

}