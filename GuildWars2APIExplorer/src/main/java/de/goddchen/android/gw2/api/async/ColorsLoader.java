package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Color;

/**
 * Created by Goddchen on 02.06.13.
 */
public class ColorsLoader extends FixedAsyncTaskLoader<List<Color>> {
    public ColorsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Color> loadInBackground() {
        try {
            List<Color> colors = new ArrayList<Color>();
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL("https://api.guildwars2.com/v1/colors.json?"
                            + "lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            JSONObject json = new JSONObject(IOUtils.toString(connection.getInputStream()));
            JSONObject jsonColors = json.getJSONObject("colors");
            Iterator<String> it = jsonColors.keys();
            while (it.hasNext()) {
                String id = it.next();
                JSONObject jsonColor = jsonColors.getJSONObject(id);
                Color color = new Color();
                color.id = Long.valueOf(id);
                color.name = jsonColor.optString("name");
                color._default = parseConfig(jsonColor.getJSONObject("default"));
                color.cloth = parseConfig(jsonColor.getJSONObject("cloth"));
                color.leather = parseConfig(jsonColor.getJSONObject("leather"));
                color.metal = parseConfig(jsonColor.getJSONObject("metal"));
                colors.add(color);
            }
            return colors;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading colors", e);
            return null;
        }
    }

    private Color.Config parseConfig(JSONObject json) throws Exception {
        Color.Config config = new Color.Config();
        config.brightness = json.optInt("brightness");
        config.contrast = json.optInt("contrast");
        config.hue = json.optInt("hue");
        config.lightness = json.optInt("lightness");
        config.saturation = json.optInt("saturation");
        return config;
    }
}
