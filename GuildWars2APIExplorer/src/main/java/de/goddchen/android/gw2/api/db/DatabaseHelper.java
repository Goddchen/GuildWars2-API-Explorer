package de.goddchen.android.gw2.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Color;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.data.EventDetails;
import de.goddchen.android.gw2.api.data.EventName;
import de.goddchen.android.gw2.api.data.Floor;
import de.goddchen.android.gw2.api.data.GuildDetails;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.data.Map;
import de.goddchen.android.gw2.api.data.MapName;
import de.goddchen.android.gw2.api.data.Match;
import de.goddchen.android.gw2.api.data.MatchDetails;
import de.goddchen.android.gw2.api.data.ObjectiveName;
import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Recipe;
import de.goddchen.android.gw2.api.data.Region;
import de.goddchen.android.gw2.api.data.SkillChallenge;
import de.goddchen.android.gw2.api.data.Task;
import de.goddchen.android.gw2.api.data.World;

/**
 * Created by Goddchen on 22.05.13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static Dao<Item, Integer> itemDao;

    private static Dao<World, Integer> worldDao;

    private static Dao<MapName, Integer> mapNameDao;

    private static Dao<EventName, String> eventNameDao;

    private static Dao<ObjectiveName, Integer> objectiveNameDao;

    private static Dao<Color, Long> colorDao;

    private static Dao<GuildDetails, String> guildDetailsDao;

    private static Dao<Continent, Long> continentDao;

    private static Dao<Floor, Long> floorDao;

    private static Dao<Region, Long> regionDao;

    private static Dao<Map, Long> mapDao;

    private static Dao<POI, Long> poiDao;

    private static Dao<Task, Long> taskDao;

    private static Dao<SkillChallenge, Long> skillChallengeDao;

    private static Dao<EventDetails, String> eventDetailsDao;

    private static Dao<Recipe, Integer> recipeDao;

    public DatabaseHelper(Context context) {
        super(context, "gw2.db", null, 11);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Recipe.Ingredient.class);
            TableUtils.createTable(connectionSource, Item.Consumable.class);
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.createTable(connectionSource, World.class);
            TableUtils.createTable(connectionSource, EventName.class);
            TableUtils.createTable(connectionSource, MapName.class);
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, ObjectiveName.class);
            TableUtils.createTable(connectionSource, GuildDetails.class);
            TableUtils.createTable(connectionSource, Color.Config.class);
            TableUtils.createTable(connectionSource, Color.class);
            TableUtils.createTable(connectionSource, Continent.class);
            TableUtils.createTable(connectionSource, Floor.class);
            TableUtils.createTable(connectionSource, Region.class);
            TableUtils.createTable(connectionSource, Map.class);
            TableUtils.createTable(connectionSource, POI.class);
            TableUtils.createTable(connectionSource, Task.class);
            TableUtils.createTable(connectionSource, SkillChallenge.class);
            TableUtils.createTable(connectionSource, EventDetails.class);
            TableUtils.createTable(connectionSource, Recipe.class);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource,
                          int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, Recipe.Ingredient.class, true);
            TableUtils.dropTable(connectionSource, Item.Consumable.class, true);
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.dropTable(connectionSource, World.class, true);
            TableUtils.dropTable(connectionSource, EventName.class, true);
            TableUtils.dropTable(connectionSource, MapName.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            TableUtils.dropTable(connectionSource, ObjectiveName.class, true);
            TableUtils.dropTable(connectionSource, GuildDetails.class, true);
            TableUtils.dropTable(connectionSource, Color.Config.class, true);
            TableUtils.dropTable(connectionSource, Color.class, true);
            TableUtils.dropTable(connectionSource, Continent.class, true);
            TableUtils.dropTable(connectionSource, Floor.class, true);
            TableUtils.dropTable(connectionSource, Region.class, true);
            TableUtils.dropTable(connectionSource, Map.class, true);
            TableUtils.dropTable(connectionSource, POI.class, true);
            TableUtils.dropTable(connectionSource, Task.class, true);
            TableUtils.dropTable(connectionSource, SkillChallenge.class, true);
            TableUtils.dropTable(connectionSource, EventDetails.class, true);
            TableUtils.dropTable(connectionSource, Recipe.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error upgrading database", e);
        }
    }

    public Dao<Item, Integer> getItemDao() throws Exception {
        if (itemDao == null) {
            itemDao = DaoManager.createDao(getConnectionSource(), Item.class);
        }
        return itemDao;
    }

    public Dao<World, Integer> getWorldDao() throws Exception {
        if (worldDao == null) {
            worldDao = DaoManager.createDao(getConnectionSource(), World.class);
        }
        return worldDao;
    }

    public Dao<MapName, Integer> getMapNameDao() throws Exception {
        if (mapNameDao == null) {
            mapNameDao = DaoManager.createDao(getConnectionSource(), MapName.class);
        }
        return mapNameDao;
    }

    public Dao<EventName, String> getEventNameDao() throws Exception {
        if (eventNameDao == null) {
            eventNameDao = DaoManager.createDao(getConnectionSource(), EventName.class);
        }
        return eventNameDao;
    }

    public Dao<ObjectiveName, Integer> getObjectiveNameDao() throws Exception {
        if (objectiveNameDao == null) {
            objectiveNameDao = DaoManager.createDao(getConnectionSource(), ObjectiveName.class);
        }
        return objectiveNameDao;
    }

    public Dao<Color, Long> getColorDao() throws Exception {
        if (colorDao == null) {
            colorDao = DaoManager.createDao(getConnectionSource(), Color.class);
        }
        return colorDao;
    }

    public Dao<GuildDetails, String> getGuildDetailsDao() throws Exception {
        if (guildDetailsDao == null) {
            guildDetailsDao = DaoManager.createDao(getConnectionSource(), GuildDetails.class);
        }
        return guildDetailsDao;
    }

    public Dao<Continent, Long> getContinentDao() throws Exception {
        if (continentDao == null) {
            continentDao = DaoManager.createDao(getConnectionSource(), Continent.class);
        }
        return continentDao;
    }

    public Dao<Floor, Long> getFloorDao() throws Exception {
        if (floorDao == null) {
            floorDao = DaoManager.createDao(getConnectionSource(), Floor.class);
        }
        return floorDao;
    }

    public Dao<Region, Long> getRegionDao() throws Exception {
        if (regionDao == null) {
            regionDao = DaoManager.createDao(getConnectionSource(), Region.class);
        }
        return regionDao;
    }

    public Dao<Map, Long> getMapDao() throws Exception {
        if (mapDao == null) {
            mapDao = DaoManager.createDao(getConnectionSource(), Map.class);
        }
        return mapDao;
    }

    public Dao<POI, Long> getPoiDao() throws Exception {
        if (poiDao == null) {
            poiDao = DaoManager.createDao(getConnectionSource(), POI.class);
        }
        return poiDao;
    }

    public Dao<Task, Long> getTaskDao() throws Exception {
        if (taskDao == null) {
            taskDao = DaoManager.createDao(getConnectionSource(), Task.class);
        }
        return taskDao;
    }

    public Dao<SkillChallenge, Long> getSkillChallengeDao() throws Exception {
        if (skillChallengeDao == null) {
            skillChallengeDao = DaoManager.createDao(getConnectionSource(), SkillChallenge.class);
        }
        return skillChallengeDao;
    }

    public Dao<EventDetails, String> getEventDetailsDao() throws Exception {
        if (eventDetailsDao == null) {
            eventDetailsDao = DaoManager.createDao(getConnectionSource(), EventDetails.class);
        }
        return eventDetailsDao;
    }

    public Dao<Recipe, Integer> getRecipeDao() throws Exception {
        if (recipeDao == null) {
            recipeDao = DaoManager.createDao(getConnectionSource(), Recipe.class);
        }
        return recipeDao;
    }

    /*public static void loadMapNames(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getMapNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/map_names
                    .json?lang="
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
            event.mapName = Application.getDatabaseHelper().getMapNameDao().queryForId(event
            .map_id);
        }
    }*/

    public static void loadEventDetails(List<Event> events) throws Exception {
        if (Application.getDatabaseHelper().getEventDetailsDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/event_details" +
                            ".json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final JSONObject jsonResponse = new JSONObject(IOUtils.toString(connection
                    .getInputStream()));
            final List<EventDetails> eventDetails = new ArrayList<EventDetails>();
            Iterator<String> eventKeys = jsonResponse.getJSONObject("events").keys();
            Gson gson = new Gson();
            while (eventKeys.hasNext()) {
                String eventKey = eventKeys.next();
                EventDetails details = gson.fromJson(jsonResponse.getJSONObject("events")
                        .getJSONObject(eventKey).toString(), EventDetails.class);
                details.id = eventKey;
                eventDetails.add(details);
            }
            Application.getDatabaseHelper().getEventDetailsDao().callBatchTasks(new
                                                                                        Callable<Object>() {
                                                                                            @Override
                                                                                            public Object call() throws Exception {
                                                                                                for (EventDetails details : eventDetails) {
                                                                                                    Application.getDatabaseHelper().getEventDetailsDao().create(details);
                                                                                                }
                                                                                                return null;
                                                                                            }
                                                                                        });
        }
        for (Event event : events) {
            EventDetails eventDetails = Application.getDatabaseHelper().getEventDetailsDao()
                    .queryForId(event.event_id);
            if (eventDetails != null) {
                event.name = eventDetails.name;
                event.level = eventDetails.level;
                event.center_x = eventDetails.location.center[0];
                event.center_y = eventDetails.location.center[1];
                event.center_z = eventDetails.location.center[2];
            }
        }
    }

    public static void loadObjectiveNames(MatchDetails matchDetails) throws Exception {
        if (Application.getDatabaseHelper().getObjectiveNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2" +
                            ".com/v1/wvw/objective_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<ObjectiveName> objectiveNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<ObjectiveName>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>
                    () {
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
                objective.name = Application.getDatabaseHelper().getObjectiveNameDao().queryForId
                        (objective.id);
            }
        }
    }

    /*public static void loadGuildNames(MatchDetails matchDetails) throws Exception {
        if (Application.getDatabaseHelper().getObjectiveNameDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2
                    .com/v1/wvw/objective_names.json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<ObjectiveName> objectiveNames =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<ObjectiveName>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>
            () {
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
                if (!TextUtils.isEmpty(objective.owner_guild)) {
                    try {
                        objective.ownerGuildDetails = DatabaseHelper.getGuildDetails(objective
                        .owner_guild);
                    } catch (Exception e) {
                        Log.w(Application.Constants.LOG_TAG, "Error loading guild details", e);
                    }
                }
                objective.name = Application.getDatabaseHelper().getObjectiveNameDao().queryForId
                (objective.id);
            }
        }
    }*/


    public static void loadWorldNames(List<Match> matches) throws Exception {
        if (Application.getDatabaseHelper().getWorldDao().queryForAll().size() == 0) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/world_names" +
                            ".json?lang="
                            + Locale.getDefault().getLanguage())
                            .openConnection();
            final List<World> worlds =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                            new TypeToken<List<World>>() {
                            }.getType());
            Application.getDatabaseHelper().getEventNameDao().callBatchTasks(new Callable<Object>
                    () {
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
            match.redWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match
                    .red_world_id);
            match.greenWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match
                    .green_world_id);
            match.blueWorld = Application.getDatabaseHelper().getWorldDao().queryForId(match
                    .blue_world_id);
        }
    }

    public static Item getItem(int id) throws Exception {
        HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/item_details" +
                        ".json?item_id=" + id
                        + "&lang=" + Locale.getDefault().getLanguage()).openConnection();
        Item item = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                Item.class);
        return item;
    }

    /*public static GuildDetails getGuildDetails(String id) throws Exception {
        Dao<GuildDetails, String> dao = DaoManager.createDao(Application.getDatabaseHelper()
        .getConnectionSource(), GuildDetails.class);
        GuildDetails guildDetails = dao.queryForId(id);
        if (guildDetails == null) {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/guild_details
                    .json?guild_id=" + id
                            + "&lang=" + Locale.getDefault().getLanguage()).openConnection();
            guildDetails =
                    new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                    GuildDetails.class);
            dao.create(guildDetails);
        }
        return guildDetails;
    }*/
}
