package com.BaseClass;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by mzc6838 on 2018/4/3.
 */

public class WindowInfo {
    private String windowTitle;
    private String windowUrl;
    private Bitmap windowIcon;

    public WindowInfo(){
        windowTitle = "";
        windowUrl = "";
        windowIcon = null;
    };

    public WindowInfo(String title, String url, Bitmap icon){
        windowTitle = title;
        windowUrl = url;
        windowIcon = icon;
    }

    public String getWindowTitle(){
        return windowTitle;
    }

    public String getWindowUrl(){
        return windowUrl;
    }

    public Bitmap getWindowIcon(){
        return windowIcon;
    }

    public void setWindowTitle(String title){
        windowTitle = title;
    }

    public void  setWindowUrl(String url){
        windowUrl = url;
    }

    public void setWindowIcon(Bitmap icon){
        windowIcon = icon;
    }
}
