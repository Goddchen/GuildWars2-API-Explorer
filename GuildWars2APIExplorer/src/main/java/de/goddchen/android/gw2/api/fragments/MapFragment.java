package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.MapAdapter;
import de.goddchen.android.gw2.api.async.MapNamesLoader;
import de.goddchen.android.gw2.api.data.MapName;
import de.goddchen.android.gw2.api.data.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MapFragment extends SherlockListFragment {

    private ArrayList<MapName> mMapNames = new ArrayList<MapName>();

    public static MapFragment newInstance(World world) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(Application.Extras.WORLD, world);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(Application.Loaders.MAP_NAMES, null, mMapNamesLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new MapAdapter(getActivity(), mMapNames));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MapName mapName = (MapName) getListAdapter().getItem(position);
        World world = (World) getArguments().getSerializable(Application.Extras.WORLD);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, EventsFragment.newInstance(world, mapName))
                .addToBackStack("event")
                .commit();
    }

    private LoaderManager.LoaderCallbacks<List<MapName>> mMapNamesLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<MapName>>() {
                @Override
                public Loader<List<MapName>> onCreateLoader(int i, Bundle bundle) {
                    return new MapNamesLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<MapName>> listLoader, List<MapName> mapNames) {
                    if (mapNames != null) {
                        mMapNames.clear();
                        mMapNames.addAll(mapNames);
                        ((MapAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<MapName>> listLoader) {

                }
            };
}
