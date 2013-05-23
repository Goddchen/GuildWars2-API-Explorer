package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Match;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchAdapter extends ArrayAdapter<Match> {
    public MatchAdapter(Context context, List<Match> objects) {
        super(context, R.layout.listitem_match, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Match match = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_match, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.name))
                .setText(String.format("%s\nvs\n%s\nvs\n%s",
                        match.redWorld == null || TextUtils.isEmpty(match.redWorld.name) ? "---" : match.redWorld.name,
                        match.blueWorld == null || TextUtils.isEmpty(match.blueWorld.name) ? "---" : match.blueWorld.name,
                        match.greenWorld == null || TextUtils.isEmpty(match.greenWorld.name) ? "---" : match.greenWorld.name
                ));
        return convertView;
    }
}
