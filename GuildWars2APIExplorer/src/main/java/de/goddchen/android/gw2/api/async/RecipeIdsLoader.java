package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipeIdsLoader extends FixedAsyncTaskLoader<List<Integer>> {
    public RecipeIdsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Integer> loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/recipes.json").openConnection();
            List<Integer> ids =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()), Response.class).recipes;
            Collections.sort(ids, new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer integer2) {
                    return integer.compareTo(integer2);
                }
            });
            return ids;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading recipe ids", e);
            return null;
        }
    }

    private class Response {
        List<Integer> recipes;
    }
}
