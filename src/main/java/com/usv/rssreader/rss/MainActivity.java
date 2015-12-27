package com.usv.rssreader.rss;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String PARAM_PINTENT = "pendingIntent";
    public static int RESULT_OK = 1;
    final String LOG = "myLogs";
    ListView listView;
    PendingIntent pendingIntent;
    static final Uri RSS_URI = Uri.parse("content://com.usv.rssreader.provider.RSSRepository/rss_list");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "Activity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RSSService.class);
        pendingIntent = createPendingResult(1, intent, 0);

        String[] columns=new String[]{RSSProvider.RSS_ID + " as _id", RSSProvider.RSS_TITLE, RSSProvider.RSS_DESCRIPTION, RSSProvider.RSS_LINK};
        Cursor cursor =  getContentResolver().query(RSS_URI, columns, null, null, null);

        String from[] = { RSSProvider.RSS_TITLE, RSSProvider.RSS_DESCRIPTION, RSSProvider.RSS_LINK };
        int to[] = {R.id.title, R.id.description, R.id.link};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to, 0);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_red_dark, android.R.color.holo_orange_light);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        startService(intent.putExtra(PARAM_PINTENT, pendingIntent));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Intent intent = new Intent(this, RSSService.class);
        pendingIntent = createPendingResult(1, intent, 0);
        startService(intent.putExtra(PARAM_PINTENT, pendingIntent));
    }

}
