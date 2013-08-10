package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Item;
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
            final Dao<Recipe, Integer> recipeDao = Application.getDatabaseHelper().getRecipeDao();
            final Dao<Item, Integer> itemDao = Application.getDatabaseHelper().getItemDao();
            final List<Recipe> recipes = new ArrayList<Recipe>();
            recipes.addAll(recipeDao.queryForAll());
            for (Recipe recipe : recipes) {
                if (recipe.outputItem == null) {
                    recipe.outputItem = itemDao.queryForId(recipe.raw_output_item_id);
                }
            }
            recipeDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (Recipe recipe : recipes) {
                        if (recipe.outputItem == null) {
                            recipeDao.update(recipe);
                        }
                    }
                    return null;
                }
            });
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
