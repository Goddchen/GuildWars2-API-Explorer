package de.goddchen.android.gw2.api;

import android.os.Handler;
import android.os.Looper;

import com.crittercism.app.Crittercism;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.otto.Bus;

import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 22.05.13.
 */
public class Application extends android.app.Application {

    private static DatabaseHelper databaseHelper;
    private static Bus bus;

    public static Bus getBus() {
        return bus;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupORMLite();
        setupBus();
        if (!getString(R.string.app_name).contains("DEBUG")) {
            Crittercism.init(getApplicationContext(), "519cb89c558d6a448b000007");
        }
    }

    private void setupBus() {
        final Handler handler = new Handler();
        bus = new Bus() {

            @Override
            public void post(final Object event) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    super.post(event);
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            post(event);
                        }
                    });
                }
            }
        };
    }

    private void setupORMLite() {
        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }

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
        public static final int CONTINENTS = 14;
        public static final int FLOOR_METADATA = 15;
        public static final int FLOOR_MARKERS = 16;
        public static final int MAP_SEARCH = 17;
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
}
