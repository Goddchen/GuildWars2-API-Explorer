package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
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
    private Handler mHandler;

    private int mAutoRefresh;

    private int mListPosition;

    private Runnable mAuthRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            View view = getView();
            if (view != null) {
                setListShown(false);
            }
            getLoaderManager().restartLoader(Application.Loaders.EVENTS, null, mEventsLoaderCallbacks);
            if (mAutoRefresh > 0) {
                mHandler.postDelayed(this, mAutoRefresh);
            }
        }
    };

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
        mHandler = new Handler();
        if (savedInstanceState != null) {
            mListPosition = savedInstanceState.getInt("list-y", 0);
        }
        mAutoRefresh = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(Application.Preferences.EVENTS_REFRESH, "-1"));
        if (mAutoRefresh > 0) {
            mHandler.postDelayed(mAuthRefreshRunnable, mAutoRefresh);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("list-y", mListPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mAuthRefreshRunnable);
        mHandler = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.EVENTS, null, mEventsLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                mListPosition = absListView.getFirstVisiblePosition();
            }
        });
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
                    setListShown(true);
                    if (events != null) {
                        List<Event> mapEvents = new ArrayList<Event>();
                        MapName mapName = (MapName) getArguments().getSerializable(Application.Extras.MAP);
                        for (Event event : events) {
                            if (event.map_id == mapName.id) {
                                mapEvents.add(event);
                            }
                        }
                        setListAdapter(new EventAdapter(getActivity(), mapEvents));
                        if (mListPosition > 0) {
                            getListView().setSelection(mListPosition);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Event>> listLoader) {

                }
            };
}
