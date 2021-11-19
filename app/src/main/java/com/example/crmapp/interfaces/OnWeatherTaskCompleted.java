package com.example.crmapp.interfaces;

import android.graphics.Bitmap;

public interface OnWeatherTaskCompleted {
    void onWeatherTaskCompleted(String degrees, String description, Bitmap iconBitmap);
}
