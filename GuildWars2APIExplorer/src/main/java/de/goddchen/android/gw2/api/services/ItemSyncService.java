package de.goddchen.android.gw2.api.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Build;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 14.07.13.
 */
public class ItemSyncService extends Service {

    private ExecutorService mExecutorService;

    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder mNotificationBuilder;

    private List<Integer> mIDs;

    private int mProgress;

    private Build mCurrentBuild;

    private Dao<Item, Integer> mItemDao;

    private PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mExecutorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startDownload();
            }
        }).start();
        return START_NOT_STICKY;
    }

    private void startDownload() {
        try {
            mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "item-sync");
            mWakeLock.acquire(1000l * 60 * 30);
            mNotificationBuilder.setTicker("Syncing items");
            mNotificationBuilder.setOngoing(true);
            mNotificationBuilder.setContentTitle("Syncing items");
            mNotificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
            startForeground(Application.Notifications.ITEM_SYNC, mNotificationBuilder.build());
            mCurrentBuild = new Gson().fromJson(new InputStreamReader(
                    new URL("https://api.guildwars2.com/v1/build.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection().getInputStream()), Build.class);
            long lastSyncBuild = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Application.Preferences.LAST_ITEM_SYNC_BUILD, -1);
            if (mCurrentBuild.build_id != lastSyncBuild) {
                sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_STARTED));
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                mItemDao = Application.getDatabaseHelper().getItemDao();
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/items.json")
                                .openConnection();
                APIResponse apiResponse = new Gson()
                        .fromJson(new InputStreamReader(connection.getInputStream()),
                                APIResponse.class);
                mIDs = apiResponse.items;
                for (int i = 0; i < mIDs.size(); i++) {
                    while (connectivityManager.getActiveNetworkInfo() == null
                            || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                        Thread.sleep(1000 * 60);
                    }
                    mExecutorService.submit(new DownloadItemRunnable(mIDs.get(i)));
                }
            } else {
                halt();
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error syncing items", e);
            sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_FAILED));
            halt();
        }
    }

    private synchronized void itemDownloaded(Item item) {
        mNotificationBuilder.setProgress(mIDs.size(), mProgress, false);
        mNotificationBuilder.setContentText(getString(R.string.format_progress_int,
                mProgress, mIDs.size()));
        mNotificationBuilder.setContentInfo(item.name.length() < 20 ?
                item.name :
                item.name.substring(0, 20) + "...");
        mNotificationManager.notify(Application.Notifications.ITEM_SYNC,
                mNotificationBuilder.build());
        if (++mProgress == mIDs.size()) {
            halt();
        }
    }

    private synchronized void itemDownloadFailed(int id) {
        if (++mProgress == mIDs.size()) {
            halt();
        }
    }

    private void halt() {
        stopForeground(true);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putLong(Application.Preferences.LAST_ITEM_SYNC_BUILD,
                mCurrentBuild.build_id).commit();
        sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_FINISHED));
        mExecutorService.shutdown();
        mWakeLock.release();
        stopSelf();
    }

    private class DownloadItemRunnable implements Runnable {

        private int mID;

        private DownloadItemRunnable(int id) {
            mID = id;
        }

        @Override
        public void run() {
            try {
                Item item = DatabaseHelper.getItem(mID);
                mItemDao.createOrUpdate(item);
                itemDownloaded(item);
            } catch (Exception e) {
                Log.e(Application.Constants.LOG_TAG, "Error loading item", e);
                itemDownloadFailed(mID);
            }
        }
    }

    private class APIResponse {
        List<Integer> items;
    }
}
