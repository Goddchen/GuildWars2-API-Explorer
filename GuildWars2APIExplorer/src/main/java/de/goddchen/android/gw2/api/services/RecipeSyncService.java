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
import de.goddchen.android.gw2.api.data.APIRecipe;
import de.goddchen.android.gw2.api.data.Build;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 14.07.13.
 */
public class RecipeSyncService extends Service {

    private ExecutorService mExecutorService;

    private NotificationManager mNotificationManager;

    private NotificationCompat.Builder mNotificationBuilder;

    private List<Integer> mIDs;

    private Dao<Recipe, Integer> mRecipeDao;

    private int mProgress;

    private Build mCurrentBuild;

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newFixedThreadPool(10);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void recipeDownloaded(Recipe recipe) {
        if (++mProgress == mIDs.size()) {
            halt();
        }
        mNotificationBuilder.setProgress(mIDs.size(), mProgress, false);
        mNotificationBuilder.setContentText(getString(R.string.format_progress_int,
                mProgress, mIDs.size()));
        mNotificationManager.notify(Application.Notifications.RECIPE_SYNC,
                mNotificationBuilder.build());
    }

    private synchronized void recipeDownloadFailed(int id) {
        if (++mProgress == mIDs.size()) {
            halt();
        }
    }

    private void halt() {
        mNotificationManager.cancel(Application.Notifications.RECIPE_SYNC);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putLong(Application.Preferences.LAST_RECIPE_SYNC_BUILD,
                mCurrentBuild.build_id).commit();
        sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_FINISHED));
        mExecutorService.shutdown();
        mWakeLock.release();
        stopSelf();
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
            mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "recipe-sync");
            mWakeLock.acquire(1000l * 60 * 30);
            mCurrentBuild = new Gson().fromJson(new InputStreamReader(
                    new URL("https://api.guildwars2.com/v1/build.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection().getInputStream()), Build.class);
            long lastSyncBuild = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Application.Preferences.LAST_RECIPE_SYNC_BUILD, -1);
            if (mCurrentBuild.build_id != lastSyncBuild) {
                sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_STARTED));
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                mNotificationBuilder.setTicker("Syncing recipes");
                mNotificationBuilder.setOngoing(true);
                mNotificationBuilder.setContentTitle("Syncing recipes");
                mNotificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
                mNotificationManager.notify(Application.Notifications.RECIPE_SYNC,
                        mNotificationBuilder.build());
                mRecipeDao = Application.getDatabaseHelper().getRecipeDao();
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/recipes" +
                                ".json").openConnection();
                APIResponse apiResponse =
                        new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                                APIResponse.class);
                mIDs = apiResponse.recipes;
                for (int i = 0; i < mIDs.size(); i++) {
                    while (connectivityManager.getActiveNetworkInfo() == null
                            || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                        Thread.sleep(1000 * 60);
                    }
                    mExecutorService.submit(new DownloadRecipeRunnable(mIDs.get(i)));
                }
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error syncing recipes", e);
            sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_FAILED));
        }
    }

    private class DownloadRecipeRunnable implements Runnable {

        private int mId;

        private DownloadRecipeRunnable(int id) {
            mId = id;
        }

        @Override
        public void run() {
            try {
                HttpsURLConnection recipeConnection =
                        (HttpsURLConnection) new URL("https://api.guildwars2" +
                                ".com/v1/recipe_details.json?recipe_id=" + mId
                                + "&lang=" + Locale.getDefault().getLanguage())
                                .openConnection();
                APIRecipe apiRecipe = new Gson().fromJson(new InputStreamReader
                        (recipeConnection
                                .getInputStream()), APIRecipe.class);
                Recipe recipe = new Recipe();
                recipe.disciplines = apiRecipe.disciplines;
                recipe.flags = apiRecipe.flags;
                recipe.min_rating = apiRecipe.min_rating;
                recipe.output_item_count = apiRecipe.output_item_count;
                recipe.recipe_id = apiRecipe.recipe_id;
                recipe.time_to_craft_ms = apiRecipe.time_to_craft_ms;
                recipe.type = apiRecipe.type;
                recipe.outputItem = Application.getDatabaseHelper().getItemDao()
                        .queryForId(apiRecipe.output_item_id);
                mRecipeDao.createOrUpdate(recipe);
                for (APIRecipe.Ingredient apiIngredient : apiRecipe.ingredients) {
                    Recipe.Ingredient ingredient = new Recipe.Ingredient();
                    ingredient.count = apiIngredient.count;
                    ingredient.item = Application.getDatabaseHelper().getItemDao()
                            .queryForId(apiIngredient.item_id);
                    ingredient.recipe = recipe;
                    Application.getDatabaseHelper().getDao(Recipe.Ingredient.class)
                            .createOrUpdate(ingredient);
                }
                recipeDownloaded(recipe);
            } catch (Exception e) {
                Log.e(Application.Constants.LOG_TAG, "Error downloading recipe", e);
                recipeDownloadFailed(mId);
            }
        }
    }

    private class APIResponse {
        public List<Integer> recipes;
    }
}
