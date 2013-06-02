package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Build;

/**
 * Created by Goddchen on 22.05.13.
 */
public class BuildLoader extends FixedAsyncTaskLoader<Build> {
    public BuildLoader(Context context) {
        super(context);
    }

    @Override
    public Build loadInBackground() {
        try {
            return new Gson().fromJson(new InputStreamReader(
                    new URL("https://api.guildwars2.com/v1/build.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection().getInputStream()), Build.class);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading build", e);
            return null;
        }
    }
}
