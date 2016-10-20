package com.kian.butba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Wait for x seconds, until the home screen is shown.
        Thread timer = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(1500);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent iHomeMenu = new Intent("HOME_SCREEN");
                    startActivity(iHomeMenu);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Kills the activity.
        finish();
    }
}
