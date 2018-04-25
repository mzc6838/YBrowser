package com.BookmarkWithServer;

/**
 * Created by mzc6838 on 2018/4/24.
 */

public class Bookmark {
    private String url;
    private String title;

    public Bookmark(String title, String url) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookmark bookmark = (Bookmark) o;

        if (url != null ? !url.equals(bookmark.url) : bookmark.url != null) return false;
        return title != null ? title.equals(bookmark.title) : bookmark.title == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
