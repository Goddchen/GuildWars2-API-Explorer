package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Item;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemIdsLoader extends FixedAsyncTaskLoader<List<Integer>> {
    public ItemIdsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Integer> loadInBackground() {
        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/items.json").openConnection();
            Integer[] ids = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), Integer[].class);
            for (int id : ids) {
                Item item = new Item();
                item.item_id = id;
                Application.getDatabaseHelper().getItemDao().create(item);
            }
            return Arrays.asList(ids);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading item ids", e);
            return null;
        }
    }
}
