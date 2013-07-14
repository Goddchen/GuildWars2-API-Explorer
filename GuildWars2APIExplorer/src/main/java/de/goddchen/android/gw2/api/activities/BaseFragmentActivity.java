package de.goddchen.android.gw2.api.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.analytics.tracking.android.EasyTracker;

import de.goddchen.android.gw2.api.receivers.BroadcastReceiver;

/**
 * Created by Goddchen on 29.05.13.
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

    private RequestQueue mRequestQueue;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        mBroadcastReceiver = getBroadcastReceiver();
        mBroadcastReceiver.register(this);
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
        mBroadcastReceiver.unregister(this);
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

    protected BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver();
    }
}
