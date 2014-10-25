package de.goddchen.android.gw2.api.adapter;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import de.goddchen.android.gw2.api.activities.BaseFragmentActivity;
import de.goddchen.android.gw2.api.data.Quaggan;

/**
 * Created by Goddchen on 22.05.13.
 */
public class QuagganAdapter extends ArrayAdapter<Quaggan> {
    public QuagganAdapter(Context context, List<Quaggan> objects) {
        super(context, R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Quaggan quaggan = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(de.goddchen.android.gw2.api.R.layout.listitem_quaggan, parent, false);
        }
        final ImageView imageView = (ImageView) convertView.findViewById(de.goddchen.android.gw2.api.R.id.image);
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
        ((BaseFragmentActivity) getContext()).getRequestQueue().add(request);
        return convertView;
    }
}
