package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.EventAdapter;
import de.goddchen.android.gw2.api.async.EventsLoader;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.data.MapName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventsFragment extends SherlockListFragment {

    public static EventsFragment newInstance(Integer worldId, MapName mapName) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putInt(Application.Extras.WORLD, worldId);
        args.putSerializable(Application.Extras.MAP, mapName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.EVENTS, null, mEventsLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            if (!getLoaderManager().hasRunningLoaders()) {
                setListAdapter(null);
                setListShown(false);
                getLoaderManager().restartLoader(Application.Loaders.EVENTS, null, mEventsLoaderCallbacks);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<List<Event>> mEventsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Event>>() {
                @Override
                public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
                    return new EventsLoader(getActivity(), getArguments().getInt(Application.Extras.WORLD));
                }

                @Override
                public void onLoadFinished(Loader<List<Event>> listLoader, List<Event> events) {
                    if (events != null) {
                        List<Event> mapEvents = new ArrayList<Event>();
                        MapName mapName = (MapName) getArguments().getSerializable(Application.Extras.MAP);
                        for (Event event : events) {
                            if (event.map_id == mapName.id) {
                                mapEvents.add(event);
                            }
                        }
                        setListAdapter(new EventAdapter(getActivity(), mapEvents));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Event>> listLoader) {

                }
            };
}
