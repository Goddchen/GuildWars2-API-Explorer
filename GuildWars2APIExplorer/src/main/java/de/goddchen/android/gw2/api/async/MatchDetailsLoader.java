package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Match;
import de.goddchen.android.gw2.api.data.MatchDetails;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchDetailsLoader extends FixedAsyncTaskLoader<MatchDetails> {

    private Match mMatch;

    public MatchDetailsLoader(Context context, Match match) {
        super(context);
        mMatch = match;
    }

    @Override
    public MatchDetails loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/wvw/match_details.json?match_id="
                            + mMatch.wvw_match_id
                            + "&lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            MatchDetails details = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    MatchDetails.class);
            for (MatchDetails.Map map : details.maps) {
                Collections.sort(map.objectives, new Comparator<MatchDetails.Objective>() {
                    @Override
                    public int compare(MatchDetails.Objective objective, MatchDetails.Objective objective2) {
                        return Integer.valueOf(objective.getOwnerPriority()).compareTo(objective2.getOwnerPriority());
                    }
                });
            }
            DatabaseHelper.loadObjectiveNames(details);
            DatabaseHelper.loadGuildNames(details);
            return details;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading match details", e);
            return null;
        }
    }
}
