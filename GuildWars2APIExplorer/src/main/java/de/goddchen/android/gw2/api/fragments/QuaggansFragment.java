package de.goddchen.android.gw2.api.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.adapter.QuagganAdapter;
import de.goddchen.android.gw2.api.async.QuagganLoader;
import de.goddchen.android.gw2.api.data.Quaggan;

/**
 * Created by Goddchen on 22.05.13.
 */
public class QuaggansFragment extends SherlockListFragment {

    private LoaderManager.LoaderCallbacks<List<Quaggan>> mQuagganLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Quaggan>>() {
                @Override
                public Loader<List<Quaggan>> onCreateLoader(int i, Bundle bundle) {
                    return new QuagganLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Quaggan>> listLoader, List<Quaggan> quaggans) {
                    if (quaggans != null) {
                        setListAdapter(new QuagganAdapter(getActivity(), quaggans));
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Quaggan>> listLoader) {

                }
            };

    public static QuaggansFragment newInstance() {
        QuaggansFragment fragment = new QuaggansFragment();
        return fragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Quaggan quaggan = (Quaggan) l.getItemAtPosition(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(quaggan.url), "image/*");
        if (getActivity().getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.WORLDS, null, mQuagganLoaderCallbacks);
    }
}
