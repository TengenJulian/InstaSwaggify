package zwaggerboyz.instaswaggify;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Created by zeta on 8/5/14.
 */
public class GLHelper {
    private static final String TAG = "MyGLRenderer";
    private static final int scratch[] = new int[1];

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        checkGlError("adding shader code");
        GLES20.glCompileShader(shader);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            Log.i("shader log:", GLES20.glGetShaderInfoLog(shader));
            if (type == GLES20.GL_VERTEX_SHADER){
                throw new RuntimeException("Error compiling shader type: Vertex Shader");
            }
            else if (type == GLES20.GL_FRAGMENT_SHADER){
                throw new RuntimeException("Error compiling shader type: Fragment Shader");
            }
            else {
                throw new RuntimeException("Error: compiling unknown shader type");
            }
        }

        return shader;
    }

    public static int loadGLTexture(Bitmap bitmap) {
        int textureDataHandle;
        // generate one texture pointer

        GLES20.glGenTextures(1, scratch, 0);
        textureDataHandle = scratch[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        checkGlError("Generate bitmaps");

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        checkGlError("Settings mipmap filter");


        return textureDataHandle;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error );
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}
