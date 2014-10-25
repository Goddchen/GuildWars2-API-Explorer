package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Quaggan;

/**
 * Created by Goddchen on 22.05.13.
 */
public class QuagganLoader extends FixedAsyncTaskLoader<List<Quaggan>> {
    public QuagganLoader(Context context) {
        super(context);
    }

    @Override
    public List<Quaggan> loadInBackground() {
        try {
            List<Quaggan> quaggans = Application.getDatabaseHelper().getDaoForClass(Quaggan.class).queryForAll();
            if (quaggans == null || quaggans.isEmpty()) {
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v2/quaggans?ids=all&lang="
                                + Locale.getDefault().getLanguage())
                                .openConnection();
                quaggans = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                        new TypeToken<List<Quaggan>>() {
                        }.getType());
                for (Quaggan quaggan : quaggans) {
                    Application.getDatabaseHelper().getDaoForClass(Quaggan.class).create(quaggan);
                }
            }
            return quaggans;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading quaggans", e);
            return null;
        }
    }
}
