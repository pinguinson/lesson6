package com.pinguinson.lesson7.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.pinguinson.lesson7.R;
import com.pinguinson.lesson7.database.ArticlesTable;
import com.pinguinson.lesson7.loading.RSSLoadResultReceiver;
import com.pinguinson.lesson7.loading.RSSPullService;
import com.pinguinson.lesson7.database.FeedsContentProvider;

/**
 * Created by pinguinson on 28.01.2015.
 */
public class LoaderActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>{
    static final String CONTENT_URL = "com.pinguinson.lesson7.content";
    private SwipeRefreshLayout swipeLayout;
    private ListView view;
    private SimpleCursorAdapter dataAdapter;
    private long id;
    private String url;
    private static ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        resolver = getContentResolver();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_feed);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);
        view = (ListView) findViewById(R.id.feed);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) dataAdapter.getItem(i);
                String entryUrl = cursor.getString(cursor.getColumnIndexOrThrow(ArticlesTable.ENTRY_URL_COLUMN));
                Intent intent = new Intent(LoaderActivity.this, ShowArticleActivity.class);
                intent.putExtra(CONTENT_URL, entryUrl);
                startActivity(intent);
            }
        });
        id = getIntent().getLongExtra("feedId", -1);
        url = getIntent().getStringExtra("feedUrl");
        initializeView();
    }

    private void initializeView(){
        String[] columns = {
                ArticlesTable.ENTRY_TITLE_COLUMN
        };

        int[] to = {
                R.id.rss_title
        };

        dataAdapter = new SimpleCursorAdapter(this, R.layout.rss_item, null, columns, to, 0);
        view.setAdapter(dataAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        refreshArticles();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 3000);
    }

    private void refreshArticles() {
        resolver.delete(FeedsContentProvider.CONTENT_URI_ENTRIES, ArticlesTable.ENTRY_FEED_ID_COLUMN + "=" + id, null);
        Intent intent = new Intent(this, RSSPullService.class);
        intent.putExtra("feedUrl", url);
        intent.putExtra("feedId", id);
        intent.putExtra("receiver", new RSSLoadResultReceiver(new Handler(), this));
        startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ArticlesTable._ID,
                ArticlesTable.ENTRY_TITLE_COLUMN,
                ArticlesTable.ENTRY_URL_COLUMN,
        };
        return new CursorLoader(this,
                FeedsContentProvider.CONTENT_URI_ENTRIES, projection, ArticlesTable.ENTRY_FEED_ID_COLUMN + "=" + id, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        dataAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        dataAdapter.swapCursor(null);
    }

    public void reload() {
        Cursor c = resolver.query(FeedsContentProvider.CONTENT_URI_ENTRIES, null, ArticlesTable.ENTRY_FEED_ID_COLUMN + "=" + id, null, null);
        dataAdapter.changeCursor(c);
        dataAdapter.notifyDataSetChanged();
    }

    public void showErrorMessage(String url) {
        Toast.makeText(this, "Error reloading " + url + ". Please, try again", Toast.LENGTH_LONG).show();
    }
}
