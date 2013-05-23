package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.data.Recipe;

import java.util.List;

/**
 * Created by Goddchen on 23.05.13.
 */
public class IngredientAdapter extends ArrayAdapter<Recipe.Ingredient> {
    public IngredientAdapter(Context context, List<Recipe.Ingredient> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Recipe.Ingredient ingredient = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(String.format("%d x %s",
                ingredient.count,
                ingredient.item == null || TextUtils.isEmpty(ingredient.item.name) ? "---" : ingredient.item.name));
        return convertView;
    }
}
