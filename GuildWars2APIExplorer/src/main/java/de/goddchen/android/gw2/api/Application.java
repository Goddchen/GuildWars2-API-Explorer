package de.goddchen.android.gw2.api;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 22.05.13.
 */
public class Application extends android.app.Application {

    public static final class Constants {
        public static final String LOG_TAG = "GW2 API Explorer";
    }

    public static final class Loaders {
        public static final int ITEM_IDS = 0;
        public static final int ITEM_DETAILS = 1;
        public static final int WORLDS_DB = 2;
        public static final int WORLDS_ONLINE = 3;
        public static final int EVENTS = 4;
    }

    private static DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        setupORMLite();
    }

    private void setupORMLite() {
        OpenHelperManager.setOpenHelperFactory(new OpenHelperManager.SqliteOpenHelperFactory() {
            @Override
            public OrmLiteSqliteOpenHelper getHelper(Context context) {
                return new DatabaseHelper(getApplicationContext());
            }
        });
        databaseHelper = (DatabaseHelper) OpenHelperManager.getHelper(getApplicationContext());
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OpenHelperManager.release();
        databaseHelper = null;
    }
}
