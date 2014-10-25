package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.World;

/**
 * Created by Goddchen on 22.05.13.
 */
public class WorldsLoader extends FixedAsyncTaskLoader<List<World>> {
    public WorldsLoader(Context context) {
        super(context);
    }

    @Override
    public List<World> loadInBackground() {
        try {
            List<World> worlds = Application.getDatabaseHelper().getWorldDao().queryForAll();
            if (worlds == null || worlds.isEmpty()) {
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v2/worlds?ids=all&lang="
                                + Locale.getDefault().getLanguage())
                                .openConnection();
                worlds = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                        new TypeToken<List<World>>() {
                        }.getType());
                for (World world : worlds) {
                    Application.getDatabaseHelper().getWorldDao().create(world);
                }
            }
            Collections.sort(worlds, new Comparator<World>() {
                @Override
                public int compare(World world, World world2) {
                    return world.name.compareTo(world2.name);
                }
            });
            return worlds;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading worlds", e);
            return null;
        }
    }
}
