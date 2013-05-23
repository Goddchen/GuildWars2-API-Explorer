package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Recipe;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipeLoader extends FixedAsyncTaskLoader<Recipe> {
    private int mId;

    public RecipeLoader(Context context, int id) {
        super(context);
        mId = id;
    }

    @Override
    public Recipe loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/recipe_details.json?recipe_id=" + mId
                            + "&lang=" + Locale.getDefault().getLanguage()).openConnection();
            Recipe recipe = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), Recipe.class);
            recipe.outputItem = DatabaseHelper.getItem(recipe.output_item_id);
            for (Recipe.Ingredient ingredient : recipe.ingredients) {
                ingredient.item = DatabaseHelper.getItem(ingredient.item_id);
            }
            return recipe;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading recipe details", e);
            return null;
        }
    }
}
