package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.APIRecipe;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipeLoader extends FixedAsyncTaskLoader<APIRecipe> {
    private int mId;

    public RecipeLoader(Context context, int id) {
        super(context);
        mId = id;
    }

    @Override
    public APIRecipe loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/recipe_details" +
                            ".json?recipe_id=" + mId
                            + "&lang=" + Locale.getDefault().getLanguage()).openConnection();
            APIRecipe recipe = new Gson().fromJson(new InputStreamReader(connection
                    .getInputStream()),
                    APIRecipe.class);
            return recipe;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading recipe details", e);
            return null;
        }
    }
}
