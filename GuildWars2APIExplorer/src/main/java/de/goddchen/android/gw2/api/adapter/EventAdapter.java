package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Event;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, List<Event> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_event, parent, false);
        }
        if (event.eventName == null || TextUtils.isEmpty(event.eventName.name)) {
            ((TextView) convertView.findViewById(R.id.name)).setText("---");
        } else {
            ((TextView) convertView.findViewById(R.id.name)).setText(event.eventName.name);
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
        return convertView;
    }
}
