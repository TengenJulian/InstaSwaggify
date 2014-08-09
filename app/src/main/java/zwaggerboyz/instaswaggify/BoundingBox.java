/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zwaggerboyz.instaswaggify;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class BoundingBox extends TexturedSquare {
    protected String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "  gl_FragColor = vec4(0.75, 0.5, 0.5, 0.5);" +
            "}";

    public BoundingBox(float width, float height) {
        super();
        super.fragmentShaderCode = fragmentShaderCode;

        if (width > height) {
            scaleX = 1;
            scaleY = height / width;

        }
        else {
            scaleX = width / height;
            scaleY = 1;
        }

        allocateBuffers();
        compileProgram();
    }
}