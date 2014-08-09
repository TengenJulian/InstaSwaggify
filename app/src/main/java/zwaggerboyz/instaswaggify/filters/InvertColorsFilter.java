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

import zwaggerboyz.instaswaggify.MyGLRenderer;

public class InvertColorsFilter extends AbstractFilterClass {
    private static final String vertexShaderCode =

            "attribute vec2 texCoordinate;" +
                    "attribute vec4 position;" +
                    "varying vec2 TexCoordinate;" +
                    "uniform mat4 uMVPMatrix;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * position;" +
                    "  TexCoordinate = texCoordinate;" +
                    "}";


    private static final String fragmentShaderCode =
            "precision mediump float;" +

            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            //"  gl_FragColor = vec4(0.5, 0.75, 0.5, 1.0);" +

            "  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0) - texture2D(u_Texture, TexCoordinate) + vec4(0, 0, 0, 1.0);" +
            //"  gl_FragColor = vec4(0.5, 0.5, 0.75, 0.5);" +

            "}";

    public InvertColorsFilter() {
        super();
        super.vertexShaderCode = vertexShaderCode;
        super.fragmentShaderCode = fragmentShaderCode;

        baseScaleX = MyGLRenderer.getWidth() / MyGLRenderer.getHeight();
        baseScaleY = 1;

        mID = FilterID.INVERT;
        mName = "Invert Colors";
        mNumValues = 0;
    }

    //@Override
    public void updateInternalValues() { }

}