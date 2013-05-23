package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;
import de.goddchen.android.gw2.api.R;

/**
 * Created by Goddchen on 22.05.13.
 */
public class RecipeSearchFragment extends SherlockFragment implements View.OnClickListener {

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
    public void onClick(View view) {
        if (view.getId() == R.id.search) {
            EditText id = (EditText) getView().findViewById(R.id.id);
            if (id.length() > 0) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment, RecipeFragment.newInstance(Integer.valueOf(id.getText().toString())))
                        .addToBackStack("recipe-details")
                        .commit();
            }
        }
    }
}
