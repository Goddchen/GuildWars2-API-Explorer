package de.goddchen.android.gw2.api.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.analytics.tracking.android.EasyTracker;

import de.goddchen.android.gw2.api.async.BitmapLruImageCache;

/**
 * Created by Goddchen on 29.05.13.
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

    private RequestQueue mRequestQueue;

    private ImageLoader.ImageCache mImageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        mImageCache = new BitmapLruImageCache(4 * 1024 * 1024);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        mImageCache = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
        mRequestQueue.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
        mRequestQueue.stop();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader.ImageCache getImageCache() {
        return mImageCache;
    }
}
