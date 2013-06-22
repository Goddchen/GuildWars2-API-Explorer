package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.data.Continent;
import de.goddchen.android.gw2.api.data.Event;
import de.goddchen.android.gw2.api.fragments.ContinentFragment;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, List<Event> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_event, parent, false);
        }
        if (event.name == null || TextUtils.isEmpty(event.name)) {
            ((TextView) convertView.findViewById(R.id.name)).setText("---");
        } else {
            ((TextView) convertView.findViewById(R.id.name)).setText(event.name);
        }
        ((TextView) convertView.findViewById(R.id.status)).setText(String.format("(%s)", event.state));
        if ("warmup".equalsIgnoreCase(event.state)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.event_warmup));
        } else if ("fail".equalsIgnoreCase(event.state)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.event_fail));
        } else if ("success".equalsIgnoreCase(event.state)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.event_success));
        } else if ("active".equalsIgnoreCase(event.state)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.event_active));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }
        if (event.center_x != 0 && event.center_y != 0) {
            convertView.findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Continent continent = Application.getDatabaseHelper().getContinentDao()
                                .queryForFirst(
                                        Application.getDatabaseHelper().getContinentDao().queryBuilder()
                                                .where().eq("name", "Tyria").prepare()
                                );
                        if (continent == null) {
                            Toast.makeText(getContext(), R.string.toast_use_map_once,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ((BaseFragmentActivity) getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment, ContinentFragment.newInstance(continent,
                                            event))
                                    .addToBackStack("event-map")
                                    .commit();
                        }
                    } catch (Exception e) {
                        Log.e(Application.Constants.LOG_TAG, "Error loading event map", e);
                    }
                }
            });
        }
        return convertView;
    }
}
