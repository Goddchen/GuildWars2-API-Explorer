package de.goddchen.android.gw2.api.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.j256.ormlite.android.AndroidDatabaseResults;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemsFragment extends SherlockListFragment {

    public static ItemsFragment newInstance() {
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setFastScrollEnabled(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Cursor cursor = ((AndroidDatabaseResults) Application.getDatabaseHelper().getItemDao().queryBuilder()
                    .orderBy("name", true)
                    .selectColumns("name", "_id")
                    .iterator().getRawResults()).getRawCursor();
            setListAdapter(new CursorAdapter(getActivity(), cursor, true) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                    View view = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                    return view;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    ((TextView) view.findViewById(android.R.id.text1)).setText(cursor.getString(cursor.getColumnIndex("name")));
                }
            });
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error getting cursor", e);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Integer itemId = (int) getListAdapter().getItemId(position);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, ItemFragment.newInstance(itemId))
                .addToBackStack("item")
                .commit();
    }
}
