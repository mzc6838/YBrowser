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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

/**
 * Created by mzc6838 on 2018/3/25.
 */

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private Toolbar mToolbar;
    private SwitchPreference javascript_allow;
    private LocalBroadcastManager localBroadcastManager;
    private EditTextPreference changeFirstPage;
    private SharedPreferences sharedPreferences;
    private ListPreference changeUA;
    private SwitchPreference outWindow_allow;
    private ListPreference changeSearchEngine;
    private CardView login_cardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.preferences_setting);

        init();
    }

    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        javascript_allow = (SwitchPreference) findPreference("allow_javascript");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        changeFirstPage = (EditTextPreference) findPreference("change_first_page");
        changeUA = (ListPreference) findPreference("change_UA");
        outWindow_allow = (SwitchPreference) findPreference("allow_outWindow");
        changeSearchEngine = (ListPreference) findPreference("change_search_engine");

        login_cardview = (CardView) findViewById(R.id.login_card);

        javascript_allow.setOnPreferenceChangeListener(this);
        outWindow_allow.setOnPreferenceChangeListener(this);
        changeFirstPage.setOnPreferenceChangeListener(this);
        changeFirstPage.setSummary(sharedPreferences.getString("change_first_page", "http://www.baidu.com"));
        changeUA.setOnPreferenceChangeListener(this);
        changeUA.setSummary(sharedPreferences.getString("change_UA", "Android"));
        changeSearchEngine.setOnPreferenceChangeListener(this);
        switch (sharedPreferences.getString("change_search_engine", "baidu")) {
            case ("baidu"): {
                changeSearchEngine.setSummary("百度(baidu)");
                break;
            }
            case ("google"): {
                changeSearchEngine.setSummary("谷歌(google)");
                break;
            }
            default:
                break;
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        login_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivityForResult(intent, 333);
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case ("allow_javascript"): {
                if (javascript_allow.isChecked() != (Boolean) newValue) {
                    boolean value = (Boolean) newValue;
                    javascript_allow.setChecked(value);
                    if (value) {
                        Intent intent = new Intent("com.mzc6838.ybrowser.setting.SET_JAVASCRIPT_ENABLED");
                        localBroadcastManager.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent("com.mzc6838.ybrowser.setting.SET_JAVASCRIPT_DISABLED");
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
                return true;
            }
            case ("allow_outWindow"): {
                return true;
            }
            case ("change_first_page"): {
                changeFirstPage.setSummary(newValue.toString());
                return true;
            }
            case ("change_UA"): {
                switch (newValue.toString()) {
                    case ("Android"): {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_ANDROID");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("Android");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    case ("iPhone"): {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_IPHONE");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("iPhone");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    case ("PC"): {
                        Intent intent = new Intent("com.mzc6838.ybrowser.SET_UA_PC");
                        localBroadcastManager.sendBroadcast(intent);
                        changeUA.setValue("PC");
                        changeUA.setSummary(newValue.toString());
                        return true;
                    }
                    default:
                        return true;
                }
            }
            case ("change_search_engine"): {
                if (newValue.toString().equals("google")) {
                    changeSearchEngine.setSummary("谷歌(google)");
                } else {
                    changeSearchEngine.setSummary("百度(baidu)");
                }
                changeSearchEngine.setValue(newValue.toString());
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
