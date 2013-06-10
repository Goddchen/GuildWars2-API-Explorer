package de.goddchen.android.gw2.api.async;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * Created by Goddchen on 07.06.13.
 */
public class GsonRequest<T> extends Request<T> {

    private Class<T> mClass;

    private Response.Listener mListener;

    public GsonRequest(String url, Class<T> objectClass, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mClass = objectClass;
        mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        T response = new Gson().fromJson(new InputStreamReader(new ByteArrayInputStream
                (networkResponse.data)), mClass);
        return Response.success(response, null);
    }

    @Override
    protected void deliverResponse(T t) {
        if (mListener != null) {
            mListener.onResponse(t);
        }
    }
}
