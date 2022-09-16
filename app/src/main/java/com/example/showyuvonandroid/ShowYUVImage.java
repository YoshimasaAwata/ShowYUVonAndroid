package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ShowYUVImage {

    private int width;
    private int height;
    private int ySize;
    private int uvSize;

    private byte[] y;
    private byte[] u;
    private byte[] v;
    private int[] argb;

    private InputStream yuvFile;

    private boolean available = false;

    public boolean isAvailable() {
        return available;
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
        argb = new int[ySize];
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
                r = clip(r);
                g = clip(g);
                b = clip(b);
                argb[y_pos + w] = (0xFF << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    public Bitmap getNextBitmap() {
        if (available) {
            transYUV2RGB();
            Bitmap bitmap = Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
            readYUV();
            return bitmap;
        }
        return null;
    }
}
