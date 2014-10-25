package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.WorldAdapter;
import de.goddchen.android.gw2.api.async.WorldsLoader;
import de.goddchen.android.gw2.api.data.World;

/**
 * Created by Goddchen on 22.05.13.
 */
public class WorldsFragment extends ListFragment {

    private LoaderManager.LoaderCallbacks<List<World>> mWorldLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<World>>() {
                @Override
                public Loader<List<World>> onCreateLoader(int i, Bundle bundle) {
                    return new WorldsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<World>> listLoader, List<World> worlds) {
                    if (worlds != null) {
                        setListAdapter(new WorldAdapter(getActivity(), worlds));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<World>> listLoader) {

                }
            };

    public static WorldsFragment newInstance() {
        WorldsFragment fragment = new WorldsFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int homeWorld = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(Application.Preferences.HOME_WORLD, -1);
        if (homeWorld == -1) {
            getLoaderManager().initLoader(Application.Loaders.WORLDS, null, mWorldLoaderCallbacks);
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, MapFragment.newInstance(homeWorld))
                    .commit();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        World world = (World) getListAdapter().getItem(position);
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putInt(Application.Preferences.HOME_WORLD, world.id)
                .commit();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, MapFragment.newInstance(world.id))
                .addToBackStack("maps")
                .commit();
    }
}
