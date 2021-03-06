package de.goddchen.android.gw2.api.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.RecipeIdsLoader;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipeSearchFragment extends ListFragment implements View.OnClickListener {

    private LoaderManager.LoaderCallbacks<List<Integer>> mIdLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Integer>>() {
                @Override
                public Loader<List<Integer>> onCreateLoader(int i, Bundle bundle) {
                    return new RecipeIdsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Integer>> listLoader, List<Integer> integers) {
                    showList(true);
                    if (integers != null) {
                        setListAdapter(
                                new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_list_item_1, integers));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Integer>> listLoader) {

                }
            };

    public static RecipeSearchFragment newInstance() {
        RecipeSearchFragment fragment = new RecipeSearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.search).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showList(false);
        getLoaderManager().initLoader(Application.Loaders.RECIPE_IDS, null, mIdLoaderCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Integer recipeId = (Integer) getListAdapter().getItem(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, RecipeFragment.newInstance(recipeId))
                .addToBackStack("recipe")
                .commit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search) {
            EditText id = (EditText) getView().findViewById(R.id.id);
            String idString = id.getText().toString();
            if (id.length() > 0) {
                if (!TextUtils.isDigitsOnly(idString)) {
                    Toast.makeText(getActivity(), R.string.toast_only_digits,
                            Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getActivity()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment,
                                    RecipeFragment.newInstance(Integer.valueOf(idString)))
                            .addToBackStack("recipe-details")
                            .commit();
                }
            }
        }
    }

    private void showList(boolean shown) {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.progress_content).setVisibility(shown ? View.GONE : View.VISIBLE);
            view.findViewById(R.id.list_content).setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }
}
