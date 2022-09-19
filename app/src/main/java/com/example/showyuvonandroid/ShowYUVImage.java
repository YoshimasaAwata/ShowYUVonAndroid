package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ShowYUVImage {

    private int width;
    private int height;
    private int ySize;
    private int uvSize;

    private byte[] y;
    private byte[] u;
    private byte[] v;
    private ByteBuffer yBuffer;
    private ByteBuffer uBuffer;
    private ByteBuffer vBuffer;

    private InputStream yuvFile;

    private boolean available = false;

    public boolean isAvailable() {
        return available;
    }

    private int[] textureIDY;
    private int[] textureIDU;
    private int[] textureIDV;

    public int getTextureIDY() {
        return textureIDY[0];
    }

    public int getTextureIDU() {
        return textureIDU[0];
    }

    public int getTextureIDV() {
        return textureIDV[0];
    }

    public ShowYUVImage(Context context) {
        int scale = context.getResources().getDisplayMetrics().densityDpi;
        int w = context.getResources().getDimensionPixelSize(R.dimen.width);
        int h = context.getResources().getDimensionPixelSize(R.dimen.height);
        width = w * 160 / scale;
        height = h * 160 / scale;
        ySize = width * height;
        uvSize = (width / 2) * (height / 2);
        y = new byte[ySize];
        u = new byte[uvSize];
        v = new byte[uvSize];
        yBuffer = ByteBuffer.allocateDirect(ySize);
        uBuffer = ByteBuffer.allocateDirect(uvSize);
        vBuffer = ByteBuffer.allocateDirect(uvSize);
    }

    protected void readYUV() {
        if (available) {
            try {
                yuvFile.read(y);
                yuvFile.read(u);
                int size = yuvFile.read(v);
                if (size < uvSize) {
                    available = false;
                }
                else {
                    yBuffer.put(y);
                    uBuffer.put(u);
                    vBuffer.put(v);
                    yBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.position(0);
                }
            } catch (IOException e) {
                Log.e("ReadFile", e.toString());
                available = false;
            }
        }
    }

    public void setYUVFileURL(ContentResolver contentResolver, Uri uri) {
        try {
            Log.i("SetFile", uri.toString());
            yuvFile = contentResolver.openInputStream(uri);
            available = true;
            readYUV();
        }
        catch (Exception e) {
            Log.e("SetFile", e.toString());
            available = false;
        }
    }

    public void init(GL10 gl10) {
        textureIDY = new int[1];
        GLES20.glGenTextures(1, textureIDY, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDY[0]);
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );

        textureIDU = new int[1];
        GLES20.glGenTextures(1, textureIDU, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDU[0]);
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );

        textureIDV = new int[1];
        GLES20.glGenTextures(1, textureIDV, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDV[0]);
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
    }

    public void setNextTexture(GL10 gl10, ShowYUVShader shader) {
        if (isAvailable()) {
            int samplerLocationY = shader.getTextureSamplerUniformY();
            GLES20.glUniform1i(samplerLocationY, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDY[0]);
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_LUMINANCE,
                    width,
                    height,
                    0,
                    GLES20.GL_LUMINANCE,
                    GLES20.GL_UNSIGNED_BYTE,
                    yBuffer);

            int samplerLocationU = shader.getTextureSamplerUniformU();
            GLES20.glUniform1i(samplerLocationU, 1);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDU[0]);
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_LUMINANCE,
                    (width / 2),
                    (height / 2),
                    0,
                    GLES20.GL_LUMINANCE,
                    GLES20.GL_UNSIGNED_BYTE,
                    uBuffer);

            int samplerLocationV = shader.getTextureSamplerUniformV();
            GLES20.glUniform1i(samplerLocationV, 2);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDV[0]);
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_LUMINANCE,
                    (width / 2),
                    (height / 2),
                    0,
                    GLES20.GL_LUMINANCE,
                    GLES20.GL_UNSIGNED_BYTE,
                    vBuffer);

            readYUV();
        }
    }
}
