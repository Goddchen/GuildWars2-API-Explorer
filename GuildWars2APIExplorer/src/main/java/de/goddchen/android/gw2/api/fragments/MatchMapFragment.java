package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.adapter.ObjectiveAdapter;
import de.goddchen.android.gw2.api.data.MatchDetails;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchMapFragment extends Fragment {

    private MatchDetails.Map mMap;

    public static MatchMapFragment newInstance(MatchDetails.Map map) {
        MatchMapFragment fragment = new MatchMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(Application.Extras.MATCH_MAP, map);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMap = (MatchDetails.Map) getArguments().getSerializable(Application.Extras.MATCH_MAP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.type)).setText(mMap.type);
        ((TextView) view.findViewById(R.id.score_1)).setText("" + mMap.scores[0]);
        ((TextView) view.findViewById(R.id.score_2)).setText("" + mMap.scores[1]);
        ((TextView) view.findViewById(R.id.score_3)).setText("" + mMap.scores[2]);
        float scoreSum = mMap.scores[0] + mMap.scores[1] + mMap.scores[2];
        Float score1Percent = mMap.scores[0] / scoreSum * 100;
        Float score2Percent = mMap.scores[1] / scoreSum * 100;
        Float score3Percent = mMap.scores[2] / scoreSum * 100;
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
        ListView objectives = (ListView) view.findViewById(R.id.objectives);
        objectives.setAdapter(new ObjectiveAdapter(getActivity(), mMap.objectives));
    }
}
