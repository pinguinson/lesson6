package com.pinguinson.lesson7.activities;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.pinguinson.lesson7.database.FeedsTable;


/**
 * Created by pinguinson on 28.01.2015.
 */
public class FeedsListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AddFeedDialog.OnCompleteListener {
    private ListView view;
    protected SimpleCursorAdapter dataAdapter;
    private static ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_list_activity);

        resolver = getContentResolver();
        displayListView();

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = dataAdapter.getCursor();
                String url = c.getString(c.getColumnIndexOrThrow(FeedsTable.FEED_URL_COLUMN));
                Intent intent = new Intent(FeedsListActivity.this, LoaderActivity.class);
                intent.putExtra("feedId", l);
                intent.putExtra("feedUrl", url);
                startActivity(intent);
            }
        });

        view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
                Uri feedsUri = Uri.parse(FeedsContentProvider.CONTENT_URI_FEEDS.toString() + "/" + id);
                resolver.delete(FeedsContentProvider.CONTENT_URI_ENTRIES, ArticlesTable.ENTRY_FEED_ID_COLUMN + "=" + id, null);
                resolver.delete(feedsUri, null, null);
                dataAdapter.changeCursor(resolver.query(FeedsContentProvider.CONTENT_URI_FEEDS, null, null, null, null));
                dataAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void displayListView() {
        String[] columns = {
                FeedsTable.FEED_TITLE_COLUMN,
                FeedsTable.FEED_URL_COLUMN,
        };

        int[] to = {
                R.id.channel_name,
                R.id.channel_url
        };

        dataAdapter = new SimpleCursorAdapter(this, R.layout.feeds_list_item, null, columns, to, 0);
        view = (ListView) findViewById(R.id.channels_list);
        view.setAdapter(dataAdapter);
        getLoaderManager().initLoader(0, null, this);
        Toast.makeText(this, "click 'add' button to add feed", Toast.LENGTH_LONG).show();
    }


    private long insertFeed(String name, String url){
        ContentValues values = new ContentValues();
        values.put(FeedsTable.FEED_TITLE_COLUMN, name);
        values.put(FeedsTable.FEED_URL_COLUMN, url);
        Uri id = resolver.insert(FeedsContentProvider.CONTENT_URI_FEEDS, values);
        dataAdapter.changeCursor(resolver.query(FeedsContentProvider.CONTENT_URI_FEEDS, null, null, null, null));
        dataAdapter.notifyDataSetChanged();
        return Long.parseLong(id.getLastPathSegment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feeds, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        new AddFeedDialog().show(ft, "dialog");
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                FeedsTable._ID,
                FeedsTable.FEED_URL_COLUMN,
                FeedsTable.FEED_TITLE_COLUMN
        };
        return new CursorLoader(this,
                FeedsContentProvider.CONTENT_URI_FEEDS, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        dataAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        dataAdapter.swapCursor(null);
    }

    @Override
    public void onComplete(String name, String url) {
        long id = insertFeed(name, url);
        Intent intent = new Intent(this, RSSPullService.class);
        intent.putExtra("feedUrl", url);
        intent.putExtra("feedId", id);
        intent.putExtra("receiver", new RSSLoadResultReceiver(new Handler(), this));
        startService(intent);
    }

    public void showErrorMessage(String url) {
        Toast.makeText(this, "Error loading " + url + ". Please, make sure that the address is valid.", Toast.LENGTH_LONG).show();
        resolver.delete(FeedsContentProvider.CONTENT_URI_FEEDS,
                FeedsTable.FEED_URL_COLUMN + "='" + url + "'", null);
        dataAdapter.changeCursor(resolver.query(FeedsContentProvider.CONTENT_URI_FEEDS, null, null, null, null));
        dataAdapter.notifyDataSetChanged();
    }
}
