package com.example.showyuvonandroid;

import android.content.Context;
import android.graphics.Bitmap;

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
}
