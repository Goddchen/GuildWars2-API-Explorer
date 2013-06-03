package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                color._default = parseConfig(jsonColor.optJSONObject("default"));
                color.cloth = parseConfig(jsonColor.optJSONObject("cloth"));
                color.leather = parseConfig(jsonColor.optJSONObject("leather"));
                color.metal = parseConfig(jsonColor.optJSONObject("metal"));
                colors.add(color);
            }
            Collections.sort(colors, new Comparator<Color>() {
                @Override
                public int compare(Color lhs, Color rhs) {
                    Color.Config config1 = lhs._default == null ? lhs.cloth : lhs._default;
                    Color.Config config2 = rhs._default == null ? rhs.cloth : rhs._default;

                    int[] rgbBase = new int[]{128, 26, 26};
                    int[] rgb1 = new int[3];
                    int[] rgb2 = new int[3];
                    for (int i = 0; i < 3; i++) {
                        rgb1[i] = (int) (((rgbBase[i] + config1.brightness) - 128) * config1.contrast + 128);
                        rgb2[i] = (int) (((rgbBase[i] + config2.brightness) - 128) * config2.contrast + 128);
                    }
                    float[] lhs1 = Color.Config.RGBtoHSL(rgb1[0], rgb1[1], rgb1[2], null);
                    float[] lhs2 = Color.Config.RGBtoHSL(rgb2[0], rgb2[1], rgb2[2], null);
                    float hue1 = lhs1[0] * 360 + config1.hue;
                    float hue2 = lhs2[0] * 360 + config2.hue;
                    return Float.valueOf(hue1).compareTo(hue2);
                }
            });
            return colors;
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading colors", e);
            return null;
        }
    }

    private Color.Config parseConfig(JSONObject json) throws Exception {
        if (json == null) {
            return null;
        } else {
            Color.Config config = new Color.Config();
            config.brightness = json.optInt("brightness");
            config.contrast = json.optDouble("contrast");
            config.hue = json.optInt("hue");
            config.lightness = json.optDouble("lightness");
            config.saturation = json.optDouble("saturation");
            return config;
        }
    }
}
