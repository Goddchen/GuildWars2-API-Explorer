package de.goddchen.android.gw2.api.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    private SharedPreferences mSharedPreferences;

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
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startDownload();
            }
        }).start();
        return START_STICKY;
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
                mItemDao = Application.getDatabaseHelper().getItemDao();
                if (mSharedPreferences.contains(Application.Preferences.ITEM_SYNC_IDS)) {
                    mIDs = new Gson().fromJson(mSharedPreferences.getString(Application.Preferences.ITEM_SYNC_IDS, "[]"),
                            new TypeToken<List<Integer>>() {
                            }.getType());
                    mProgress = mSharedPreferences.getInt(Application.Preferences.ITEM_SYNC_POSITION, 0);
                    for (int i = mProgress - 10; i < mIDs.size(); i++) {
                        mExecutorService.submit(new DownloadItemRunnable(mIDs.get(i)));
                    }
                } else {
                    HttpsURLConnection connection =
                            (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/items.json")
                                    .openConnection();
                    APIResponse apiResponse = new Gson()
                            .fromJson(new InputStreamReader(connection.getInputStream()),
                                    APIResponse.class);
                    mIDs = apiResponse.items;
                    mSharedPreferences.edit()
                            .putString(Application.Preferences.ITEM_SYNC_IDS, new Gson().toJson(mIDs))
                            .putInt(Application.Preferences.ITEM_SYNC_POSITION, 0)
                            .commit();
                    for (int i = 0; i < mIDs.size(); i++) {
                        mExecutorService.submit(new DownloadItemRunnable(mIDs.get(i)));
                    }
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
        mProgress++;
        mSharedPreferences.edit().putInt(Application.Preferences.ITEM_SYNC_POSITION, mProgress).commit();
        if (mProgress == mIDs.size()) {
            halt();
        }
    }

    private synchronized void itemDownloadFailed(int id) {
        mProgress++;
        mSharedPreferences.edit().putInt(Application.Preferences.ITEM_SYNC_POSITION, mProgress).commit();
        if (mProgress == mIDs.size()) {
            halt();
        }
    }

    private void halt() {
        stopForeground(true);
        mSharedPreferences.edit()
                .putLong(Application.Preferences.LAST_ITEM_SYNC_BUILD, mCurrentBuild.build_id)
                .remove(Application.Preferences.ITEM_SYNC_POSITION)
                .remove(Application.Preferences.ITEM_SYNC_IDS)
                .commit();
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

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningService :
                activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ItemSyncService.class.getName().equals(runningService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
