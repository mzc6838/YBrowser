package com.mzc6838.ybrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mzc6838 on 2018/4/13.
 */

public class LoginActivity extends Activity implements View.OnClickListener {

    private TextView title;
    private TextInputLayout userName;
    private TextInputLayout password;
    private LinearLayout registerLayout;
    private TextInputLayout emailAddress;
    private TextInputLayout captcha;
    private Button getCaptcha;
    private Button loginOrRegister;
    private TextView ifRegistered;
    private TimeCount timeCount;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private boolean IS_REGISTER = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init(savedInstanceState);
    }

    public void init(@Nullable Bundle savedInstanceState) {

        title = (TextView) findViewById(R.id.login_activity_title);
        userName = (TextInputLayout) findViewById(R.id.user_name);
        password = (TextInputLayout) findViewById(R.id.user_password);
        registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        emailAddress = (TextInputLayout) findViewById(R.id.email_address);
        captcha = (TextInputLayout) findViewById(R.id.captcha);
        getCaptcha = (Button) findViewById(R.id.send_captcha);
        loginOrRegister = (Button) findViewById(R.id.login_or_register_button);
        ifRegistered = (TextView) findViewById(R.id.if_register);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        preferences.getString("name", "");
        preferences.getString("tooken", "");
        preferences.getBoolean("ifLogin", false);

        editor = getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
        editor.apply();

        timeCount = new TimeCount(60000, 1000, getCaptcha);

        getCaptcha.setOnClickListener(this);
        loginOrRegister.setOnClickListener(this);
        ifRegistered.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.if_register): {
                if (!IS_REGISTER) {
                    registerLayout.setVisibility(View.VISIBLE);
                    ifRegistered.setText("已有账号？返回登录");
                    loginOrRegister.setText("注册");
                } else {
                    registerLayout.setVisibility(View.GONE);
                    ifRegistered.setText("没有账号？点击注册");
                    loginOrRegister.setText("登录");
                }
                IS_REGISTER = !IS_REGISTER;
                break;
            }
            case (R.id.login_or_register_button): {
                if (IS_REGISTER) {
                    Log.d("IS_REGISTER", "register");
                    if (validateName(userName, userName.getEditText().getText().toString())
                            && validatePassword(password, password.getEditText().getText().toString())
                            && validateEmail(emailAddress, emailAddress.getEditText().getText().toString())
                            && validateCaptcha(captcha, captcha.getEditText().getText().toString())) {

                        userName.setEnabled(false);
                        password.setEnabled(false);
                        emailAddress.setEnabled(false);
                        captcha.setEnabled(false);

                        OkHttpClient okHttpClient = new OkHttpClient();
                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        bodyBuilder.add("name", userName.getEditText().getText().toString());
                        bodyBuilder.add("password", passwordEncrypt(password.getEditText().getText().toString()));
                        bodyBuilder.add("email", emailAddress.getEditText().getText().toString());
                        bodyBuilder.add("captcha", captcha.getEditText().getText().toString());
                        RequestBody requestBody = bodyBuilder.build();
                        Request request = new Request.Builder()
                                .url(/*注册链接*/"http://toothless.mzc6838.xyz/browser/register.php")
                                .post(requestBody)
                                .build();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Gson gson = new Gson();
                                ErrorResponse errorResponse = gson.fromJson(response.body().string(), ErrorResponse.class);
                                switch (errorResponse.getErrCode()) {
                                    case (0): {
                                        editor.putString("name", userName.getEditText().getText().toString());
                                        editor.putString("tooken", errorResponse.getBody());
                                        editor.putBoolean("ifLogin", true);
                                        editor.apply();
                                        Intent intent = new Intent();
                                        intent.putExtra("name", userName.getEditText().getText().toString());
                                        setResult(486, intent);
                                        finish();
                                        break;
                                    }
                                    case (1): {
                                        popAlertDialog(1);
                                        break;
                                    }
                                    case (3): {
                                        popAlertDialog(3);
                                        break;
                                    }
                                    case (4): {
                                        popAlertDialog(4);
                                        break;
                                    }
                                    case (5): {
                                        popAlertDialog(5);
                                        break;
                                    }
                                    case (6): {
                                        popAlertDialog(6);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                            }
                        });

                    }
                } else {
                    Log.d("IS_REGISTER", "login");

                    if (validateName(userName, userName.getEditText().getText().toString())
                            && validatePassword(password, password.getEditText().getText().toString())) {

                        userName.setEnabled(false);
                        password.setEnabled(false);
                        emailAddress.setEnabled(false);
                        captcha.setEnabled(false);

                        OkHttpClient okHttpClient = new OkHttpClient();
                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        bodyBuilder.add("name", userName.getEditText().getText().toString());
                        bodyBuilder.add("password", password.getEditText().getText().toString());
                        RequestBody requestBody = bodyBuilder.build();
                        Request request = new Request.Builder()
                                .url(/*登录链接*/"http://toothless.mzc6838.xyz/browser/login.php")
                                .post(requestBody)
                                .build();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //Log.d("onResponse233: \n", response.body().string());
                                Gson gson = new Gson();
                                ErrorResponse errorResponse = gson.fromJson(response.body().string(), ErrorResponse.class);
                                switch (errorResponse.getErrCode()) {
                                    case (0): {
                                        editor.putString("name", userName.getEditText().getText().toString());
                                        editor.putString("tooken", errorResponse.getBody());
                                        editor.putBoolean("ifLogin", true);
                                        editor.apply();
                                        Intent intent = new Intent();
                                        intent.putExtra("name", userName.getEditText().getText().toString());
                                        setResult(487, intent);
                                        finish();
                                        break;
                                    }
                                    case (1): {
                                        popAlertDialog(1);
                                        break;
                                    }
                                    case (2): {
                                        popAlertDialog(2);
                                        break;
                                    }
                                    case (8): {
                                        popAlertDialog(8);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                            }
                        });

                    }
                }
                break;
            }
            case (R.id.send_captcha): {
                if (validateName(userName, userName.getEditText().getText().toString())
                        && validateEmail(emailAddress, emailAddress.getEditText().getText().toString())) {
                    timeCount.start();

                    userName.setEnabled(false);
                    password.setEnabled(false);
                    emailAddress.setEnabled(false);
                    captcha.setEnabled(false);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    bodyBuilder.add("name", userName.getEditText().getText().toString());
                    bodyBuilder.add("email", emailAddress.getEditText().getText().toString());
                    RequestBody requestBody = bodyBuilder.build();
                    Request request = new Request.Builder()
                            .url(/*发送验证码*/"http://toothless.mzc6838.xyz/browser/getCaptcha.php")
                            .post(requestBody)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Gson gson = new Gson();
                            String re = response.body().string();
                            Log.d("onResponse: ", re);
                            ErrorResponse errorResponse = gson.fromJson(re, ErrorResponse.class);
                            switch (errorResponse.getErrCode()) {
                                case (1):{
                                    popAlertDialog(1);
                                    break;
                                }
                                case (3): {
                                    popAlertDialog(3);
                                    //timeCount.onFinish();
                                    break;
                                }
                                case (9): {
                                    popAlertDialog(9);
                                    //timeCount.onFinish();
                                    break;
                                }
                                case (10): {
                                    popAlertDialog(10);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    });
                }
                break;
            }
            default:
                break;
        }
    }

    public class TimeCount extends CountDownTimer {

        private Button btn_count;

        public TimeCount(long millisInfuture, long countDownInterval, Button btn_count) {
            super(millisInfuture, countDownInterval);
            this.btn_count = btn_count;
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            btn_count.setEnabled(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_count.setText(millisUntilFinished / 1000 + "秒");
                    btn_count.setBackgroundResource(R.drawable.captcha_button_disable);
                }
            });
        }

        @Override
        public void onFinish() {
            btn_count.setEnabled(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_count.setText("重新发送验证码");
                    btn_count.setBackgroundResource(R.drawable.login_button_shape);
                }
            });
        }
    }

    private void showError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
        textInputLayout.getEditText().setFocusable(true);
        textInputLayout.getEditText().setFocusableInTouchMode(true);
        textInputLayout.getEditText().requestFocus();
    }

    private boolean validateName(TextInputLayout textInputLayout, String input) {
        textInputLayout.setErrorEnabled(true);
        if (input.isEmpty()) {
            showError(userName, "用户名不能为空");
            return false;
        }
        if (input.length() < 4) {
            showError(userName, "用户名过短,至少4位");
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword(TextInputLayout textInputLayout, String input) {
        textInputLayout.setErrorEnabled(true);
        if (input.isEmpty()) {
            showError(password, "密码不能为空");
            return false;
        }
        if (input.length() < 6 || input.length() > 18) {
            showError(password, "密码长度为6-18位");
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmail(TextInputLayout textInputLayout, String input) {
        textInputLayout.setErrorEnabled(true);
        if (input.isEmpty()) {
            showError(emailAddress, "邮箱不能为空");
            return false;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            textInputLayout.setErrorEnabled(false);
            return true;
        } else {
            showError(emailAddress, "邮箱地址不合法");
            return false;
        }
    }

    private boolean validateCaptcha(TextInputLayout textInputLayout, String input) {
        textInputLayout.setErrorEnabled(true);
        if (input.isEmpty()) {
            showError(captcha, "验证码不能为空");
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }


    private String getSalt() {
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            result += random.nextInt();
        }
        result = SHA256(result);
        return result.substring(0, 8);
    }

    private enum DigestType{

        MD5("MD5")
        ,SHA("SHA")
        ,SHA256("SHA-256")
        ,SHA512("SHA-512");

        private String digestDesc;

        private DigestType(String digestDesc){
            this.digestDesc = digestDesc;
        }

        public String getDigestDesc() {
            return digestDesc;
        }
    }

    private final static String digest(String sourceStr,DigestType type) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            byte[] btInput = sourceStr.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance(type.digestDesc);
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static String SHA256(String s){
        return digest(s, DigestType.SHA256);
    }

    private String passwordEncrypt(String password) {
        String salt;
        salt = getSalt();
        return salt + "#" + SHA256(SHA256(password) + salt);
    }

    private void popAlertDialog(int code) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        switch (code) {
            case (1): {
                builder.setMessage("服务器这边出了点问题，稍后再试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (2): {
                builder.setMessage("用户名或密码错误，再试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (3): {
                builder.setMessage("用户名已经存在，换一个试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (4): {
                builder.setMessage("这个邮箱已经被使用过了，换一个试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (5): {
                builder.setMessage("验证码已经过期了，请重新发送一个验证码")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (6): {
                builder.setMessage("验证码错误")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (8): {
                builder.setMessage("用户名或密码错误，再试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (9): {
                builder.setMessage("邮件发送失败了，再试试吧")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            case (10): {
                builder.setMessage("验证码已成功发送到您的邮箱，请去确认下")
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setEnabled(true);
                                password.setEnabled(true);
                                emailAddress.setEnabled(true);
                                captcha.setEnabled(true);
                                dialog.dismiss();
                            }
                        });
                break;
            }
            default:
                break;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        new Thread() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }.start();
    }
}
