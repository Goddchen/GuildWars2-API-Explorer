package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import java.util.Arrays;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.ItemLoader;
import de.goddchen.android.gw2.api.data.Item;

/**
 * Created by Goddchen on 23.05.13.
 */
public class ItemFragment extends SherlockFragment {

    private Handler mHandler;

    private Item mItem;

    public static ItemFragment newInstance(int id) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(Application.Extras.ITEM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        try {
            mItem = Application.getDatabaseHelper().getItemDao().queryForId(
                    getArguments().getInt(Application.Extras.ITEM_ID)
            );
        } catch (Exception e) {
            Log.e(Application.Constants.LOG_TAG, "Error loading item", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mItem == null) {
            view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            view.findViewById(R.id.content).setVisibility(View.GONE);
            getLoaderManager().initLoader(Application.Loaders.ITEM_DETAILS, null,
                    mItemLoaderCallbacks);
        } else {
            view.findViewById(R.id.loading).setVisibility(View.GONE);
            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
            updateView();
        }
    }

    private void updateView() {
        ((TextView) getView().findViewById(R.id.name))
                .setText(getString(R.string.item_name, mItem.name));
        ((TextView) getView().findViewById(R.id.description))
                .setText(getString(R.string.item_description,
                        mItem.description == null ? "" :
                                Html.fromHtml(mItem.description)));
        ((TextView) getView().findViewById(R.id.type))
                .setText(getString(R.string.item_type, mItem.type));
        ((TextView) getView().findViewById(R.id.level))
                .setText(getString(R.string.item_level, mItem.level));
        ((TextView) getView().findViewById(R.id.rarity))
                .setText(getString(R.string.item_rarity, mItem.rarity));
        ((TextView) getView().findViewById(R.id.vendor_value))
                .setText(getString(R.string.item_vendor_value,
                        mItem.vendor_value));
        ((TextView) getView().findViewById(R.id.game_types))
                .setText(getString(R.string.item_game_types,
                        Arrays.toString(mItem.game_types)));
        ((TextView) getView().findViewById(R.id.flags))
                .setText(getString(R.string.item_flags,
                        Arrays.toString(mItem.flags)));
        ((TextView) getView().findViewById(R.id.restrictions))
                .setText(getString(R.string.item_restrictions,
                        Arrays.toString(mItem.restrictions)));
        ((TextView) getView().findViewById(R.id.crafting_material))
                .setText(getString(R.string.item_crafting_material,
                        mItem.crafting_material));
        if (mItem.consumable != null) {
            getView().findViewById(R.id.consumable_wrapper)
                    .setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.consumable_description))
                    .setText(getString(R.string.consumable_description,
                            mItem.consumable.description == null ? "" :
                                    Html.fromHtml(mItem.consumable
                                            .description)));
            ((TextView) getView().findViewById(R.id.consumable_type))
                    .setText(getString(R.string.consumable_type,
                            mItem.consumable.type));
            ((TextView) getView().findViewById(R.id.consumable_duration))
                    .setText(getString(R.string.consumable_duration,
                            mItem.consumable.duration_ms / 1000f / 60f));
        } else {
            getView().findViewById(R.id.consumable_wrapper).setVisibility(View
                    .GONE);
        }
    }

    private LoaderManager.LoaderCallbacks<Item> mItemLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Item>() {
                @Override
                public Loader<Item> onCreateLoader(int i, Bundle bundle) {
                    return new ItemLoader(getActivity(), getArguments().getInt(Application
                            .Extras
                            .ITEM_ID));
                }

                @Override
                public void onLoadFinished(Loader<Item> itemLoader, Item item) {
                    getView().findViewById(R.id.loading).setVisibility(View.GONE);
                    getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
                    if (item == null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        R.string.toast_error_getting_item_details,
                                        Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    } else {
                        mItem = item;
                        updateView();
                    }
                }

                @Override
                public void onLoaderReset(Loader<Item> itemLoader) {

                }
            };
}
