package com.emplaceme.feedback.Applications;

import android.app.Application;
import android.content.Context;

/**
 * Created by developer on 02/01/16.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public static MyApplication getsInstance() {
        return sInstance;
    }

    public static Context getAppContext() {

        return sInstance.getApplicationContext();
    }


}
