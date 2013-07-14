package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 14.07.13.
 */
public class RecipesLoader extends FixedAsyncTaskLoader<List<Recipe>> {
    public RecipesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Recipe> loadInBackground() {
        try {
            List<Recipe> recipes = Application.getDatabaseHelper().getRecipeDao().queryForAll();
            Collections.sort(recipes, new Comparator<Recipe>() {
                @Override
                public int compare(Recipe recipe, Recipe recipe2) {
                    if (recipe.outputItem == null && recipe2.outputItem == null) {
                        return 0;
                    } else if (recipe.outputItem == null) {
                        return 1;
                    } else if (recipe2.outputItem == null) {
                        return -1;
                    } else {
                        return recipe.outputItem.name.compareTo(recipe2.outputItem.name);
                    }
                }
            });
            return recipes;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading recipes", e);
            return null;
        }
    }
}
