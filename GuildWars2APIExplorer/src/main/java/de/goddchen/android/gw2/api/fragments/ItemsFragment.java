package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.SherlockListFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.async.ItemIdsLoader;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemsFragment extends SherlockListFragment {

    public static ItemsFragment newInstance() {
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(Application.Loaders.ITEM_IDS, null, mItemIdsLoaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<List<Integer>> mItemIdsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Integer>>() {
                @Override
                public Loader<List<Integer>> onCreateLoader(int i, Bundle bundle) {
                    return new ItemIdsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Integer>> listLoader, List<Integer> integers) {
                    if (integers != null) {

                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Integer>> listLoader) {

                }
            };
}
