package com.larkspur.stockly;

import android.app.Application;

public class MainApplication extends Application {

    public static final String TAG = MainApplication.class.getSimpleName();


    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


}
