package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Arrays;
import java.util.Locale;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.adapter.IngredientAdapter;
import de.goddchen.android.gw2.api.async.GsonRequest;
import de.goddchen.android.gw2.api.async.RecipeLoader;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 23.05.13.
 */
public class RecipeFragment extends SherlockFragment {

    private Handler mHandler;

    public static RecipeFragment newInstance(int id) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putInt(Application.Extras.RECIPE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        view.findViewById(R.id.content).setVisibility(View.GONE);
        getLoaderManager().initLoader(Application.Loaders.RECIPE_DETAILS, null, mRecipeLoaderCallbacks);
    }

    private void loadItem(final TextView view, Recipe recipe, final String format) {
        view.setText("Loading...");
        ((BaseFragmentActivity) getActivity()).getRequestQueue()
                .add(new GsonRequest<Item>(
                        "https://api.guildwars2.com/v1/item_details.json?item_id=" + recipe.output_item_id
                                + "&lang=" + Locale.getDefault().getLanguage(),
                        Item.class,
                        new Response.Listener<Item>() {
                            @Override
                            public void onResponse(Item item) {
                                view.setText(String.format(format, item.name));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                view.setText("---");
                            }
                        }
                ));
    }

    private LoaderManager.LoaderCallbacks<Recipe> mRecipeLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Recipe>() {
                @Override
                public Loader<Recipe> onCreateLoader(int i, Bundle bundle) {
                    return new RecipeLoader(getActivity(), getArguments().getInt(Application.Extras.RECIPE_ID));
                }

                @Override
                public void onLoadFinished(Loader<Recipe> itemLoader, Recipe recipe) {
                    getView().findViewById(R.id.loading).setVisibility(View.GONE);
                    getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    if (recipe == null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.toast_error_getting_recipe_details,
                                        Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    } else {
                        ((TextView) getView().findViewById(R.id.type))
                                .setText(getString(R.string.recipe_type, recipe.type));
                        loadItem((TextView) getView().findViewById(R.id.output_item),
                                recipe, getString(R.string.recipe_output_item));
                        ((TextView) getView().findViewById(R.id.output_item_count))
                                .setText(getString(R.string.recipe_output_item_count,
                                        recipe.output_item_count));
                        ((TextView) getView().findViewById(R.id.min_rating))
                                .setText(getString(R.string.recipe_min_rating, recipe.min_rating));
                        ((TextView) getView().findViewById(R.id.time_to_craft))
                                .setText(getString(R.string.recipe_time_to_craft,
                                        recipe.time_to_craft_ms));
                        ((TextView) getView().findViewById(R.id.disciplines))
                                .setText(getString(R.string.recipe_disciplines,
                                        Arrays.toString(recipe.disciplines)));
                        ((TextView) getView().findViewById(R.id.flags))
                                .setText(getString(R.string.recipe_flags,
                                        Arrays.toString(recipe.flags)));
                        ListView ingredients = (ListView) getView().findViewById(R.id.ingredients);
                        ingredients.setAdapter(new IngredientAdapter(getActivity(), recipe.ingredients));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Recipe> itemLoader) {

                }
            };
}
