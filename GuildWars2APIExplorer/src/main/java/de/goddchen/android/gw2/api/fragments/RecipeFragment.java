package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.IngredientAdapter;
import de.goddchen.android.gw2.api.async.RecipeLoader;
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
                                .setText(String.format("Type: %s", recipe.type));
                        ((TextView) getView().findViewById(R.id.output_item))
                                .setText(String.format("Output item: %s",
                                        recipe.outputItem == null || TextUtils.isEmpty(recipe.outputItem.name) ? "---"
                                                : recipe.outputItem.name));
                        ((TextView) getView().findViewById(R.id.output_item_count))
                                .setText(String.format("Output item count: %d", recipe.output_item_count));
                        ((TextView) getView().findViewById(R.id.min_rating))
                                .setText(String.format("Min. rating: %d", recipe.min_rating));
                        ((TextView) getView().findViewById(R.id.time_to_craft))
                                .setText(String.format("Time to craft (ms): %d", recipe.time_to_craft_ms));
                        ListView ingredients = (ListView) getView().findViewById(R.id.ingredients);
                        ingredients.setAdapter(new IngredientAdapter(getActivity(), recipe.ingredients));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Recipe> itemLoader) {

                }
            };
}
