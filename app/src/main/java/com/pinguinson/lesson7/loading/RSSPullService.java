package com.pinguinson.lesson7.loading;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.pinguinson.lesson7.database.FeedsContentProvider;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSPullService extends IntentService {
    static final int SUCCESS = 0;
    static final int IO_ERROR = 1;
    private long id;
    private ResultReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public RSSPullService() {
        super("PullRssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("feedUrl");
        id = intent.getLongExtra("feedId", -1);
        receiver = intent.getParcelableExtra("receiver");
        new RSSLoaderTask(this).execute(url);
    }

    public void onDataLoaded(RSSFeed feed) {
        Uri uri = Uri.withAppendedPath(FeedsContentProvider.CONTENT_URI_ENTRIES, String.valueOf(id));
        Bundle bundle = new Bundle();
        if (feed.getArticles() == null) {
            bundle.putString("url", feed.getUrl());
            receiver.send(IO_ERROR, bundle);
            return;
        }
        for (RSSArticle article : feed.getArticles()) {
            ContentValues values = article.toContentValue();
            getContentResolver().insert(uri, values);
        }
        if (receiver != null) {
            receiver.send(SUCCESS, Bundle.EMPTY);
        }
    }
}
