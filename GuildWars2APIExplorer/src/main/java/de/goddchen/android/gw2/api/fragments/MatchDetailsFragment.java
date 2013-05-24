package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    private int mAutoRefresh;

    private Runnable mAuthRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            View view = getView();
            if (view != null) {
                view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                view.findViewById(R.id.content).setVisibility(View.GONE);
            }
            getLoaderManager().restartLoader(Application.Loaders.MATCH_DETAILS, null, mMatcheDetailsLoaderCallbacks);
            if (mAutoRefresh > 0) {
                mHandler.postDelayed(this, mAutoRefresh);
            }
        }
    };

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
        mAutoRefresh = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(Application.Preferences.WVWVW_REFRESH, "-1"));
        if (mAutoRefresh > 0) {
            mHandler.postDelayed(mAuthRefreshRunnable, mAutoRefresh);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mAuthRefreshRunnable);
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
        mHandler.post(mAuthRefreshRunnable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_match_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            mHandler.removeCallbacks(mAuthRefreshRunnable);
            mHandler.post(mAuthRefreshRunnable);
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
                        float scoreSum = matchDetails.scores[0] + matchDetails.scores[1] + matchDetails.scores[2];
                        Float score1Percent = matchDetails.scores[0] / scoreSum * 100;
                        Float score2Percent = matchDetails.scores[1] / scoreSum * 100;
                        Float score3Percent = matchDetails.scores[2] / scoreSum * 100;
                        ((LinearLayout.LayoutParams) getView().findViewById(R.id.score_1).getLayoutParams())
                                .weight = score1Percent;
                        ((LinearLayout.LayoutParams) getView().findViewById(R.id.score_2).getLayoutParams())
                                .weight = score2Percent;
                        ((LinearLayout.LayoutParams) getView().findViewById(R.id.score_3).getLayoutParams())
                                .weight = score3Percent;
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
