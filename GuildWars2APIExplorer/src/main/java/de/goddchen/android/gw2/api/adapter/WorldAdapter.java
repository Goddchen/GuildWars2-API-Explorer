package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.data.World;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class WorldAdapter extends ArrayAdapter<World> {
    public WorldAdapter(Context context, List<World> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).name);
        return convertView;
    }
}
