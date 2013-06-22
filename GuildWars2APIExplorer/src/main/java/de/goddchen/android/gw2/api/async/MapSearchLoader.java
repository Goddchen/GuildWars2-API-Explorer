package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Floor;
import de.goddchen.android.gw2.api.data.Map;
import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Region;
import de.goddchen.android.gw2.api.data.Task;

/**
 * Created by Goddchen on 22.06.13.
 */
public class MapSearchLoader extends FixedAsyncTaskLoader<List<List<?>>> {

    private Continent mContinent;

    private String mQuery;

    public MapSearchLoader(Context context, Continent continent, String query) {
        super(context);
        mContinent = continent;
        mQuery = query;
    }

    @Override
    public List<List<?>> loadInBackground() {
        try {
            List<POI> pois = new ArrayList<POI>();
            List<Task> tasks = new ArrayList<Task>();
            Floor floor = Application.getDatabaseHelper().getFloorDao().queryForFirst(
                    Application.getDatabaseHelper().getFloorDao().queryBuilder().where().eq
                            ("continent_id", mContinent.id).and().eq("floor_id", 1).prepare()
            );
            for (Region region : floor.regions) {
                for (Map map : region.maps) {
                    pois.addAll(Application.getDatabaseHelper().getPoiDao().query(
                            Application.getDatabaseHelper().getPoiDao().queryBuilder().where().like
                                    ("name", "%" + mQuery + "%")
                                    .and().eq("map_id", map.id).prepare()));
                    tasks.addAll(Application.getDatabaseHelper().getTaskDao().query(
                            Application.getDatabaseHelper().getTaskDao().queryBuilder().where()
                                    .like("objective", "%" + mQuery + "%")
                                    .and().eq("map_id", map.id)
                                    .prepare()));
                }
            }
            List<List<? extends Object>> results = new ArrayList<List<? extends Object>>();
            results.add(pois);
            results.add(tasks);
            return results;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error searching", e);
            return null;
        }
    }
}
