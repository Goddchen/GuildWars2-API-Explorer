package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.MapAdapter;
import de.goddchen.android.gw2.api.async.MapNamesLoader;
import de.goddchen.android.gw2.api.data.MapName;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MapFragment extends ListFragment {

    private LoaderManager.LoaderCallbacks<List<MapName>> mMapNamesLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<MapName>>() {
                @Override
                public Loader<List<MapName>> onCreateLoader(int i, Bundle bundle) {
                    return new MapNamesLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<MapName>> listLoader, List<MapName> mapNames) {
                    if (mapNames != null) {
                        setListAdapter(new MapAdapter(getActivity(), mapNames));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<MapName>> listLoader) {

                }
            };

    public static MapFragment newInstance(Integer worldId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(Application.Extras.WORLD, worldId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.MAP_NAMES, null, mMapNamesLoaderCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MapName mapName = (MapName) getListAdapter().getItem(position);
        int worldId = getArguments().getInt(Application.Extras.WORLD);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, EventsFragment.newInstance(worldId, mapName))
                .addToBackStack("event")
                .commit();
    }
}
