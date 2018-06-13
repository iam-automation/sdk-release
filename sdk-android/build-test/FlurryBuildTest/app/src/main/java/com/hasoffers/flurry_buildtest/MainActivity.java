package com.hasoffers.flurry_buildtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.flurry.android.FlurryAgent;
import com.tune.Tune;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tune.init(this, "your_advertiser_id", "your_key");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "YOUR_API_KEY");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
