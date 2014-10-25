package de.goddchen.android.gw2.api.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.async.QuagganLoader;
import de.goddchen.android.gw2.api.data.Quaggan;

/**
 * Created by Goddchen on 22.05.13.
 */
public class QuaggansFragment extends SherlockFragment {

    private GridLayout mGridLayout;
    private LoaderManager.LoaderCallbacks<List<Quaggan>> mQuagganLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Quaggan>>() {
                @Override
                public Loader<List<Quaggan>> onCreateLoader(int i, Bundle bundle) {
                    return new QuagganLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<List<Quaggan>> listLoader, List<Quaggan> quaggans) {
                    if (quaggans != null) {
                        for (final Quaggan quaggan : quaggans) {
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_quaggan, mGridLayout, false);
                            final ImageView imageView = (ImageView) view.findViewById(de.goddchen.android.gw2.api.R.id.image);
                            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                            ImageRequest request = new ImageRequest(quaggan.url, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    imageView.setImageBitmap(bitmap);
                                }
                            }, 0, 0, null, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                                }
                            });
                            request.setShouldCache(true);
                            ((BaseFragmentActivity) getActivity()).getRequestQueue().add(request);
                            mGridLayout.addView(view);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse(quaggan.url), "image/*");
                                    if (getActivity().getPackageManager().resolveActivity(intent, 0) != null) {
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quaggans, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridLayout = (GridLayout) view.findViewById(R.id.gridlayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Application.Loaders.WORLDS, null, mQuagganLoaderCallbacks);
    }
}
