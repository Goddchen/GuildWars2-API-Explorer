package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Continent;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentLoader extends FixedAsyncTaskLoader<List<Continent>> {
    public ContinentLoader(Context context) {
        super(context);
    }

    @Override
    public List<Continent> loadInBackground() {
        try {
            List<Continent> continents = new ArrayList<Continent>();
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/continents.json?"
                            + "&lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            JSONObject jsonResponse =
                    new JSONObject(IOUtils.toString(connection.getInputStream()));
            Iterator<String> it = jsonResponse.getJSONObject("continents").keys();
            Gson gson = new Gson();
            while (it.hasNext()) {
                String key = it.next();
                Long id = Long.valueOf(key);
                Continent continent = gson.fromJson(jsonResponse.getJSONObject("continents")
                        .getJSONObject(key).toString(), Continent.class);
                continent.id = id;
                Application.getDatabaseHelper().getContinentDao().createOrUpdate(continent);
                continents.add(continent);
            }
            return continents;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading continents", e);
            return null;
        }
    }
}
