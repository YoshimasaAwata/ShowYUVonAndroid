package com.example.showyuvonandroid;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ShowYUVView extends View {

    private ShowYUVImage showYUVImage;

    public ShowYUVView(Context context) {
        super(context);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        showYUVImage = new ShowYUVImage(context);
    }

    public ShowYUVView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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

    public boolean isAvailable() {
        return showYUVImage.isAvailable();
    }
}
