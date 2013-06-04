package de.goddchen.android.gw2.api.data;

/**
 * Created by Goddchen on 02.06.13.
 */
public class Color {

    public long id;

    public String name;

    public int[] base_rgb;

    public Config cloth;

    public Config leather;

    public Config metal;

    public static class Config {
        public int brightness;
        public double contrast;
        public int hue;
        public double saturation;
        public double lightness;
        public int[] rgb;

        public int getRgbColor() {
            return android.graphics.Color.rgb(rgb[0], rgb[1], rgb[2]);
        }
    }

}
