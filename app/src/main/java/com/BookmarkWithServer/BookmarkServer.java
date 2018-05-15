package com.BookmarkWithServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzc6838 on 2018/4/24.
 */

public class BookmarkServer {

    private List<Bookmark> bookmark;

    public BookmarkServer() {
        bookmark = new ArrayList<>();
    }

    public List<Bookmark> getBookmark() {
        return bookmark;
    }

    public void setBookmark(List<com.BaseClass.Bookmark> bookmark) {
        for(int i = 0; i < bookmark.size(); i++){
            this.bookmark.add(new Bookmark(bookmark.get(i).getTitle(), bookmark.get(i).getUrl()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookmarkServer that = (BookmarkServer) o;

        return bookmark != null ? bookmark.equals(that.bookmark) : that.bookmark == null;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
