package com.mzc6838.ybrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
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
    private EditTextPreference changeFirstPage;
    private SharedPreferences sharedPreferences;
    private ListPreference changeUA;

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
        changeFirstPage = (EditTextPreference) findPreference("change_first_page");
        changeUA = (ListPreference) findPreference("change_UA");

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

        changeFirstPage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                changeFirstPage.setSummary(newValue.toString());
                return true;
            }
        });
        changeFirstPage.setSummary(sharedPreferences.getString("change_first_page", "http://toothless.mzc6838.xyz"));

        changeUA.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch (newValue.toString())
                {
                    case ("Android"):
                    {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_ANDROID");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("Android");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    case("iPhone"):
                    {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_IPHONE");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("iPhone");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    case("PC"):
                    {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_PC");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("PC");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    default:return true;
                }
            }
        });
        changeUA.setSummary(sharedPreferences.getString("change_UA", "Android"));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
