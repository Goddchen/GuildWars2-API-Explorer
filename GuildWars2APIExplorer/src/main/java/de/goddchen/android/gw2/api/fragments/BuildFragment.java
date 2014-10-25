package de.goddchen.android.gw2.api.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Locale;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.async.GsonRequest;
import de.goddchen.android.gw2.api.data.Build;

/**
 * Created by Goddchen on 22.05.13.
 */
public class BuildFragment extends Fragment {

    public static BuildFragment newInstance() {
        BuildFragment fragment = new BuildFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseFragmentActivity) getActivity()).getRequestQueue()
                .add(new GsonRequest<Build>("https://api.guildwars2.com/v1/build.json?lang="
                        + Locale.getDefault().getLanguage(), Build.class,
                        new Response.Listener<Build>() {

                            @Override
                            public void onResponse(Build build) {
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
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        View view = getView();
                        if (view != null) {
                            view.findViewById(R.id.loading).setVisibility(View.GONE);
                            view.findViewById(R.id.content).setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getActivity(), R.string.toast_error_loading_data,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                ));
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
}
