package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.World;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class WorldsDbLoader extends FixedAsyncTaskLoader<List<World>> {
    public WorldsDbLoader(Context context) {
        super(context);
    }

    @Override
    public List<World> loadInBackground() {
        try {
            List<World> worlds = Application.getDatabaseHelper().getWorldDao().queryForAll();
            return worlds;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading worlds", e);
            return null;
        }
    }
}
