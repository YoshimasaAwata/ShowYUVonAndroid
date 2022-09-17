package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShowYUVView extends GLSurfaceView {

    private ShowYUVImage showYUVImage;
    private Renderer renderer;

    public ShowYUVView(Context context) {
        super(context);
        init(context);
    }

    public ShowYUVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        setEGLContextClientVersion(1);
        renderer = new ShowYUVRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        showYUVImage = new ShowYUVImage(context);
    }

    public void setYUVFileURL(ContentResolver contentResolver, Uri uri) {
        showYUVImage.setYUVFileURL(contentResolver, uri);
    }

    public boolean isAvailable() {
        return showYUVImage.isAvailable();
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
            textureBuffer = createFloatDirect(TEXTURE_DATA);
            vertexBuffer = createFloatDirect(VERTEX_DATA);
            showYUVImage.init(gl10);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            GLU.gluOrtho2D(gl10, -1.0f, 1.0f, -1.0f, 1.0f);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            showYUVImage.setNextTexture(gl10);
            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, showYUVImage.getTextureID());
            gl10.glEnable(GL10.GL_TEXTURE_2D);
            gl10.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4 );
            gl10.glDisable(GL10.GL_TEXTURE_2D);
            gl10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
    }
}
