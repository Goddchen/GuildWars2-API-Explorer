package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.RecipesLoader;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipesFragment extends SherlockListFragment {

    public static RecipesFragment newInstance() {
        RecipesFragment fragment = new RecipesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showList(false);
        getLoaderManager().initLoader(Application.Loaders.RECIPES, null, mRecipesLoaderCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Recipe recipe = (Recipe) l.getItemAtPosition(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, RecipeFragment.newInstance(recipe.recipe_id))
                .addToBackStack("recipe")
                .commit();
    }

    private void showList(boolean shown) {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.progress_content).setVisibility(shown ? View.GONE : View
                    .VISIBLE);
            view.findViewById(R.id.list_content).setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    private LoaderManager.LoaderCallbacks<List<Recipe>> mRecipesLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Recipe>>() {
                @Override
                public Loader<List<Recipe>> onCreateLoader(int i, Bundle bundle) {
                    return new RecipesLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Recipe>> listLoader, List<Recipe> recipes) {
                    showList(true);
                    if (recipes != null) {
                        setListAdapter(new ArrayAdapter<Recipe>(getActivity(),
                                android.R.layout.simple_list_item_1, recipes) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                Item outputItem = getItem(position).outputItem;
                                ((TextView) view.findViewById(android.R.id.text1))
                                        .setText(outputItem == null ? "???" : outputItem.name);
                                return view;
                            }
                        });
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Recipe>> listLoader) {

                }
            };
}
