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

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class TexturedSquare {

    protected void printCoords() {
        printSquare3("coords", squareCoords);
    }

    public static void printSquare4(String name, float matrix[]) {
        for (int i = 0; i < 16; i += 4){
            Log.i(name, "" + matrix[i] + ", " + matrix[i + 1] + ", " + matrix[i + 2] + ", " + matrix[i+3]);
        }
    }

    public static void printSquare3(String name, float matrix[]) {
        for (int i = 0; i < 9; i += 3){
            Log.i(name, "" + matrix[i] + ", " + matrix[i + 1] + ", " + matrix[i + 2]);
        }
    }

    protected String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "attribute vec2 texCoordinate;" +
            "varying vec2 TexCoordinate;" +
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 position;" +

            "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  TexCoordinate = texCoordinate;" +
            "  gl_Position = uMVPMatrix * position;" +
            "}";

    protected String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D u_Texture;" +
            "varying vec2 TexCoordinate;" +

            "void main() {" +
            "  gl_FragColor = texture2D(u_Texture, TexCoordinate);" +
            "}";

    protected FloatBuffer textureBuffer;	// buffer holding the texture coordinates
    protected FloatBuffer vertexBuffer;
    protected ShortBuffer drawListBuffer;
    protected int mProgram;

    private int mPositionHandle;
    private int mMVPMatrixHandle;

    protected String positionAttrName = "position";
    protected String textureAttrName = "texCoordinate";
    protected String textureUniformName = "u_texture";

    private float[] transformationMatrix = new float[16];
    private float[] scratch = new float[16];
    private float[] boundingBox = new float[16];
    private int[] viewport = new int[4];

    protected float baseScaleX = 1;
    protected float baseScaleY = 1;
    protected float scaleX  = 1;
    protected float scaleY  = 1;
    protected float centerX = 0;
    protected float centerY = 0;
    protected float angle   = 0;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static float squareCoords[] = {
            -1f,  1f, 0.0f,   // top left
            -1f, -1f, 0.0f,   // bottom left
             1f, -1f, 0.0f,   // bottom right
             1f,  1f, 0.0f }; // top right

    static float squareCoords2[] = {
            -1f,  1f, 0.0f, 1f,   // top left
            -1f, -1f, 0.0f, 1f,   // bottom left
             1f, -1f, 0.0f, 1f, // bottom right
             1f,  1f, 0.0f, 1f }; // top right


    static float textureCoords[] = {
            // Mapping coordinates for the vertices
            1.0f, 0.0f,		// top right	(V4)
            1.0f, 1.0f,		// bottom right	(V3)
            0.0f, 1.0f,		// top left		(V2)
            0.0f, 0.0f,		// bottom left	(V1)
    };

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle to our texture data. */
    protected int mTextureDataHandle;

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    protected void allocateBuffers() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureBuffer = bb2.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    protected void compileProgram(){
        // prepare shaders and OpenGL program
        int vertexShader = GLHelper.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLHelper.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glBindAttribLocation(mProgram, 0, "texCoordinate");
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0) {
            throw new RuntimeException("Error creating program.");
        }

        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        Log.i("viewport dimensions", "" + viewport[0]+", " + viewport[1] + ", " + viewport[2] + ", "+ viewport[3]);

    }

    public void compile() {

    }

    protected TexturedSquare() {}

    /**
     * Sets up the drawing object data for use in an OpenGL ES mContext.
     */
    public TexturedSquare(float width, float height, int textureHandle) {
        baseScaleX = width / height;
        baseScaleY = 1;

        mTextureDataHandle = textureHandle;
        allocateBuffers();
        compileProgram();

    }

    protected void specifyVariableHandles() {
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLHelper.checkGlError("mMVPMatrixHandle");

        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLHelper.checkGlError("mTectureUniformHandle");

        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "texCoordinate");
        GLHelper.checkGlError("mTectureUniformHandle");

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");

        specifyExtraVariableHandles();
    }

    protected void specifyExtraVariableHandles() {

    }

    protected void bindExtraVariableHandles() {

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment

        calcTransformation();
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, transformationMatrix, 0);
        Matrix.multiplyMM(boundingBox, 0, transformationMatrix, 0, squareCoords2, 0);

        GLES20.glUseProgram(mProgram);
        specifyVariableHandles();

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        bindExtraVariableHandles();

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the texture coordinate information
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle, mTextureCoordinateDataSize,
                GLES20.GL_FLOAT, false,
                0, textureBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);

    }

    protected void calcTransformation() {
        Matrix.setIdentityM(transformationMatrix, 0);

        Matrix.translateM(transformationMatrix, 0, -centerX, centerY, 0);
        Matrix.rotateM(transformationMatrix, 0, angle, 0, 0, 1.f);
        Matrix.scaleM(transformationMatrix, 0, baseScaleX * scaleX, baseScaleY * scaleY, 1);
    }

    public boolean contains(float x, float y, float[] mvpMatrix) {
        float minX, maxX, minY, maxY;
        float[] indices = new float[4];

        indices[0] = boundingBox[0];
        indices[1] = boundingBox[4];
        indices[2] = boundingBox[8];
        indices[3] = boundingBox[12];

        Arrays.sort(indices);
        minX = indices[0];
        maxX = indices[3];

        indices[0] = boundingBox[1];
        indices[1] = boundingBox[5];
        indices[2] = boundingBox[9];
        indices[3] = boundingBox[13];

        Arrays.sort(indices);
        minY = indices[0];
        maxY = indices[3];

        return minX <= x && x <= maxX && minY <= y && y <= maxY;
    }

    public void rotate(float angle) {
        this.angle = angle;
    }
    public void setScaleFactor(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setScaleFactor(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void setTextureDataHandle(int texture) {
        mTextureDataHandle = texture;
    }

    public void moveCenter(float deltaX, float deltaY) {
        this.centerX += deltaX;
        this.centerY += deltaY;
    }

    public void close() {
        //GLES20.glDeleteProgram(mProgram);
    }

}