package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Color;

/**
 * Created by Goddchen on 02.06.13.
 */
public class ColorAdapter extends ArrayAdapter<Color> {
    public ColorAdapter(Context context, List<Color> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Color color = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_color, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.name)).setText(color.name);
        ((TextView) convertView.findViewById(R.id.id))
                .setText(String.format("ID: %d", color.id));
        setColor(convertView.findViewById(R.id.cloth), color.cloth);
        setColor(convertView.findViewById(R.id.leather), color.leather);
        setColor(convertView.findViewById(R.id.metal), color.metal);
        return convertView;
    }

    private void setColor(View view, Color.Config config) {
        view.setBackgroundColor(
                config == null ? android.graphics.Color.TRANSPARENT : config.getRgbColor());
    }
}
