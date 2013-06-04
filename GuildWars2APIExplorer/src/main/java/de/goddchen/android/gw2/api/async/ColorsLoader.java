package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

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
            final List<Color> colors = new ArrayList<Color>();
            final Dao<Color, Long> colorDao = Application.getDatabaseHelper().getColorDao();
            if (colorDao.countOf() == 0) {
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
                    JSONArray baseRgbs = jsonColor.optJSONArray("base_rgb");
                    if (baseRgbs != null && baseRgbs.length() == 3) {
                        color.base_rgb = new int[3];
                        for (int i = 0; i < 3; i++) {
                            color.base_rgb[i] = baseRgbs.getInt(i);
                        }
                    }
                    color.cloth = parseConfig(jsonColor.optJSONObject("cloth"));
                    color.leather = parseConfig(jsonColor.optJSONObject("leather"));
                    color.metal = parseConfig(jsonColor.optJSONObject("metal"));
                    colors.add(color);
                }
                colorDao.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (Color color : colors) {
                            colorDao.create(color);
                        }
                        Log.d(Application.Constants.LOG_TAG,
                                colors.size() + " colors persisted");
                        return null;
                    }
                });
            } else {
                colors.addAll(colorDao.queryForAll());
            }
            Collections.sort(colors, new Comparator<Color>() {
                @Override
                public int compare(Color lhs, Color rhs) {
                    Color.Config configl = lhs.cloth != null ?
                            lhs.cloth : lhs.leather != null ? lhs.leather : lhs.metal;
                    Color.Config configr = rhs.cloth != null
                            ? rhs.cloth : rhs.leather != null ? rhs.leather : rhs.metal;

                    float[] hsvl = new float[3];
                    float[] hsvr = new float[3];
                    android.graphics.Color.
                            RGBToHSV(configl.rgb[0], configl.rgb[1], configl.rgb[2], hsvl);
                    android.graphics.Color
                            .RGBToHSV(configr.rgb[0], configr.rgb[1], configr.rgb[2], hsvr);

                    return Float.valueOf(hsvl[0]).compareTo(hsvr[0]);
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
            JSONArray rgbs = json.optJSONArray("rgb");
            if (rgbs != null && rgbs.length() == 3) {
                config.rgb = new int[3];
                for (int i = 0; i < 3; i++) {
                    config.rgb[i] = rgbs.getInt(i);
                }
            }
            return config;
        }
    }
}
