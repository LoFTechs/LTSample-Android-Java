package com.loftechs.sample;

import android.app.Application;
import android.content.Context;

public class SampleApp extends Application {

    public static Context context;

    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
