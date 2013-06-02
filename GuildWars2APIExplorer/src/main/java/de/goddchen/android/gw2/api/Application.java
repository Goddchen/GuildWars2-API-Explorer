package de.goddchen.android.gw2.api;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 22.05.13.
 */
public class Application extends android.app.Application {

    public static final class Constants {
        public static final String LOG_TAG = "GW2 API Explorer";
    }

    public static final class Loaders {
        public static final int WORLDS = 3;
        public static final int EVENTS = 4;
        public static final int MAP_NAMES = 5;
        public static final int MATCHES = 6;
        public static final int MATCH_DETAILS = 7;
        public static final int ITEM_DETAILS = 8;
        public static final int RECIPE_DETAILS = 9;
        public static final int ITEM_IDS = 10;
        public static final int RECIPE_IDS = 11;
        public static final int BUILD = 12;
        public static final int COLORS = 13;
    }

    public static final class Extras {
        public static final String WORLD = "world";
        public static final String MAP = "map";
        public static final String MATCH = "match";
        public static final String MATCH_MAP = "match_map";
        public static final String ITEM_ID = "item.id";
        public static final String RECIPE_ID = "recipe.id";
    }

    public static final class Preferences {
        public static final String WVWVW_REFRESH = "wvwvw.auto.refresh";
        public static final String HOME_WORLD = "home.world";
        public static final String EVENTS_REFRESH = "events.auto.refresh";
    }

    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        setupORMLite();
        if (!getString(R.string.app_name).contains("DEBUG")) {
            Crittercism.init(getApplicationContext(), "519cb89c558d6a448b000007");
        }
    }

    private void setupORMLite() {
        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
