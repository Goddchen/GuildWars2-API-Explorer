package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.data.Event;

import java.util.List;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, List<Event> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        try {
            String eventName = Application.getDatabaseHelper().getEventNameDao().queryForId(getItem(position).event_id).name;
            ((TextView) convertView.findViewById(R.id.text1)).setText(eventName);
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error getting event name", e);
            ((TextView) convertView.findViewById(R.id.text1)).setText("Error getting event name");
        }
        return convertView;
    }
}
