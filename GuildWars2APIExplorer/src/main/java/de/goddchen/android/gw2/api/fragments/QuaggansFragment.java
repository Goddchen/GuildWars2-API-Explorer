package de.goddchen.android.gw2.api.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Quaggan;
import de.goddchen.android.gw2.api.events.QuaggansUpdatedEvent;

/**
 * Created by Goddchen on 22.05.13.
 */
public class QuaggansFragment extends SherlockFragment {

    private RecyclerView mRecyclerView;

    public static QuaggansFragment newInstance() {
        QuaggansFragment fragment = new QuaggansFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Application.getBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quaggans, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Quaggan> quaggans = Application.getDatabaseHelper().getDaoForClass(Quaggan.class).queryForAll();
                    if (quaggans == null || quaggans.isEmpty()) {
                        HttpsURLConnection connection =
                                (HttpsURLConnection) new URL("https://api.guildwars2.com/v2/quaggans?ids=all&lang="
                                        + Locale.getDefault().getLanguage())
                                        .openConnection();
                        quaggans = new Gson().fromJson(new InputStreamReader(connection.getInputStream()),
                                new TypeToken<List<Quaggan>>() {
                                }.getType());
                        for (Quaggan quaggan : quaggans) {
                            Application.getDatabaseHelper().getDaoForClass(Quaggan.class).create(quaggan);
                        }
                    }
                    Application.getBus().post(new QuaggansUpdatedEvent(quaggans));
                } catch (Exception e) {
                    Log.e(Application.Constants.LOG_TAG, "Error loading quaggans", e);
                }
            }
        }).start();
    }

    @Subscribe
    public void onQuaggansUpdated(final QuaggansUpdatedEvent event) {
        if (event.getQuaggans() != null && mRecyclerView != null) {
            mRecyclerView.setAdapter(new RecyclerView.Adapter() {

                class ViewHolder extends RecyclerView.ViewHolder {

                    public ImageView imageView;

                    public ViewHolder(View itemView) {
                        super(itemView);
                        imageView = (ImageView) itemView.findViewById(R.id.image);
                    }
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View v = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_quaggan, viewGroup, false);
                    return new ViewHolder(v);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    final Quaggan quaggan = event.getQuaggans().get(i);
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                    Picasso.with(getActivity()).load(Uri.parse(quaggan.url))
                            .centerInside()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_close_clear_cancel)
                            .resize(size, size)
                            .into(((ViewHolder) viewHolder).imageView);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

                @Override
                public int getItemCount() {
                    return event.getQuaggans().size();
                }
            });
        }
    }
}
