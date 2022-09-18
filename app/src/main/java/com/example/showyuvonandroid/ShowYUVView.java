package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShowYUVView extends GLSurfaceView {

    private ShowYUVImage showYUVImage;
    private Renderer renderer;
    private ShowYUVShader shader;

    public ShowYUVView(Context context) {
        super(context);
        init(context);
    }

    public ShowYUVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        setEGLContextClientVersion(2);
        renderer = new ShowYUVRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        showYUVImage = new ShowYUVImage(context);
    }

    public void setYUVFileURL(ContentResolver contentResolver, Uri uri) {
        showYUVImage.setYUVFileURL(contentResolver, uri);
    }

    public boolean isAvailable() {
        boolean rc = false;
        if (shader != null) {
            rc = showYUVImage.isAvailable() && shader.isAvailable();
        }
        return rc;
    }

    protected class ShowYUVRenderer implements Renderer {

        private final float[] TEXTURE_DATA = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };

        private final float[] VERTEX_DATA = {
                -1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
        };

        private FloatBuffer textureBuffer;
        private FloatBuffer vertexBuffer;
        private int[] vertexBufferIDs = new int[1];
        private int[] textureBufferIDs = new int[1];

        public FloatBuffer createFloatDirect(float[] array) {
            ByteBuffer workBuffer = ByteBuffer.allocateDirect(array.length * 4);    // 頂点数 x 4バイト
            workBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer floatBuffer = workBuffer.asFloatBuffer();
            floatBuffer.put(array);
            floatBuffer.position(0);
            return floatBuffer;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            gl10.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

            try {
                shader = new ShowYUVShader();
            }
            catch (GLException e) {
                String message = e.getMessage();
                Log.e("ShaderProgram", message);
                return;
            }

            vertexBuffer = createFloatDirect(VERTEX_DATA);
            GLES20.glGenBuffers(1, vertexBufferIDs, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferIDs[0]);
            GLES20.glBufferData(
                    GLES20.GL_ARRAY_BUFFER,
                    (VERTEX_DATA.length * 4),
                    vertexBuffer,
                    GLES20.GL_STATIC_DRAW);

            textureBuffer = createFloatDirect(TEXTURE_DATA);
            GLES20.glGenBuffers(1, textureBufferIDs, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferIDs[0]);
            GLES20.glBufferData(
                    GLES20.GL_ARRAY_BUFFER,
                    (TEXTURE_DATA.length * 4),
                    textureBuffer,
                    GLES20.GL_STATIC_DRAW);

            showYUVImage.init(gl10);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
//            gl10.glMatrixMode(GL10.GL_PROJECTION);
//            GLU.gluOrtho2D(gl10, -1.0f, 1.0f, -1.0f, 1.0f);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glUseProgram(shader.getProgramID());
            showYUVImage.setNextTexture(gl10, shader);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            int positionAttrib = shader.getPositionAttribute();
            int vertexUVAttrib = shader.getVertexUVAttribute();

            GLES20.glEnableVertexAttribArray(positionAttrib);
            GLES20.glEnableVertexAttribArray(vertexUVAttrib);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferIDs[0]);
            GLES20.glVertexAttribPointer(
                    positionAttrib,
                    3, // サイズ
                    GLES20.GL_FLOAT, // タイプ
                    false, // 正規化？
                    0, // ストライド
                    0 //vertexBuffer // 配列バッファ
            );

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferIDs[0]);
            GLES20.glVertexAttribPointer(
                    vertexUVAttrib,
                    2, // サイズ
                    GLES20.GL_FLOAT, // タイプ
                    false, // 正規化？
                    0, // ストライド
                    0 //textureBuffer // 配列バッファ
            );

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

            GLES20.glDisableVertexAttribArray(vertexUVAttrib);
            GLES20.glDisableVertexAttribArray(positionAttrib);
        }
    }
}
