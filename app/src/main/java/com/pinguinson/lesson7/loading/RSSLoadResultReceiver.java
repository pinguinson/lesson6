package com.pinguinson.lesson7.loading;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.pinguinson.lesson7.activities.FeedsListActivity;
import com.pinguinson.lesson7.activities.LoaderActivity;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class RSSLoadResultReceiver extends ResultReceiver {
    private LoaderActivity loaderActivity;
    private FeedsListActivity feedsListActivity;

    public RSSLoadResultReceiver(Handler handler, LoaderActivity loaderActivity) {
        super(handler);
        this.loaderActivity = loaderActivity;
    }

    public RSSLoadResultReceiver(Handler handler, FeedsListActivity feedsListActivity) {
        super(handler);
        this.feedsListActivity = feedsListActivity;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        String url = resultData.getString("url");
        super.onReceiveResult(resultCode, resultData);
        if (loaderActivity != null) {
            if (resultCode == RSSPullService.SUCCESS)
                loaderActivity.reload();
            else
                loaderActivity.showErrorMessage(url);
        } else if (feedsListActivity != null) {
            if (resultCode == RSSPullService.IO_ERROR) {
                feedsListActivity.showErrorMessage(url);
            }
        }
    }
}
