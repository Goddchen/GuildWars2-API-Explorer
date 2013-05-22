package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.MatchAdapter;
import de.goddchen.android.gw2.api.async.MatchesLoader;
import de.goddchen.android.gw2.api.data.Match;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchesFragment extends SherlockListFragment {

    public static MatchesFragment newInstance() {
        MatchesFragment fragment = new MatchesFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(Application.Loaders.MATCHES, null, mMatchesLoaderCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Match match = (Match) getListAdapter().getItem(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, MatchDetailsFragment.newInstance(match))
                .addToBackStack("match")
                .commit();
    }

    private LoaderManager.LoaderCallbacks<List<Match>> mMatchesLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Match>>() {
                @Override
                public Loader<List<Match>> onCreateLoader(int i, Bundle bundle) {
                    return new MatchesLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Match>> listLoader, List<Match> matches) {
                    if (matches != null) {
                        setListAdapter(new MatchAdapter(getActivity(), matches));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Match>> listLoader) {

                }
            };
}
