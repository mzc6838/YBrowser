package com.BaseClass;

import org.litepal.crud.DataSupport;

/**
 * Created by mzc6838 on 2018/4/8.
 */

public class Bookmark extends DataSupport {

    public int id;
    private String title;
    private String url;

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
