package com.usv.rssreader.rss;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RSSService extends Service {
    ExecutorService executorService;
    final String LOG = "myLogs";

    public RSSService() {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOG, "Service created");
        Toast.makeText(this, "Служба создана", Toast.LENGTH_SHORT).show();
        super.onCreate();
        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        Toast.makeText(this, "Getting RSS feed", Toast.LENGTH_SHORT).show();
        Log.d(LOG, "Service started");
        PendingIntent pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        executorService.submit(new Loader(pendingIntent));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG, "Service destroied");
        Toast.makeText(this, "Служба остановлена",
                Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public class Loader implements Runnable{

        PendingIntent pendingIntent;

        public Loader(PendingIntent pendingIntent){
            this.pendingIntent = pendingIntent;
        }
        @Override
        public void run() {
            Log.d(LOG, "Thread runned");
            List<RSSNote> netWork = new Network().getRSSList("http://news.sportbox.ru/taxonomy/term/7212/0/feed");
            getContentResolver().delete(MainActivity.RSS_URI, null, null);
            for (RSSNote rss : netWork) {
                ContentValues values = new ContentValues();
                values.put(RSSProvider.RSS_TITLE, rss.getTitle());
                values.put(RSSProvider.RSS_DESCRIPTION, rss.getDescription());
                values.put(RSSProvider.RSS_LINK, rss.getLink());
                Uri uri = getContentResolver().insert(RSSProvider.RSS_CONTENT_URI, values);
                Log.d(LOG, uri.toString());
            }
            try {

                pendingIntent.send(RSSService.this, MainActivity.RESULT_OK, new Intent(RSSService.this, RSSService.class));
            } catch (PendingIntent.CanceledException ex) {

            }
        }
    }
}
