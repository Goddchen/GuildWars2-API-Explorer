package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.async.BuildLoader;
import de.goddchen.android.gw2.api.data.Build;

/**
 * Created by Goddchen on 22.05.13.
 */
public class BuildFragment extends SherlockFragment {

    public static BuildFragment newInstance() {
        BuildFragment fragment = new BuildFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.WORLDS, null, mWorldLoaderCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_build, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        view.findViewById(R.id.content).setVisibility(View.GONE);
    }

    private LoaderManager.LoaderCallbacks<Build> mWorldLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Build>() {
                @Override
                public Loader<Build> onCreateLoader(int i, Bundle bundle) {
                    return new BuildLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<Build> listLoader, Build build) {
                    View view = getView();
                    if (view != null) {
                        view.findViewById(R.id.loading).setVisibility(View.GONE);
                        view.findViewById(R.id.content).setVisibility(View.VISIBLE);
                    }
                    if (build != null) {
                        ((TextView) getView().findViewById(R.id.build))
                                .setText(String.format("Build: %d", build.build_id));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Build> listLoader) {

                }
            };
}
