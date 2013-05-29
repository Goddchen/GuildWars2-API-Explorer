package de.goddchen.android.gw2.api.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by Goddchen on 29.05.13.
 */
public class AnalyticsSherlockFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
}
