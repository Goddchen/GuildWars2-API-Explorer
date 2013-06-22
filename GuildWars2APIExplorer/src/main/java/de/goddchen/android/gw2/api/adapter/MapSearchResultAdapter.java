package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.goddchen.android.gw2.api.data.POI;
import de.goddchen.android.gw2.api.data.Task;

/**
 * Created by Goddchen on 22.06.13.
 */
public class MapSearchResultAdapter extends ArrayAdapter<Object> {
    private List<POI> mPois;
    private List<Task> mTasks;

    public MapSearchResultAdapter(Context context, List<POI> pois, List<Task> tasks) {
        super(context, R.layout.simple_list_item_1);
        mPois = pois == null ? new ArrayList<POI>() : pois;
        mTasks = tasks == null ? new ArrayList<Task>() : tasks;
    }

    @Override
    public int getCount() {
        return mPois.size() + mTasks.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > mPois.size() - 1) {
            return mTasks.get(position - mPois.size());
        } else {
            return mPois.get(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (getItem(position) instanceof POI) {
            ((TextView) view.findViewById(R.id.text1)).setText(((POI) getItem(position)).name);
        } else if (getItem(position) instanceof Task) {
            ((TextView) view.findViewById(R.id.text1)).setText(((Task) getItem(position)).objective);
        }
        return view;
    }
}
