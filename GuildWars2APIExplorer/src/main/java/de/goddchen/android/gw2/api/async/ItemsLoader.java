package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Item;

/**
 * Created by Goddchen on 14.07.13.
 */
public class ItemsLoader extends FixedAsyncTaskLoader<List<Item>> {
    public ItemsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Item> loadInBackground() {
        try {
            return Application.getDatabaseHelper().getItemDao().queryBuilder()
                    .orderBy("name", true)
                    .query();
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading items from db", e);
            return null;
        }
    }
}
