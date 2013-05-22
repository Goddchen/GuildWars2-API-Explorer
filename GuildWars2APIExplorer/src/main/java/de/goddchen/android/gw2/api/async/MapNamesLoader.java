package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.MapName;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MapNamesLoader extends FixedAsyncTaskLoader<List<MapName>> {
    public MapNamesLoader(Context context) {
        super(context);
    }

    @Override
    public List<MapName> loadInBackground() {
        try {
            List<MapName> mapNames = Application.getDatabaseHelper().getMapNameDao().queryForAll();
            if (mapNames == null || mapNames.isEmpty()) {
                HttpsURLConnection connection =
                        (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/map_names.json").openConnection();
                mapNames = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                        new TypeToken<List<MapName>>() {
                        }.getType());
                for (MapName mapName : mapNames) {
                    Application.getDatabaseHelper().getMapNameDao().create(mapName);
                }
            }
            Collections.sort(mapNames, new Comparator<MapName>() {
                @Override
                public int compare(MapName mapName, MapName mapName2) {
                    return mapName.name.compareTo(mapName2.name);
                }
            });
            return mapNames;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading map names", e);
            return null;
        }
    }
}
