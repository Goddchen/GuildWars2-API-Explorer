package de.goddchen.android.gw2.api.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.WorldsFragment;

/**
 * Created by Goddchen on 22.05.13.
 */
public class EventsActivity extends BaseFragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fragment_container);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, WorldsFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.change_home_world) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .remove(Application.Preferences.HOME_WORLD)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, WorldsFragment.newInstance())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}