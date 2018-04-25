package com.mzc6838.ybrowser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thinkcool.circletextimageview.CircleTextImageView;

import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private CircleTextImageView profile_image;
    private TextView profile_name;

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
        profile_image = (CircleTextImageView) findViewById(R.id.profile_image);
        profile_name = (TextView) findViewById(R.id.profile_name);

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

        if (checkIfLogin()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    profile_name.setText(sp.getString("name", ""));
                    profile_image.setText(sp.getString("name", "").charAt(0) + "");
                    profile_image.setImageDrawable(null);
                    profile_image.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    profile_image.setFillColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profile_name.setText("即刻登录，享受云同步");
                    profile_image.setImageResource(R.drawable.ic_anonymous);
                    profile_image.setText("");
                    profile_image.setFillColor(Color.WHITE);
                }
            });
        }

        login_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfLogin()) {
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 333);
                }else{
                    popAlertDialog();
                }
            }
        });

        login_cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(getSharedPreferences("UserInfo", MODE_PRIVATE).getBoolean("ifLogin", false)){
                    Random random = new Random();
                    profile_image.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    profile_image.setFillColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
                return true;
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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333 && (resultCode == 486 || resultCode == 487)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    profile_name.setText(data.getExtras().getString("name") + "");
                    profile_image.setText(data.getExtras().getString("name").charAt(0) + "");
                    profile_image.setImageDrawable(null);
                    profile_image.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    profile_image.setFillColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                }
            });

        }
    }

    private boolean checkIfLogin() {
        SharedPreferences sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        return sp.getBoolean("ifLogin", false);
    }

    private void popAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setMessage("你确定要退出登录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                        editor.putString("name", "none");
                        editor.putString("tooken", "none");
                        editor.putBoolean("ifLogin", false);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profile_name.setText("即刻登录，享受云同步");
                                profile_image.setImageResource(R.drawable.ic_anonymous);
                                profile_image.setText("");
                                profile_image.setFillColor(Color.WHITE);
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
