package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;
import java.util.Locale;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.async.GsonRequest;
import de.goddchen.android.gw2.api.data.GuildDetails;
import de.goddchen.android.gw2.api.data.MatchDetails;

/**
 * Created by Goddchen on 22.05.13.
 */
public class ObjectiveAdapter extends ArrayAdapter<MatchDetails.Objective> {
    public ObjectiveAdapter(Context context, List<MatchDetails.Objective> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MatchDetails.Objective objective = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_objective, parent, false);
        }
        if (objective.name == null || TextUtils.isEmpty(objective.name.name)) {
            ((TextView) convertView.findViewById(R.id.name)).setText("---");
        } else {
            ((TextView) convertView.findViewById(R.id.name)).setText(objective.name.name);
        }
        if (!TextUtils.isEmpty(objective.owner_guild)) {
            convertView.findViewById(R.id.owner).setVisibility(View.VISIBLE);
            try {
                GuildDetails guildDetails = Application.getDatabaseHelper().getGuildDetailsDao()
                        .queryForId(objective.owner_guild);
                if (guildDetails != null) {
                    ((TextView) convertView.findViewById(R.id.owner)).setText(
                            String.format("%s [%s]", guildDetails.guild_name, guildDetails.tag));
                } else {
                    loadGuildDetails((TextView) convertView.findViewById(R.id.owner), objective);
                }
            } catch (Exception e) {
                Log.w(Application.Constants.LOG_TAG, "Error loading guild details", e);
                ((TextView) convertView.findViewById(R.id.owner)).setText("---");
            }
        } else {
            convertView.findViewById(R.id.owner).setVisibility(View.GONE);
        }
        if ("red".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_red));
        } else if ("green".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_green));
        } else if ("blue".equalsIgnoreCase(objective.owner)) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.objective_owner_blue));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            convertView.findViewById(R.id.owner).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void loadGuildDetails(final TextView view, MatchDetails.Objective objective) {
        view.setText(R.string.loading);
        ((BaseFragmentActivity) getContext()).getRequestQueue()
                .add(new GsonRequest<GuildDetails>(
                        "https://api.guildwars2.com/v1/guild_details.json?guild_id="
                                + objective.owner_guild
                                + "&lang=" + Locale.getDefault().getLanguage(),
                        GuildDetails.class,
                        new Response.Listener<GuildDetails>() {
                            @Override
                            public void onResponse(GuildDetails guildDetails) {
                                try {
                                    Application.getDatabaseHelper().getGuildDetailsDao().create
                                            (guildDetails);
                                } catch (Exception e) {
                                    Log.w(Application.Constants.LOG_TAG,
                                            "Error saving guild details", e);
                                }
                                view.setText(String.format("%s [%s]",
                                        guildDetails.guild_name,
                                        guildDetails.tag));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                view.setText("---");
                            }
                        }
                ));
    }
}
