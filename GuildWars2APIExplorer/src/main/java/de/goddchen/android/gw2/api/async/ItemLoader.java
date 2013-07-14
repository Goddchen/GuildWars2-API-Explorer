package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Item;
import de.goddchen.android.gw2.api.db.DatabaseHelper;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemLoader extends FixedAsyncTaskLoader<Item> {
    private int mId;

    public ItemLoader(Context context, int id) {
        super(context);
        mId = id;
    }

    @Override
    public Item loadInBackground() {
        try {
            Item item = DatabaseHelper.getItem(mId);
            Application.getDatabaseHelper().getItemDao().createOrUpdate(item);
            return item;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading item details", e);
            return null;
        }
    }
}
