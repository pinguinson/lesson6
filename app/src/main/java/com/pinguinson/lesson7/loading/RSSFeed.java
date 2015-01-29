package com.pinguinson.lesson7.loading;

import java.util.ArrayList;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSFeed {

    private String name;
    private String url;
    private ArrayList<RSSArticle> articles;

    public RSSFeed(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public RSSFeed(String url, ArrayList<RSSArticle> articles) {
        this.url = url;
        this.articles = articles;
    }

    public RSSFeed(String url) {
        this.url = url;
    }

    public void setArticles(ArrayList<RSSArticle> articles) {
        this.articles = articles;
    }
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<RSSArticle> getArticles() {
        return articles;
    }
}
