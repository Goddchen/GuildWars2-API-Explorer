package de.goddchen.android.gw2.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static Dao<Item, Integer> itemDao;

    private static Dao<World, Integer> worldDao;

    private static Dao<MapName, Integer> mapNameDao;

    private static Dao<EventName, String> eventNameDao;

    public DatabaseHelper(Context context) {
        super(context, "gw2.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.createTable(connectionSource, World.class);
            TableUtils.createTable(connectionSource, EventName.class);
            TableUtils.createTable(connectionSource, MapName.class);
            TableUtils.createTable(connectionSource, Event.class);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.dropTable(connectionSource, World.class, true);
            TableUtils.dropTable(connectionSource, EventName.class, true);
            TableUtils.dropTable(connectionSource, MapName.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error upgrading database", e);
        }
    }

    public Dao<Item, Integer> getItemDao() throws Exception {
        if (itemDao == null) {
            itemDao = BaseDaoImpl.createDao(getConnectionSource(), Item.class);
        }
        return itemDao;
    }

    public Dao<World, Integer> getWorldDao() throws Exception {
        if (worldDao == null) {
            worldDao = BaseDaoImpl.createDao(getConnectionSource(), World.class);
        }
        return worldDao;
    }

    public Dao<MapName, Integer> getMapNameDao() throws Exception {
        if (mapNameDao == null) {
            mapNameDao = BaseDaoImpl.createDao(getConnectionSource(), MapName.class);
        }
        return mapNameDao;
    }

    public Dao<EventName, String> getEventNameDao() throws Exception {
        if (eventNameDao == null) {
            eventNameDao = BaseDaoImpl.createDao(getConnectionSource(), EventName.class);
        }
        return eventNameDao;
    }

    public static void loadMapNames(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getMapNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/map_names.json").openConnection();
            List<MapName> mapNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<MapName>>() {
                            }.getType());
            for (MapName mapName : mapNames) {
                Application.getDatabaseHelper().getMapNameDao().create(mapName);
            }
        }
        for (Event event : events) {
            event.mapName = Application.getDatabaseHelper().getMapNameDao().queryForId(event.map_id);
        }
    }

    public static void loadEventNames(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getEventNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/event_names.json").openConnection();
            List<EventName> eventNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<EventName>>() {
                            }.getType());
            for (EventName eventName : eventNames) {
                Application.getDatabaseHelper().getEventNameDao().create(eventName);
            }
        }
        for (Event event : events) {
            event.eventName = Application.getDatabaseHelper().getEventNameDao().queryForId(event.event_id);
        }
    }
}
