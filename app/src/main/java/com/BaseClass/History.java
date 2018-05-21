package com.BaseClass;

import org.litepal.crud.DataSupport;

/**
 * Created by mzc6838 on 2018/4/11.
 */

public class History extends DataSupport{
    private int id;
    private String title;
    private String url;
    private String addTime;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
