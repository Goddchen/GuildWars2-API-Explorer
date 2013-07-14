package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import de.goddchen.android.gw2.api.Application;

/**
 * Created by Goddchen on 23.05.13.
 */
@DatabaseTable
public class Recipe implements Serializable {
    @DatabaseField(id = true)
    public int recipe_id;

    @DatabaseField
    public String type;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public Item outputItem;

    @DatabaseField
    public int output_item_count;

    @DatabaseField
    public int min_rating;

    @DatabaseField
    public int time_to_craft_ms;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] disciplines;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public String[] flags;

    @ForeignCollectionField
    public ForeignCollection<Ingredient> ingredients;

    @DatabaseTable
    public static class Ingredient implements Serializable {
        @DatabaseField(generatedId = true)
        private int id;

        @DatabaseField(foreign = true, foreignAutoRefresh = true)
        public Recipe recipe;

        @DatabaseField(foreign = true, foreignAutoRefresh = true)
        public Item item;

        @DatabaseField
        public int count;
    }

    public Recipe() {
    }

    public Recipe(APIRecipe apiRecipe) throws Exception {
        disciplines = apiRecipe.disciplines;
        flags = apiRecipe.flags;
        min_rating = apiRecipe.min_rating;
        output_item_count = apiRecipe.output_item_count;
        recipe_id = apiRecipe.recipe_id;
        time_to_craft_ms = apiRecipe.time_to_craft_ms;
        type = apiRecipe.type;
        outputItem = Application.getDatabaseHelper().getItemDao()
                .queryForId(apiRecipe.output_item_id);
        Application.getDatabaseHelper().getRecipeDao().createOrUpdate(this);
        for (APIRecipe.Ingredient apiIngredient : apiRecipe.ingredients) {
            Recipe.Ingredient ingredient = new Recipe.Ingredient();
            ingredient.count = apiIngredient.count;
            ingredient.item = Application.getDatabaseHelper().getItemDao()
                    .queryForId(apiIngredient.item_id);
            ingredient.recipe = this;
            Application.getDatabaseHelper().getDao(Recipe.Ingredient.class)
                    .createOrUpdate(ingredient);
        }
    }
}
