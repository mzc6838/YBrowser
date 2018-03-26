package com.mzc6838.ybrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by mzc6838 on 2018/3/25.
 */

public class SettingActivity extends PreferenceActivity {

    private Toolbar mToolbar;
    private SwitchPreference javascript_allow;
    private LocalBroadcastManager localBroadcastManager;
    private EditTextPreference editTextPreference;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.preferences_setting);

        init();
    }

    public void init()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        javascript_allow = (SwitchPreference) findPreference("allow_javascript");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        editTextPreference = (EditTextPreference) findPreference("change_first_page");

        javascript_allow.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(javascript_allow.isChecked() != (Boolean)newValue)
                {
                    boolean value = (Boolean)newValue;
                    javascript_allow.setChecked(value);
                    if(value)
                    {
                        Intent intent = new Intent("com.mzc6838.ybrowser.setting.SET_JAVASCRIPT_ENABLED");
                        localBroadcastManager.sendBroadcast(intent);
                    }else{
                        Intent intent = new Intent("com.mzc6838.ybrowser.setting.SET_JAVASCRIPT_DISABLED");
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
                return true;
            }
        });

        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editTextPreference.setSummary(newValue.toString());
                return true;
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
