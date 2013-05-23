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
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Created by Goddchen on 22.05.13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static Dao<Item, Integer> itemDao;

    private static Dao<World, Integer> worldDao;

    private static Dao<MapName, Integer> mapNameDao;

    private static Dao<EventName, String> eventNameDao;

    private static Dao<ObjectiveName, Integer> objectiveNameDao;

    public DatabaseHelper(Context context) {
        super(context, "gw2.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.createTable(connectionSource, World.class);
            TableUtils.createTable(connectionSource, EventName.class);
            TableUtils.createTable(connectionSource, MapName.class);
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, ObjectiveName.class);
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
            TableUtils.dropTable(connectionSource, ObjectiveName.class, true);
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

    public Dao<ObjectiveName, Integer> getObjectiveNameDao() throws Exception {
        if (objectiveNameDao == null) {
            objectiveNameDao = BaseDaoImpl.createDao(getConnectionSource(), ObjectiveName.class);
        }
        return objectiveNameDao;
    }

    public static void loadMapNames(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getMapNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/map_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<MapName> mapNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<MapName>>() {
                            }.getType());
            Application.getDatabaseHelper().getMapNameDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (MapName mapName : mapNames) {
                        Application.getDatabaseHelper().getMapNameDao().create(mapName);
                    }
                    return null;
                }
            });
        }
        for (Event event : events) {
            event.mapName = Application.getDatabaseHelper().getMapNameDao().queryForId(event.map_id);
        }
    }

    public static void loadEventNames(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getEventNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/event_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<EventName> eventNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<EventName>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (EventName eventName : eventNames) {
                        Application.getDatabaseHelper().getEventNameDao().create(eventName);
                    }
                    return null;
                }
            });
        }
        for (Event event : events) {
            event.eventName = Application.getDatabaseHelper().getEventNameDao().queryForId(event.event_id);
        }
    }

    public static void loadObjectiveNames(MatchDetails matchDetails) throws Exception {
        if (Application.getDatabaseHelper().getObjectiveNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/wvw/objective_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<ObjectiveName> objectiveNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<ObjectiveName>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (ObjectiveName objectiveName : objectiveNames) {
                        Application.getDatabaseHelper().getObjectiveNameDao().create(objectiveName);
                    }
                    return null;
                }
            });
        }
        for (MatchDetails.Map map : matchDetails.maps) {
            for (MatchDetails.Objective objective : map.objectives) {
                objective.name = Application.getDatabaseHelper().getObjectiveNameDao().queryForId(objective.id);
            }
        }
    }


    public static void loadWorldNames(List<Match> matches) throws Exception {
        if (Application.getDatabaseHelper().getWorldDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/world_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<World> worlds =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<World>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (World world : worlds) {
                        Application.getDatabaseHelper().getWorldDao().create(world);
                    }
                    return null;
                }
            });
        }
        for (Match match : matches) {
            match.redWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match.red_world_id);
            match.greenWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match.green_world_id);
            match.blueWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match.blue_world_id);
        }
    }

    public static Item getItem(int id) throws Exception {
        HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/item_details.json?item_id=" + id
                        + "&lang=" + Locale.getDefault().getLanguage()).openConnection();
        Item item = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), Item.class);
        return item;
    }
}
