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
    private ByteBuffer rgb;

    private InputStream yuvFile;

    private boolean available = false;

    public boolean isAvailable() {
        return available;
    }

    private int[] textureID;

    public int getTextureID() {
        return textureID[0];
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
        rgb = ByteBuffer.allocateDirect(ySize * 3);
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

    protected int clip(int n) {
        return (n <= 0) ? 0 : ((n >= 255) ? 255 : n);
    }

    protected void transYUV2RGB() {
        for (int h = 0; h < height; h++) {
            int y_pos = h * width;
            int uv_pos = (h / 2) * (width / 2);

            for (int w = 0; w < width; w++) {
                int yi = y[y_pos + w] & 0x0FF;
                int ui = u[uv_pos + (w / 2)] & 0x0FF;
                int vi = v[uv_pos + (w / 2)] & 0x0FF;

                double y16 = yi - 16.0;
                double u128 = ui - 128.0;
                double v128 = vi - 128.0;

                int r = (int) ((1.164 * y16) + (0.0 * u128) + (1.596 * v128));
                int g = (int) ((1.164 * y16) + (-0.392 * u128) + (-0.813 * v128));
                int b = (int) ((1.164 * y16) + (2.017 * u128) + (0.0 * v128));
                rgb.put((byte)clip(r));
                rgb.put((byte)clip(g));
                rgb.put((byte)clip(b));
            }
        }
        rgb.position(0);
    }

    public void init(GL10 gl10) {
        textureID = new int[1];
        GLES20.glGenTextures(1, textureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0]);
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
//        gl10.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE );
    }

    public void setNextTexture(GL10 gl10, ShowYUVShader shader) {
        if (isAvailable()) {
            transYUV2RGB();
            int samplerLocation = shader.getTextureSamplerUniform();
            GLES20.glUniform1i(samplerLocation, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0]);
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGB,
                    width,
                    height,
                    0,
                    GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_BYTE,
                    rgb);
            readYUV();
        }
    }
}
