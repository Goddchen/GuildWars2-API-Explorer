package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 10.08.13.
 */
public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private List<Recipe> mItems, mFilteredItems;

    public RecipeAdapter(Context context, List<Recipe> objects) {
        super(context, android.R.layout.simple_list_item_1);
        mItems = objects;
        mFilteredItems = objects;
    }

    @Override
    public Recipe getItem(int position) {
        return mFilteredItems.get(position);
    }

    @Override
    public int getCount() {
        return mFilteredItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mFilteredItems.get(position).raw_output_item_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Recipe recipe = getItem(position);
        View view = super.getView(position, convertView, parent);
        ((TextView) view.findViewById(android.R.id.text1))
                .setText(recipe.outputItem == null ? ("??? (#" + recipe.recipe_id + ")") : recipe.outputItem.name);
        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Recipe> results = new ArrayList<Recipe>();
                for (Recipe recipe : mItems) {
                    if (recipe.outputItem != null && recipe.outputItem.name.contains(charSequence)) {
                        results.add(recipe);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = results.size();
                filterResults.values = results;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredItems = (List<Recipe>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
