package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.ItemsLoader;
import de.goddchen.android.gw2.api.data.Item;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ItemsFragment extends SherlockListFragment {

    public static ItemsFragment newInstance() {
        ItemsFragment fragment = new ItemsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showList(false);
        getLoaderManager().initLoader(Application.Loaders.ITEMS, null, mItemsLoaderCallbacks);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Integer itemId = ((Item) l.getItemAtPosition(position)).item_id;
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, ItemFragment.newInstance(itemId))
                .addToBackStack("item")
                .commit();
    }

    private void showList(boolean shown) {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.progress_content).setVisibility(shown ? View.GONE : View
                    .VISIBLE);
            view.findViewById(R.id.list_content).setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    private LoaderManager.LoaderCallbacks<List<Item>> mItemsLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Item>>() {
                @Override
                public Loader<List<Item>> onCreateLoader(int i, Bundle bundle) {
                    return new ItemsLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Item>> listLoader, List<Item> items) {
                    showList(true);
                    if (items != null) {
                        setListAdapter(new ArrayAdapter<Item>(getActivity(),
                                android.R.layout.simple_list_item_1, items) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view.findViewById(android.R.id.text1))
                                        .setText(getItem(position).name);
                                return view;
                            }
                        });
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Item>> listLoader) {

                }
            };
}
