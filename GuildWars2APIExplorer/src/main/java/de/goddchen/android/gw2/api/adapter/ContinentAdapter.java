package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.goddchen.android.gw2.api.data.Continent;

/**
 * Created by Goddchen on 21.06.13.
 */
public class ContinentAdapter extends ArrayAdapter<Continent> {
    public ContinentAdapter(Context context, List<Continent> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ((TextView) view.findViewById(R.id.text1)).setText(getItem(position).name);
        return view;
    }
}
