package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.MatchDetails;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ObjectiveAdapter extends ArrayAdapter<MatchDetails.Objective> {
    public ObjectiveAdapter(Context context, List<MatchDetails.Objective> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MatchDetails.Objective objective = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_objective, parent, false);
        }
        if (objective.name == null || TextUtils.isEmpty(objective.name.name)) {
            ((TextView) convertView.findViewById(R.id.name)).setText("---");
        } else {
            ((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).name.name);
        }
        ((TextView) convertView.findViewById(R.id.owner)).setText(objective.owner);
        if ("red".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_red));
        } else if ("green".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_green));
        } else if ("blue".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_blue));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
        return convertView;
    }
}
