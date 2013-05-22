package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.MatchMapPagerAdapter;
import de.goddchen.android.gw2.api.async.MatchDetailsLoader;
import de.goddchen.android.gw2.api.data.Match;
import de.goddchen.android.gw2.api.data.MatchDetails;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchDetailsFragment extends SherlockFragment {

    private Handler mHandler;

    public static MatchDetailsFragment newInstance(Match match) {
        MatchDetailsFragment fragment = new MatchDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Application.Extras.MATCH, match);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        view.findViewById(R.id.content).setVisibility(View.GONE);
        getLoaderManager().restartLoader(Application.Loaders.MATCH_DETAILS, null, mMatcheDetailsLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_match_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getView().findViewById(R.id.loading).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.content).setVisibility(View.GONE);
            getLoaderManager().restartLoader(Application.Loaders.MATCH_DETAILS, null, mMatcheDetailsLoaderCallbacks);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<MatchDetails> mMatcheDetailsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<MatchDetails>() {
                @Override
                public Loader<MatchDetails> onCreateLoader(int i, Bundle bundle) {
                    return new MatchDetailsLoader(getActivity(),
                            (Match) getArguments().getSerializable(Application.Extras.MATCH));
                }

                @Override
                public void onLoadFinished(Loader<MatchDetails> listLoader, MatchDetails matchDetails) {
                    getView().findViewById(R.id.loading).setVisibility(View.GONE);
                    getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    if (matchDetails != null) {
                        ((TextView) getView().findViewById(R.id.score_1)).setText("" + matchDetails.scores[0]);
                        ((TextView) getView().findViewById(R.id.score_2)).setText("" + matchDetails.scores[1]);
                        ((TextView) getView().findViewById(R.id.score_3)).setText("" + matchDetails.scores[2]);
                        getView().findViewById(R.id.score_1)
                                .setBackgroundColor(getResources().getColor(R.color.score_red));
                        getView().findViewById(R.id.score_2)
                                .setBackgroundColor(getResources().getColor(R.color.score_blue));
                        getView().findViewById(R.id.score_3)
                                .setBackgroundColor(getResources().getColor(R.color.score_green));
                        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.maps);
                        viewPager.setOffscreenPageLimit(10);
                        viewPager.setAdapter(new MatchMapPagerAdapter(getFragmentManager(), matchDetails));
                        PageIndicator pageIndicator = (PageIndicator) getView().findViewById(R.id.page_indicators);
                        pageIndicator.setViewPager(viewPager);
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.toast_error_getting_match_details,
                                        Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    }
                }

                @Override
                public void onLoaderReset(Loader<MatchDetails> listLoader) {

                }
            };
}
