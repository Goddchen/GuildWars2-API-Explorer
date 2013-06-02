package de.goddchen.android.gw2.api.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Goddchen on 02.06.13.
 */
public class Color {

    public long id;

    public String name;

    @SerializedName(value = "default")
    public Config _default;

    public Config cloth;

    public Config leather;

    public Config metal;

    public static class Config {
        public int brightness;
        public int contrast;
        public int hue;
        public int saturation;
        public int lightness;

        public int hslToRgb() {
            float[] hsl = new float[]{hue, saturation / 256f, lightness / 256f};
            float h = hsl[0];
            float s = hsl[1];
            float l = hsl[2];

            float c = (1 - Math.abs(2.f * l - 1.f)) * s;
            float h_ = h / 60.f;
            float h_mod2 = h_;
            if (h_mod2 >= 4.f) h_mod2 -= 4.f;
            else if (h_mod2 >= 2.f) h_mod2 -= 2.f;

            float x = c * (1 - Math.abs(h_mod2 - 1));
            float r_, g_, b_;
            if (h_ < 1) {
                r_ = c;
                g_ = x;
                b_ = 0;
            } else if (h_ < 2) {
                r_ = x;
                g_ = c;
                b_ = 0;
            } else if (h_ < 3) {
                r_ = 0;
                g_ = c;
                b_ = x;
            } else if (h_ < 4) {
                r_ = 0;
                g_ = x;
                b_ = c;
            } else if (h_ < 5) {
                r_ = x;
                g_ = 0;
                b_ = c;
            } else {
                r_ = c;
                g_ = 0;
                b_ = x;
            }

            float m = l - (0.5f * c);
            int r = (int) ((r_ + m) * (255.f) + 0.5f);
            int g = (int) ((g_ + m) * (255.f) + 0.5f);
            int b = (int) ((b_ + m) * (255.f) + 0.5f);
//            return r << 16 | g << 8 | b;
            return android.graphics.Color.rgb(r, g, b);
        }

        public void rgbToHsl(int rgb, float[] hsl) {
            float r = ((0x00ff0000 & rgb) >> 16) / 255.f;
            float g = ((0x0000ff00 & rgb) >> 8) / 255.f;
            float b = ((0x000000ff & rgb)) / 255.f;
            float max = Math.max(Math.max(r, g), b);
            float min = Math.min(Math.min(r, g), b);
            float c = max - min;

            float h_ = 0.f;
            if (c == 0) {
                h_ = 0;
            } else if (max == r) {
                h_ = (float) (g - b) / c;
                if (h_ < 0) h_ += 6.f;
            } else if (max == g) {
                h_ = (float) (b - r) / c + 2.f;
            } else if (max == b) {
                h_ = (float) (r - g) / c + 4.f;
            }
            float h = 60.f * h_;

            float l = (max + min) * 0.5f;

            float s;
            if (c == 0) {
                s = 0.f;
            } else {
                s = c / (1 - Math.abs(2.f * l - 1.f));
            }

            hsl[0] = h;
            hsl[1] = s;
            hsl[2] = l;
        }
    }

}
