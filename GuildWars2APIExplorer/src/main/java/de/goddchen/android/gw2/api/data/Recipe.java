package de.goddchen.android.gw2.api.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Goddchen on 23.05.13.
 */
public class Recipe implements Serializable {
    public int recipe_id;

    public String type;

    public int output_item_id;

    public int output_item_count;

    public int min_rating;

    public int time_to_craft_ms;

    public String[] disciplines;

    public String[] flags;

    public List<Ingredient> ingredients;

    public class Ingredient implements Serializable {
        public int item_id;
        public int count;
    }
}
