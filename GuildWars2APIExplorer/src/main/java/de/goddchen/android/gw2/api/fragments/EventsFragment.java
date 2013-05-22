package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import com.actionbarsherlock.app.SherlockListFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.adapter.EventAdapter;
import de.goddchen.android.gw2.api.async.EventsLoader;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.data.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventsFragment extends SherlockListFragment {

    public static final String EXTRA_WORLD = "world";

    private ArrayList<Event> mEvents = new ArrayList<Event>();

    public static EventsFragment newInstance(World world) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_WORLD, world);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(Application.Loaders.EVENTS, null, mEventsLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new EventAdapter(getActivity(), mEvents));
    }

    private LoaderManager.LoaderCallbacks<List<Event>> mEventsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Event>>() {
                @Override
                public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
                    return new EventsLoader(getActivity(), (World) getArguments().getSerializable(EXTRA_WORLD));
                }

                @Override
                public void onLoadFinished(Loader<List<Event>> listLoader, List<Event> events) {
                    if (events != null) {
                        mEvents.clear();
                        mEvents.addAll(events);
                        ((EventAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Event>> listLoader) {

                }
            };
}
