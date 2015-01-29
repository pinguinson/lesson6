package com.pinguinson.lesson7.loading;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSLoaderTask extends AsyncTask<String, Void, RSSFeed> {
    private RSSPullService ctx;

    public RSSLoaderTask(RSSPullService ctx) {
        this.ctx = ctx;
    }

    @Override
    protected RSSFeed doInBackground(String... strings) {
        RSSHandler handler = new RSSHandler();
        try {
            return new RSSFeed(strings[0], handler.getArticles(strings[0]));
        } catch (IOException e) {
            return new RSSFeed(strings[0]);
        }
    }

    @Override
    protected void onPostExecute(RSSFeed feed) {
        ctx.onDataLoaded(feed);
    }
}
