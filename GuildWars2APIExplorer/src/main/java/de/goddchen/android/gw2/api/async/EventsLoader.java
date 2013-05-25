package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

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
public class EventsLoader extends FixedAsyncTaskLoader<List<Event>> {
    private int mWorldId;

    public EventsLoader(Context context, int worldId) {
        super(context);
        mWorldId = worldId;
    }

    @Override
    public List<Event> loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/events.json?world_id=" + mWorldId
                            + "&lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            List<Event> events = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    Response.class).events;
            DatabaseHelper.loadEventNames(events);
            DatabaseHelper.loadMapNames(events);
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event event, Event event2) {
                    if (event.state.equals(event2.state)) {
                        if (event.eventName == null || event2.eventName == null) {
                            return -1;
                        } else {
                            return event.eventName.name.compareTo(event2.eventName.name);
                        }
                    } else {
                        return -(Integer.valueOf(event.getStatePriority()).compareTo(event2.getStatePriority()));
                    }
                }
            });
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
