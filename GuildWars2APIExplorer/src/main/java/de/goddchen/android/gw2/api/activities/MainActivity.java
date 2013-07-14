package de.goddchen.android.gw2.api.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.crittercism.app.Crittercism;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.BuildFragment;
import de.goddchen.android.gw2.api.fragments.ColorsFragment;
import de.goddchen.android.gw2.api.fragments.ContinentsFragment;
import de.goddchen.android.gw2.api.fragments.ItemsFragment;
import de.goddchen.android.gw2.api.fragments.MatchesFragment;
import de.goddchen.android.gw2.api.fragments.RecipesFragment;
import de.goddchen.android.gw2.api.fragments.WorldsFragment;
import de.goddchen.android.gw2.api.fragments.dialogs.CrashDialogFragment;
import de.goddchen.android.gw2.api.services.ItemSyncService;
import de.goddchen.android.gw2.api.services.RecipeSyncService;

public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.items).setOnClickListener(this);
        findViewById(R.id.events).setOnClickListener(this);
        findViewById(R.id.wvwvw).setOnClickListener(this);
        findViewById(R.id.recipes).setOnClickListener(this);
        findViewById(R.id.build).setOnClickListener(this);
        findViewById(R.id.colors).setOnClickListener(this);
        findViewById(R.id.maps).setOnClickListener(this);
        if (Crittercism.didCrashOnLastAppLoad()) {
            CrashDialogFragment.newInstance().show(getSupportFragmentManager(), "crash");
        }
        startSyncServices();
    }

    private void startSyncServices() {
        ActivityManager activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        boolean itemSyncRunning = false;
        boolean recipeSyncRunning = false;
        for (ActivityManager.RunningServiceInfo runningService :
                activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ItemSyncService.class.getName().equals(runningService.service.getClassName())) {
                itemSyncRunning = true;
            }
            if (RecipeSyncService.class.getName().equals(runningService.service.getClassName())) {
                recipeSyncRunning = true;
            }
        }
        if (!itemSyncRunning) {
            startService(new Intent(this, ItemSyncService.class));
        }
        if (!recipeSyncRunning) {
            startService(new Intent(this, RecipeSyncService.class));
        }
    }

    @Override
    public void onClick(View view) {
        if (findViewById(R.id.fragment) == null) {
            if (view.getId() == R.id.items) {
                startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
            } else if (view.getId() == R.id.events) {
                startActivity(new Intent(getApplicationContext(), EventsActivity.class));
            } else if (view.getId() == R.id.wvwvw) {
                startActivity(new Intent(getApplicationContext(), WvWActivity.class));
            } else if (view.getId() == R.id.recipes) {
                startActivity(new Intent(getApplicationContext(), RecipesActivity.class));
            } else if (view.getId() == R.id.build) {
                startActivity(new Intent(getApplicationContext(), BuildActivity.class));
            } else if (view.getId() == R.id.colors) {
                startActivity(new Intent(getApplicationContext(), ColorsActivity.class));
            } else if (view.getId() == R.id.maps) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        } else {
            if (view.getId() == R.id.items) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ItemsFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.events) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, WorldsFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.wvwvw) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, MatchesFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.recipes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, RecipesFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.maps) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ContinentsFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.colors) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ColorsFragment.newInstance())
                        .commit();
            } else if (view.getId() == R.id.build) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, BuildFragment.newInstance())
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
