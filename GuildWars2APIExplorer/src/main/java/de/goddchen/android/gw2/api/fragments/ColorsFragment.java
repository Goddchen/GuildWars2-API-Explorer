package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.table.TableUtils;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.ColorAdapter;
import de.goddchen.android.gw2.api.async.ColorsLoader;
import de.goddchen.android.gw2.api.data.Color;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ColorsFragment extends ListFragment {

    private LoaderManager.LoaderCallbacks<List<Color>> mColorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Color>>() {
                @Override
                public Loader<List<Color>> onCreateLoader(int i, Bundle bundle) {
                    return new ColorsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Color>> listLoader, List<Color> colors) {
                    setListShown(true);
                    if (colors != null) {
                        setListAdapter(new ColorAdapter(getActivity(), colors));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Color>> listLoader) {

                }
            };

    public static ColorsFragment newInstance() {
        ColorsFragment fragment = new ColorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_colors, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            try {
                TableUtils.clearTable(Application.getDatabaseHelper().getConnectionSource(),
                        Color.Config.class);
                TableUtils.clearTable(Application.getDatabaseHelper().getConnectionSource(),
                        Color.class);
                setListShown(false);
                getLoaderManager()
                        .restartLoader(Application.Loaders.COLORS, null, mColorLoaderCallbacks);
            } catch (Exception e) {
                Log.e(Application.Constants.LOG_TAG, "Error refreshing colors", e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.COLORS, null, mColorLoaderCallbacks);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setFastScrollEnabled(true);
    }
}
