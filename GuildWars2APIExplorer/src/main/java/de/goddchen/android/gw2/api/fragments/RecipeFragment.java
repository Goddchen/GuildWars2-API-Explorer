package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.adapter.IngredientAdapter;
import de.goddchen.android.gw2.api.async.GsonRequest;
import de.goddchen.android.gw2.api.async.RecipeLoader;
import de.goddchen.android.gw2.api.data.APIRecipe;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.data.Recipe;

/**
 * Created by Goddchen on 23.05.13.
 */
public class RecipeFragment extends SherlockFragment {

    private Handler mHandler;

    private Recipe mRecipe;

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
        try {
            mRecipe = Application.getDatabaseHelper().getRecipeDao().queryForId(
                    getArguments().getInt(Application.Extras.RECIPE_ID)
            );
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading recipe", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mRecipe == null) {
            view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            view.findViewById(R.id.content).setVisibility(View.GONE);
            getLoaderManager().initLoader(Application.Loaders.RECIPE_DETAILS, null,
                    mRecipeLoaderCallbacks);
        } else {
            view.findViewById(R.id.loading).setVisibility(View.GONE);
            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
            updateView();
        }
    }

    private void updateView() {
        ((TextView) getView().findViewById(R.id.type))
                .setText(getString(R.string.recipe_type, mRecipe.type));
        loadItem((TextView) getView().findViewById(R.id.output_item),
                mRecipe, getString(R.string.recipe_output_item));
        ((TextView) getView().findViewById(R.id.output_item_count))
                .setText(getString(R.string.recipe_output_item_count,
                        mRecipe.output_item_count));
        ((TextView) getView().findViewById(R.id.min_rating))
                .setText(getString(R.string.recipe_min_rating, mRecipe.min_rating));
        ((TextView) getView().findViewById(R.id.time_to_craft))
                .setText(getString(R.string.recipe_time_to_craft,
                        mRecipe.time_to_craft_ms));
        ((TextView) getView().findViewById(R.id.disciplines))
                .setText(getString(R.string.recipe_disciplines,
                        Arrays.toString(mRecipe.disciplines)));
        ((TextView) getView().findViewById(R.id.flags))
                .setText(getString(R.string.recipe_flags,
                        Arrays.toString(mRecipe.flags)));

        ListView ingredients = (ListView) getView().findViewById(R.id.ingredients);
        ingredients.setAdapter(new IngredientAdapter(getActivity(),
                new ArrayList<Recipe.Ingredient>(mRecipe.ingredients)));
        ingredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Recipe.Ingredient ingredient = (Recipe.Ingredient) parent
                        .getItemAtPosition(position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment,
                                ItemFragment.newInstance(ingredient.raw_item_id))
                        .addToBackStack("item-details")
                        .commit();
            }
        });
    }

    private void loadItem(final TextView view, Recipe recipe, final String format) {
        view.setText("Loading...");
        ((BaseFragmentActivity) getActivity()).getRequestQueue()
                .add(new GsonRequest<Item>(
                        "https://api.guildwars2.com/v1/item_details.json?item_id=" + recipe.raw_output_item_id
                                + "&lang=" + Locale.getDefault().getLanguage(),
                        Item.class,
                        new Response.Listener<Item>() {
                            @Override
                            public void onResponse(Item item) {
                                view.setText(String.format(format, item.name));
                                if (TextUtils.isEmpty(item.icon_file_id)
                                        || TextUtils.isEmpty(item.icon_file_signature)) {
                                    getView().findViewById(R.id.icon).setVisibility(View.GONE);
                                } else {
                                    NetworkImageView iconView = (NetworkImageView) getView().findViewById(R.id.icon);
                                    iconView.setDefaultImageResId(android.R.drawable.ic_menu_gallery);
                                    iconView.setImageUrl(String.format("https://render.guildwars2.com/file/%s/%s.png",
                                            item.icon_file_signature, item.icon_file_id),
                                            new ImageLoader(((BaseFragmentActivity) getActivity()).getRequestQueue(),
                                                    ((BaseFragmentActivity) getActivity()).getImageCache()));
                                }
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

    private LoaderManager.LoaderCallbacks<APIRecipe> mRecipeLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<APIRecipe>() {
                @Override
                public Loader<APIRecipe> onCreateLoader(int i, Bundle bundle) {
                    return new RecipeLoader(getActivity(), getArguments().getInt(Application
                            .Extras.RECIPE_ID));
                }

                @Override
                public void onLoadFinished(Loader<APIRecipe> itemLoader, APIRecipe recipe) {
                    getView().findViewById(R.id.loading).setVisibility(View.GONE);
                    getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    if (recipe == null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        R.string.toast_error_getting_recipe_details,
                                        Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    } else {
                        try {
                            mRecipe = new Recipe(recipe);
                            updateView();
                        } catch (Exception e) {
                            Log.e(Application.Constants.LOG_TAG, "Error loading recipe", e);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<APIRecipe> itemLoader) {

                }
            };
}
