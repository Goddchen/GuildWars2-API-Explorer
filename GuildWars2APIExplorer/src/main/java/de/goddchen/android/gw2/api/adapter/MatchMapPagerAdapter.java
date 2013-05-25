package de.goddchen.android.gw2.api.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.goddchen.android.gw2.api.data.MatchDetails;
import de.goddchen.android.gw2.api.fragments.MatchMapFragment;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchMapPagerAdapter extends FragmentStatePagerAdapter {
    private MatchDetails mMatchDetails;

    public MatchMapPagerAdapter(FragmentManager fm, MatchDetails matchDetails) {
        super(fm);
        mMatchDetails = matchDetails;
    }

    @Override
    public Fragment getItem(int i) {
        return MatchMapFragment.newInstance(mMatchDetails.maps.get(i));
    }

    @Override
    public int getCount() {
        return 4;
    }
}
