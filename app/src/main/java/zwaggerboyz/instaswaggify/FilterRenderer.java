package zwaggerboyz.instaswaggify;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

import zwaggerboyz.instaswaggify.filters.AbstractFilterClass;
import zwaggerboyz.instaswaggify.filters.IdentityFilter;


/**
 * Created by zeta on 8/5/14.
 */
public class FilterRenderer {
    private int inputIndex = 1;
    private int outputIndex = 0;
    private int width;
    private int height;
    private int backgroundTex;
    private Bitmap mBitmap;

    private int[] fboTextures = new int[2];
    private int[] frameBuffers = new int[2];
    private TexturedSquare mBackground;
    private List<AbstractFilterClass> mFilters = new ArrayList<AbstractFilterClass>();
    private IdentityFilter identity;

    public FilterRenderer(int width, int height, Bitmap background) {
        this.width = width;
        this.height = height;

        backgroundTex = GLHelper.loadGLTexture(background);
        mBackground = new TexturedSquare(width, height, backgroundTex);

        background.recycle();
        createFrameBuffers();
        identity = new IdentityFilter();
        identity.allocateAndCompile();
    }

    private void recreateFrameBuffers() {
        GLES20.glDeleteFramebuffers(2, frameBuffers, 0);
        GLES20.glDeleteTextures(2, fboTextures, 0);

        createFrameBuffers();
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;

    }

    private void createFrameBuffers() {
        GLES20.glGenFramebuffers(2, frameBuffers, 0);

        GLES20.glGenTextures(2, fboTextures, 0);
        GLHelper.checkGlError("texColorBuffer");

        for (int i = 0; i < 2; i ++) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[i]);
            GLHelper.checkGlError("bindbuffer");


            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextures[i]);
            GLHelper.checkGlError("bindtexture");


            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null
            );
            GLHelper.checkGlError("create texture");

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLHelper.checkGlError("setting texture min filter");
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLHelper.checkGlError("setting texture mag filter");

            GLES20.glFramebufferTexture2D(
                    GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTextures[i], 0
            );
            GLHelper.checkGlError("attaching texture to framebuffer");

        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLHelper.checkGlError("resetting to default framebuffer");

    }

    public void draw(float[] mvpMatrix) {
        if (mBitmap != null) {
            int[] scratch = {backgroundTex};
            GLES20.glDeleteTextures(1, scratch, 0);

            backgroundTex = GLHelper.loadGLTexture(mBitmap);
            mBackground.setTextureDataHandle(backgroundTex);
            mBitmap.recycle();
            mBitmap = null;
        }

        int size = mFilters.size();
        int skip = 0;
        if (size == 0) {
            mBackground.draw(mvpMatrix);
            return;
        }
        else if (size % 2 == 0) {
            skip = 1;
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[outputIndex]);
        mBackground.draw(mvpMatrix);

        inputIndex ^= 1;
        outputIndex ^= 1;

        for (int i = 0; i < size - skip; i++) {
            AbstractFilterClass filter = mFilters.get(i);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextures[inputIndex]);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[outputIndex]);

            filter.draw(mvpMatrix, fboTextures[inputIndex]);

            inputIndex ^= 1;
            outputIndex ^= 1;
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextures[inputIndex]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        if (skip == 1) {
            AbstractFilterClass filter = mFilters.get(size - 1);
            filter.draw(mvpMatrix, fboTextures[inputIndex]);
        }
        else {
            identity.draw(mvpMatrix, fboTextures[inputIndex]);
        }

    }

    public void setFilters(List<AbstractFilterClass> filters) {
        mFilters = filters;
    }
}
