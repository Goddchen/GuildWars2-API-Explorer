package de.goddchen.android.gw2.api.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.data.Match;

/**
 * Created by Goddchen on 22.05.13.
 */
public class MatchAdapter extends ArrayAdapter<Match> {

    private SimpleDateFormat mInputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    private DateFormat mOutputDateFormat = DateFormat.getDateTimeInstance();

    public MatchAdapter(Context context, List<Match> objects) {
        super(context, R.layout.listitem_match, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Match match = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_match, parent, false);
        }
        Spanned text = Html.fromHtml(
                String.format("<b>%s</b><br>vs<br><b>%s</b><br>vs<br><b>%s</b>",
                        match.redWorld == null || TextUtils.isEmpty(match.redWorld.name) ?
                                "---" : match.redWorld.name,
                        match.blueWorld == null || TextUtils.isEmpty(match.blueWorld.name) ?
                                "---" : match.blueWorld.name,
                        match.greenWorld == null || TextUtils.isEmpty(match.greenWorld.name) ?
                                "---" : match.greenWorld.name
                ));
        ((TextView) convertView.findViewById(R.id.name)).setText(text);
        try {
            ((TextView) convertView.findViewById(R.id.start_time))
                    .setText(getContext().getString(R.string.match_start_time,
                            mOutputDateFormat.format(mInputDateFormat
                                    .parse(match.start_time))));
            ((TextView) convertView.findViewById(R.id.end_time))
                    .setText(getContext().getString(R.string.match_end_time,
                            mOutputDateFormat.format(mInputDateFormat
                                    .parse(match.end_time))));
        } catch (Exception e) {
            Log.w(Application.Constants.LOG_TAG, "Error parsing dates", e);
            convertView.findViewById(R.id.start_time).setVisibility(View.GONE);
            convertView.findViewById(R.id.end_time).setVisibility(View.GONE);
        }
        return convertView;
    }
}
