package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.WorldAdapter;
import de.goddchen.android.gw2.api.async.WorldsDbLoader;
import de.goddchen.android.gw2.api.async.WorldsLoader;
import de.goddchen.android.gw2.api.data.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class WorldsFragment extends SherlockListFragment {

    private ArrayList<World> mWorlds;

    public static WorldsFragment newInstance() {
        WorldsFragment fragment = new WorldsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWorlds = (ArrayList<World>) savedInstanceState.getSerializable("worlds");
        } else {
            mWorlds = new ArrayList<World>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("worlds", mWorlds);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(Application.Loaders.WORLDS_DB, null, mWorldDbLoaderCallbacks);
        getLoaderManager().restartLoader(Application.Loaders.WORLDS_ONLINE, null, mWorldLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new WorldAdapter(getActivity(), mWorlds));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        World world = (World) getListAdapter().getItem(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, EventsFragment.newInstance(world))
                .addToBackStack("events")
                .commit();
    }

    private LoaderManager.LoaderCallbacks<List<World>> mWorldDbLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<World>>() {
                @Override
                public Loader<List<World>> onCreateLoader(int i, Bundle bundle) {
                    return new WorldsDbLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<World>> listLoader, List<World> worlds) {
                    if (worlds != null) {
                        mWorlds.clear();
                        mWorlds.addAll(worlds);
                        ((WorldAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<World>> listLoader) {

                }
            };

    private LoaderManager.LoaderCallbacks<List<World>> mWorldLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<World>>() {
                @Override
                public Loader<List<World>> onCreateLoader(int i, Bundle bundle) {
                    return new WorldsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<World>> listLoader, List<World> worlds) {
                    if (worlds != null) {
                        mWorlds.clear();
                        mWorlds.addAll(worlds);
                        ((WorldAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<World>> listLoader) {

                }
            };
}
