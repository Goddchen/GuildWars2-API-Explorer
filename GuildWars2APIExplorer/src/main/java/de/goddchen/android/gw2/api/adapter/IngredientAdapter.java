package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;
import java.util.Locale;

import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.async.GsonRequest;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 23.05.13.
 */
public class IngredientAdapter extends ArrayAdapter<Recipe.Ingredient> {
    public IngredientAdapter(Context context, List<Recipe.Ingredient> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Recipe.Ingredient ingredient = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        final TextView textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(de.goddchen.android.gw2.api.R.string.loading);
        ((BaseFragmentActivity) getContext()).getRequestQueue()
                .add(new GsonRequest<Item>(
                        "https://api.guildwars2.com/v1/item_details.json?item_id=" + ingredient.item_id
                                + "&lang=" + Locale.getDefault().getLanguage(),
                        Item.class,
                        new Response.Listener<Item>() {
                            @Override
                            public void onResponse(Item item) {
                                textView.setText(String.format("%d x %s",
                                        ingredient.count, item.name));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                textView.setText("---");
                            }
                        }
                ));
        return convertView;
    }
}
