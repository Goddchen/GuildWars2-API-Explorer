package de.goddchen.android.gw2.api.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Build;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 14.07.13.
 */
public class ItemSyncService extends IntentService {

    public ItemSyncService() {
        super(ItemSyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Build currentBuild = new Gson().fromJson(new InputStreamReader(
                    new URL("https://api.guildwars2.com/v1/build.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection().getInputStream()), Build.class);
            long lastSyncBuild = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Application.Preferences.LAST_ITEM_SYNC_BUILD, -1);
            if (currentBuild.build_id != lastSyncBuild) {
                sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_STARTED));
                NotificationManager notificationManager = (NotificationManager) getSystemService
                        (Context.NOTIFICATION_SERVICE);
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder
                        (this);
                notificationBuilder.setTicker("Syncing items");
                notificationBuilder.setOngoing(true);
                notificationBuilder.setContentTitle("Syncing items");
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
                notificationManager.notify(Application.Notifications.ITEM_SYNC,
                        notificationBuilder.build());
                Dao<Item, Integer> itemDao = Application.getDatabaseHelper().getItemDao();
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/items.json")
                                .openConnection();
                APIResponse apiResponse = new Gson()
                        .fromJson(new InputStreamReader(connection.getInputStream()),
                                APIResponse.class);
                List<Integer> ids = apiResponse.items;
                for (int i = 0; i < ids.size(); i++) {
                    try {
                        notificationBuilder.setProgress(ids.size(), i, false);
                        notificationBuilder.setContentText(getString(R.string.format_progress_int,
                                i, ids.size()));
                        notificationManager.notify(Application.Notifications.ITEM_SYNC,
                                notificationBuilder.build());
                        while (connectivityManager.getActiveNetworkInfo() == null
                                || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                            Thread.sleep(1000 * 60);
                        }
                        int id = ids.get(i);
                        Item item = DatabaseHelper.getItem(id);
                        itemDao.createOrUpdate(item);
                    } catch (Exception e) {
                        Log.e(Application.Constants.LOG_TAG, "Error loading item", e);
                    }
                }
                notificationManager.cancel(Application.Notifications.ITEM_SYNC);
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putLong(Application.Preferences.LAST_ITEM_SYNC_BUILD,
                        currentBuild.build_id).commit();
                sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_FINISHED));
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error syncing items", e);
            sendBroadcast(new Intent(Application.Actions.ITEMS_SYNC_FAILED));
        }
    }

    private class APIResponse {
        List<Integer> items;
    }
}
