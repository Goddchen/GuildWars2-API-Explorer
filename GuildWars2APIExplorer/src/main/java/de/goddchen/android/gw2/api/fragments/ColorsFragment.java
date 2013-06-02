package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.adapter.ColorAdapter;
import de.goddchen.android.gw2.api.async.ColorsLoader;
import de.goddchen.android.gw2.api.data.Color;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ColorsFragment extends SherlockListFragment {

    public static ColorsFragment newInstance() {
        ColorsFragment fragment = new ColorsFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.COLORS, null, mColorLoaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<List<Color>> mColorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Color>>() {
                @Override
                public Loader<List<Color>> onCreateLoader(int i, Bundle bundle) {
                    return new ColorsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Color>> listLoader, List<Color> colors) {
                    if (colors != null) {
                        setListAdapter(new ColorAdapter(getActivity(), colors));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Color>> listLoader) {

                }
            };
}
