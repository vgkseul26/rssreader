package com.usv.rssreader.rss;

import android.content.Intent;
import android.database.Cursor;
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
    public static String PARAM_PINTENT = "pendingIntent";
    public static int INSERT_RSS = 100;
    final String LOG = "myLogs";
    ListView lw = null;
    final Uri RSS_URI = Uri.parse("content://com.usv.rssreader.provider.RSSRepository/rss_lists");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "Activity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, RSSService.class);

        Cursor cursor = getContentResolver().query(RSS_URI, null, null, null, null);
        String from[] = { RSSProvider.RSS_ID, RSSProvider.RSS_TITLE, RSSProvider.RSS_DESCRIPTION, RSSProvider.RSS_LINK };
        int to[] = { android.R.id.list};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
        lw = (ListView) findViewById(R.id.listView);
        lw.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_red_dark, android.R.color.holo_orange_light);
        lw.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                int topRowVerticalPosition = (lw == null || lw.getChildCount() == 0) ? 0 : lw.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        startService(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == INSERT_RSS) {
         /*   ArrayAdapter<? extends Parcelable> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data.getParcelableArrayListExtra("rss"));
            lw.setAdapter(adapter);*/
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Intent intent = new Intent(this, RSSService.class);
        startService(intent);
    }

}
