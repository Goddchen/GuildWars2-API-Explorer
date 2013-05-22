package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import de.goddchen.android.gw2.api.data.Item;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemsLoader extends AsyncTaskLoader<List<Item>> {
    public ItemsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Item> loadInBackground() {
        return null;
    }
}
