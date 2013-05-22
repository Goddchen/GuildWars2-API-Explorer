package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.data.World;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventsLoader extends FixedAsyncTaskLoader<List<Event>> {
    private World mWorld;

    public EventsLoader(Context context, World world) {
        super(context);
        mWorld = world;
    }

    @Override
    public List<Event> loadInBackground() {
        try {
            DatabaseHelper.loadEventNames();
            DatabaseHelper.loadMapNames();
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/events.json?world_id=" + mWorld.id)
                            .openConnection();
            List<Event> events = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    Response.class).events;
            return events;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading events", e);
            return null;
        }
    }

    private class Response {
        public List<Event> events;
    }
}
