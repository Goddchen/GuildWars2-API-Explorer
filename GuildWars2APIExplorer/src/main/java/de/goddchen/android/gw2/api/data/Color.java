package de.goddchen.android.gw2.api.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Goddchen on 02.06.13.
 */
@DatabaseTable(tableName = "color")
public class Color {

    @DatabaseField
    public long id;

    @DatabaseField
    public String name;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public int[] base_rgb;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    public Config cloth;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    public Config leather;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    public Config metal;

    @DatabaseTable(tableName = "color_config")
    public static class Config {
        @DatabaseField(generatedId = true)
        public long id;
        @DatabaseField
        public int brightness;
        @DatabaseField
        public double contrast;
        @DatabaseField
        public int hue;
        @DatabaseField
        public double saturation;
        @DatabaseField
        public double lightness;
        @DatabaseField(dataType = DataType.SERIALIZABLE)
        public int[] rgb;

        public int getRgbColor() {
            return android.graphics.Color.rgb(rgb[0], rgb[1], rgb[2]);
        }
    }

}
