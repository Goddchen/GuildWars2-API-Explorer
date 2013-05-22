package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Match;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchesLoader extends FixedAsyncTaskLoader<List<Match>> {

    public MatchesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Match> loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/wvw/matches.json"
                            + "?lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            List<Match> matches = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    Response.class).wvw_matches;
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match match, Match match2) {
                    return match.wvw_match_id.compareTo(match2.wvw_match_id);
                }
            });
            return matches;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading matches", e);
            return null;
        }
    }

    private class Response {
        public List<Match> wvw_matches;
    }
}
