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
import de.goddchen.android.gw2.api.data.APIRecipe;
import de.goddchen.android.gw2.api.data.Build;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 14.07.13.
 */
public class RecipeSyncService extends IntentService {
    public RecipeSyncService() {
        super(RecipeSyncService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Build currentBuild = new Gson().fromJson(new InputStreamReader(
                    new URL("https://api.guildwars2.com/v1/build.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection().getInputStream()), Build.class);
            long lastSyncBuild = PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(Application.Preferences.LAST_RECIPE_SYNC_BUILD, -1);
            if (currentBuild.build_id != lastSyncBuild) {
                sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_STARTED));
                NotificationManager notificationManager = (NotificationManager) getSystemService
                        (Context.NOTIFICATION_SERVICE);
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder
                        (this);
                notificationBuilder.setTicker("Syncing recipes");
                notificationBuilder.setOngoing(true);
                notificationBuilder.setContentTitle("Syncing recipes");
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_notification);
                notificationManager.notify(Application.Notifications.RECIPE_SYNC,
                        notificationBuilder.build());
                Dao<Recipe, Integer> recipeDao = Application.getDatabaseHelper().getRecipeDao();
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/recipes" +
                                ".json").openConnection();
                APIResponse apiResponse =
                        new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                                APIResponse.class);
                List<Integer> ids = apiResponse.recipes;
                for (int i = 0; i < ids.size(); i++) {
                    try {
                        notificationBuilder.setProgress(ids.size(), i, false);
                        notificationBuilder.setContentText(getString(R.string.format_progress_int,
                                i, ids.size()));
                        notificationManager.notify(Application.Notifications.RECIPE_SYNC,
                                notificationBuilder.build());
                        while (connectivityManager.getActiveNetworkInfo() == null
                                || !connectivityManager.getActiveNetworkInfo().isConnected()) {
                            Thread.sleep(1000 * 60);
                        }
                        int id = ids.get(i);
                        HttpsURLConnection recipeConnection =
                                (HttpsURLConnection) new URL("https://api.guildwars2" +
                                        ".com/v1/recipe_details.json?recipe_id=" + id
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
                        recipeDao.createOrUpdate(recipe);
                        for (APIRecipe.Ingredient apiIngredient : apiRecipe.ingredients) {
                            Recipe.Ingredient ingredient = new Recipe.Ingredient();
                            ingredient.count = apiIngredient.count;
                            ingredient.item = Application.getDatabaseHelper().getItemDao()
                                    .queryForId(apiIngredient.item_id);
                            ingredient.recipe = recipe;
                            Application.getDatabaseHelper().getDao(Recipe.Ingredient.class)
                                    .createOrUpdate(ingredient);
                        }
                    } catch (Exception e) {
                        Log.e(Application.Constants.LOG_TAG, "Error loading recipe", e);
                    }
                }
                notificationManager.cancel(Application.Notifications.RECIPE_SYNC);
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putLong(Application.Preferences.LAST_RECIPE_SYNC_BUILD,
                        currentBuild.build_id).commit();
                sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_FINISHED));
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error syncing recipes", e);
            sendBroadcast(new Intent(Application.Actions.RECIPE_SYNC_FAILED));
        }
    }

    private class APIResponse {
        public List<Integer> recipes;
    }
}
