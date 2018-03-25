package com.mzc6838.ybrowser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;

/**
 * Created by mzc6838 on 2018/3/25.
 */

public class WelcomeActivity extends Activity {

    public static final String WELCOME_BROADCAST = "com.mzc6838.ybrowser.WELCOME_BROADCAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        while(true)
        {
            if(MainActivity.WELCOME_SHOULD_END == 999)
            {
                Log.d("Welcome", "has ended");
                break;
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        try{
            Thread.currentThread().sleep(3000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finish();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_out);
        super.onWindowFocusChanged(hasFocus);
    }
}
