package de.goddchen.android.gw2.api.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import de.goddchen.android.gw2.api.Application;

/**
 * Created by Goddchen on 14.07.13.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Application.Actions.ITEMS_SYNC_FAILED.equals(intent.getAction())) {
            onItemSyncFailed();
        } else if (Application.Actions.ITEMS_SYNC_FINISHED.equals(intent.getAction())) {
            onItemSyncFinished();
        } else if (Application.Actions.ITEMS_SYNC_STARTED.equals(intent.getAction())) {
            onItemSyncStarted();
        } else if (Application.Actions.RECIPE_SYNC_FAILED.equals(intent.getAction())) {
            onRecipeSyncFailed();
        } else if (Application.Actions.RECIPE_SYNC_FINISHED.equals(intent.getAction())) {
            onRecipeSyncFinished();
        } else if (Application.Actions.RECIPE_SYNC_STARTED.equals(intent.getAction())) {
            onRecipeSyncStarted();
        }
    }

    public void register(Activity activity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Application.Actions.ITEMS_SYNC_FAILED);
        intentFilter.addAction(Application.Actions.ITEMS_SYNC_FINISHED);
        intentFilter.addAction(Application.Actions.ITEMS_SYNC_STARTED);
        intentFilter.addAction(Application.Actions.RECIPE_SYNC_FAILED);
        intentFilter.addAction(Application.Actions.RECIPE_SYNC_FINISHED);
        intentFilter.addAction(Application.Actions.RECIPE_SYNC_STARTED);
        activity.registerReceiver(this, intentFilter);
    }

    public void unregister(Activity activity) {
        activity.unregisterReceiver(this);
    }

    protected void onItemSyncStarted() {

    }

    protected void onItemSyncFailed() {

    }

    protected void onItemSyncFinished() {

    }

    protected void onRecipeSyncFailed() {

    }

    protected void onRecipeSyncStarted() {

    }

    protected void onRecipeSyncFinished() {

    }
}
