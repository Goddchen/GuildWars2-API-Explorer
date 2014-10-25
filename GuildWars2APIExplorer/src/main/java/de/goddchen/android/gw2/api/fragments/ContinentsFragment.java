package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.ContinentAdapter;
import de.goddchen.android.gw2.api.async.ContinentLoader;
import de.goddchen.android.gw2.api.data.Continent;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentsFragment extends ListFragment {

    private LoaderManager.LoaderCallbacks<List<Continent>> mContinentLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Continent>>() {
                @Override
                public Loader<List<Continent>> onCreateLoader(int i, Bundle bundle) {
                    return new ContinentLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Continent>> listLoader, List<Continent> continents) {
                    setListShown(true);
                    if (continents != null) {
                        setListAdapter(new ContinentAdapter(getActivity(), continents));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Continent>> listLoader) {

                }
            };

    public static ContinentsFragment newInstance() {
        ContinentsFragment fragment = new ContinentsFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListShown(false);
        getLoaderManager().initLoader(Application.Loaders.CONTINENTS, null,
                mContinentLoaderCallbacks);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            List<Continent> continents = Application.getDatabaseHelper().getContinentDao()
                    .queryForAll();
            if (continents != null && !continents.isEmpty()) {
                setListAdapter(new ContinentAdapter(getActivity(), continents));
                setListShown(true);
            }
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading continents from db", e);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Continent continent = (Continent) l.getItemAtPosition(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, ContinentFragment.newInstance(continent))
                .addToBackStack("continent")
                .commit();
    }
}
