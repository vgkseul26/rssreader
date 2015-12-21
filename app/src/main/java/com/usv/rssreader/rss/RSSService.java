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
    ExecutorService es;
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
        es = Executors.newFixedThreadPool(2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        Toast.makeText(this, "Getting RSS feed", Toast.LENGTH_SHORT).show();
        Log.d(LOG, "Service started");
       final  PendingIntent pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        es.submit(new Loader(pi));
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

        PendingIntent pi;

        public Loader(PendingIntent pi){
            this.pi = pi;
        }
        @Override
        public void run() {
            Log.d(LOG, "Thread runned");
            List<RSSNote> netWork = new Network().getRSSList("http://news.sportbox.ru/taxonomy/term/7212/0/feed");
            for (RSSNote rss : netWork) {
                ContentValues values = new ContentValues();
                values.put(RSSProvider.RSS_TITLE, rss.getTitle());
                values.put(RSSProvider.RSS_DESCRIPTION, rss.getDescription());
                values.put(RSSProvider.RSS_LINK, rss.getLink());
                Uri uri = getContentResolver().insert(RSSProvider.RSS_CONTENT_URI, values);
                Log.d(LOG, uri.toString());
            }
            try {
                pi.send(RSSService.this, MainActivity.INSERT_RSS, new Intent(RSSService.this, RSSService.class));
            } catch (PendingIntent.CanceledException ex) {

            }
        }
    }
}
