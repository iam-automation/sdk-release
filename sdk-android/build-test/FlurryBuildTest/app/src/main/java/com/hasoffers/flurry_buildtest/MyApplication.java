package com.hasoffers.flurry_buildtest;

import android.app.Application;

import com.flurry.android.FlurryAgent;

/**
 * Created by johng on 12/4/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        // init Flurry
        FlurryAgent.init(this, "YOUR_API_KEY");
    }
}
