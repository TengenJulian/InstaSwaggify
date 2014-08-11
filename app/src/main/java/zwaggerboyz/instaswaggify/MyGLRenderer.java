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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zwaggerboyz.instaswaggify.filters.BrightnessFilter;
import zwaggerboyz.instaswaggify.filters.ColorizeFilter;
import zwaggerboyz.instaswaggify.filters.ContrastFilter;
import zwaggerboyz.instaswaggify.filters.GaussianBlurFilter;
import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;
import zwaggerboyz.instaswaggify.filters.IdentityFilter;
import zwaggerboyz.instaswaggify.filters.InvertColorsFilter;
import zwaggerboyz.instaswaggify.filters.NoiseFilter;
import zwaggerboyz.instaswaggify.filters.RotationFilter;
import zwaggerboyz.instaswaggify.filters.SaturationFilter;
import zwaggerboyz.instaswaggify.filters.SepiaFilter;
import zwaggerboyz.instaswaggify.filters.ThresholdBlurFilter;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static final String[] RESOURCE_NAME_MAP =
            {
                    "InstaAchievement",
                    "InstaBeard",
                    "InstaBeard2",
                    "InstaBling",
                    "InstaBling2",
                    "InstaCap",
                    "InstaCrown",
                    "InstaDealWithIt",
                    "InstaDew",
                    "InstaDoge",
                    "InstaDoritos",
                    "InstaFedora",
                    "InstaHitmarker",
                    "InstaJoint",
                    "InstaMLG",
                    "InstaMoney",
                    "InstaMoustache",
                    "InstaMoustache2",
                    "InstaNoScope",
                    "InstaNova",
                    "InstaPlus100",
                    "InstaSnoop",
                    "InstaSwag",
                    "InstaWeed",
            };
    static final int[] RESOURCE_MAP = {
            R.drawable.instaachievement,
            R.drawable.instabeard,
            R.drawable.instabeard2,
            R.drawable.instabling,
            R.drawable.instabling2,
            R.drawable.instacap,
            R.drawable.instacrown,
            R.drawable.instadealwithit,
            R.drawable.instadew,
            R.drawable.instadoge,
            R.drawable.instadoritos,
            R.drawable.instafedora,
            R.drawable.instahitmarker,
            R.drawable.instajoint,
            R.drawable.instamlg,
            R.drawable.instamoney,
            R.drawable.instamoustache,
            R.drawable.instamoustache2,
            R.drawable.instanoscope,
            R.drawable.instanova,
            R.drawable.instaplus100,
            R.drawable.instasnoop,
            R.drawable.instaswag,
            R.drawable.instaweed
    };
    private static final int[] TEXTURE_DATA_HANDLES = new int[RESOURCE_MAP.length];
    private static final int[] TEXTURE_WIDTH_MAP = new int[RESOURCE_MAP.length];
    private static final int[] TEXTURE_HEIGHT_MAP = new int[RESOURCE_MAP.length];

    private static final int[] scratch = new int[1];
    private static float Width;
    private static float Height;
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private List<Overlay> mOverlays;
    private List<Overlay> toBeCompiled = new ArrayList<Overlay>();
    private FilterRenderer filterRenderer;
    private ExportHelper mExportHelper;
    private boolean mShare;
    private MainActivity mContext;
    private float ratio;
    private int height, width;
    private boolean savePicture;

    public static float getWidth() {
        return Width;
    }

    public static float getHeight() {
        return Height;
    }

    public static void loadCachedTextureResource(Overlay overlay, Context context) {
        int textureId = overlay.getTextureId();
        int textureDataHandle = TEXTURE_DATA_HANDLES[textureId];
        int width, height;

        if (textureDataHandle == -1) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), RESOURCE_MAP[textureId]);
            textureDataHandle = GLHelper.loadGLTexture(bitmap);
            TEXTURE_WIDTH_MAP[textureId] = width = bitmap.getWidth();
            TEXTURE_HEIGHT_MAP[textureId] = height = bitmap.getHeight();
            bitmap.recycle();

        }
        else {
            width = TEXTURE_WIDTH_MAP[textureId];
            height = TEXTURE_HEIGHT_MAP[textureId];
        }

        overlay.calcBaseScale(width, height);
        overlay.setTextureDataHandle(textureDataHandle);

    }

    public static void clearCache() {
        Arrays.fill(TEXTURE_DATA_HANDLES, -1);
    }

    public void setActivity(MainActivity activity) {
        this.mContext = activity;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public float getRatio(){
        return ratio;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mExportHelper = new ExportHelper(mContext);
        // Set the background frame color
        GLES20.glClearColor(0.3f, 0.0f, 0.0f, 1.0f);

        // No culling of back faces
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // No depth testing
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL );
        GLES20.glDepthMask( true );

        // Enable blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Allocates vertex, texture, and draw order buffer for TexturedSquare and all it's children.
        TexturedSquare.allocateBuffers();
        TexturedSquare.compileProgram(TexturedSquare.vertexShaderCode, TexturedSquare.fragmentShaderCode);

        BoundingBox.compileProgram(BoundingBox.vertexShaderCode, BoundingBox.fragmentShaderCode);

        // Compile all the filter programs
        BrightnessFilter.compileProgram(BrightnessFilter.vertexShaderCode, BrightnessFilter.fragmentShaderCode);
        ContrastFilter.compileProgram(ContrastFilter.vertexShaderCode, ContrastFilter.fragmentShaderCode);
        GaussianBlurFilter.compileProgram(GaussianBlurFilter.vertexShaderCode, GaussianBlurFilter.fragmentShaderCode);
        RotationFilter.compileProgram(RotationFilter.vertexShaderCode, RotationFilter.fragmentShaderCode);
        SaturationFilter.compileProgram(SaturationFilter.vertexShaderCode, SaturationFilter.fragmentShaderCode);
        SepiaFilter.compileProgram(SepiaFilter.vertexShaderCode, SepiaFilter.fragmentShaderCode);
        NoiseFilter.compileProgram(NoiseFilter.vertexShaderCode, NoiseFilter.fragmentShaderCode);
        InvertColorsFilter.compileProgram(InvertColorsFilter.vertexShaderCode, InvertColorsFilter.fragmentShaderCode);
        ColorizeFilter.compileProgram(ColorizeFilter.vertexShaderCode, ColorizeFilter.fragmentShaderCode);
        ThresholdBlurFilter.compileProgram(ThresholdBlurFilter.vertexShaderCode, ThresholdBlurFilter.fragmentShaderCode);
        IdentityFilter.compileProgram(IdentityFilter.vertexShaderCode, IdentityFilter.fragmentShaderCode);

        clearCache();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        for (Overlay overlay : toBeCompiled) {
            loadCachedTextureResource(overlay, mContext);
        }
        toBeCompiled.clear();

        filterRenderer.draw(mMVPMatrix);

        // Draw overlays
        for (int i = mOverlays.size() - 1; 0 <= i; i--) {
            mOverlays.get(i).draw(mMVPMatrix);
        }

        if (savePicture) {
            executeSavePicture();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        Width = width;
        Height = height;

        Log.i("widht, height", width + ", " + height);

        ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        if (filterRenderer == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.data);
            filterRenderer = new FilterRenderer(width, height, bitmap);
        }
    }

    public Overlay getSelection(float x, float y) {

        for (int i = 0; i < mOverlays.size(); i++) {
            Overlay overlay = mOverlays.get(i);

            if (overlay.contains(x, y, mMVPMatrix))
                return overlay;
        }

        return null;
    }

    public int getSelectionIndex(float x, float y) {

        for (int i = 0; i < mOverlays.size(); i++) {
            Overlay overlay = mOverlays.get(i);

            if (overlay.contains(x, y, mMVPMatrix))
                return i;
        }

        return -1;
    }

    public void savePicture(boolean share) {
        savePicture = true;
        mShare = share;

    }

    public void executeSavePicture() {
        int screenshotSize = this.width * this.height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());

        GLES20.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);

        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;

        for (int i = 0; i < screenshotSize; ++i) {
            // The alpha and green channels' positions are preserved while the red and blue are swapped
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
        savePicture = false;

        mContext.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       mExportHelper.exportPicture(mShare, bitmap);
                                   }
                               }
        );
    }

    public void setImage(Bitmap bitmap) {
        filterRenderer.setImage(bitmap);
    }

    public void setOverlays(List<Overlay> overlays) {
        mOverlays = overlays;
    }

    public void setFilters(List<AbstractFilterClass> filters) {
        filterRenderer.setFilters(filters);
    }

    public void addToCompileQueue(Overlay overlay) {
        toBeCompiled.add(overlay);
    }

}