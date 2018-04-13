package com.mzc6838.ybrowser;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mzc6838 on 2018/4/13.
 */

public class LoginActivity extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init(savedInstanceState);
    }

    public void init(@Nullable Bundle savedInstanceState){

    }
}
