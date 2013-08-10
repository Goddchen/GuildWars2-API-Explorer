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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.RecipesLoader;
import de.goddchen.android.gw2.api.data.Recipe;
import de.goddchen.android.gw2.api.fragments.dialogs.ShouldSyncDialogFragment;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipesFragment extends SherlockListFragment {

    public static RecipesFragment newInstance() {
        RecipesFragment fragment = new RecipesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sync) {
            ShouldSyncDialogFragment.newInstance(ShouldSyncDialogFragment.TYPE_RECIPE_SYNC).show(getFragmentManager(), "should-sync");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Recipe recipe = getItem(position);
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view.findViewById(android.R.id.text1))
                                        .setText(recipe.outputItem == null ? ("??? (#" + recipe.recipe_id + ")") : recipe.outputItem.name);
                                return view;
                            }
                        });
                        if (recipes.size() == 0) {
                            ShouldSyncDialogFragment.newInstance(ShouldSyncDialogFragment.TYPE_RECIPE_SYNC)
                                    .show(getFragmentManager(), "should-sync");
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Recipe>> listLoader) {

                }
            };
}
