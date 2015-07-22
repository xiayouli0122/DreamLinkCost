package com.yuri.dreamlinkcost.notification.interfaces;

import android.graphics.Bitmap;

public interface OnImageLoadingCompleted {
    /**
     * Call this method when you finish loading the Image via ImageLoader
     * @param bitmap the bitmap that you would like to report being completed
     */
    void imageLoadingCompleted(Bitmap bitmap);
}
