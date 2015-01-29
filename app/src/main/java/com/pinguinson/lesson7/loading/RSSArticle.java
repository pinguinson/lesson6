package com.pinguinson.lesson7.loading;

import android.content.ContentValues;

import com.pinguinson.lesson7.database.ArticlesTable;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSArticle {

    private String title;
    private String url;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContentValues toContentValue() {
        ContentValues values = new ContentValues();
        values.put(ArticlesTable.ENTRY_URL_COLUMN, url);
        values.put(ArticlesTable.ENTRY_TITLE_COLUMN, title);
        return values;
    }

}
