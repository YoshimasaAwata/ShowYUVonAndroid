package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowYUVView extends SurfaceView implements SurfaceHolder.Callback {

    private ShowYUVImage showYUVImage;

    public ShowYUVView(Context context) {
        super(context);
        getHolder().addCallback(this);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(this);
        showYUVImage = new ShowYUVImage(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bmp = showYUVImage.getNextBitmap();
        if (bmp != null) {
            Rect dst = new Rect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
            canvas.drawBitmap(bmp, null, dst, null);
        }
        else {
            canvas.drawARGB(0xFF, 0x00, 0x00, 0xFF);
        }
    }

    public void setYUVFileURL(ContentResolver contentResolver, Uri uri) {
        showYUVImage.setYUVFileURL(contentResolver, uri);
    }

    public void drawNextBitmap() {
        Canvas canvas = getHolder().lockCanvas();
        Bitmap bmp = showYUVImage.getNextBitmap();
        if (bmp != null) {
            Rect dst = new Rect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
            canvas.drawBitmap(bmp, null, dst, null);
        }
        else {
            canvas.drawARGB(0xFF, 0x00, 0x00, 0xFF);
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    public boolean isAvailable() {
        return showYUVImage.isAvailable();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        drawNextBitmap();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
