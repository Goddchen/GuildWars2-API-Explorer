package de.goddchen.android.gw2.api.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.BuildFragment;
import de.goddchen.android.gw2.api.fragments.ColorsFragment;
import de.goddchen.android.gw2.api.fragments.ContinentsFragment;
import de.goddchen.android.gw2.api.fragments.ItemSearchFragment;
import de.goddchen.android.gw2.api.fragments.MatchesFragment;
import de.goddchen.android.gw2.api.fragments.QuaggansFragment;
import de.goddchen.android.gw2.api.fragments.RecipeSearchFragment;
import de.goddchen.android.gw2.api.fragments.WorldsFragment;
import de.goddchen.android.gw2.api.fragments.dialogs.CrashDialogFragment;

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
        findViewById(R.id.quaggans).setOnClickListener(this);
        if (Crittercism.didCrashOnLastAppLoad()) {
            CrashDialogFragment.newInstance().show(getSupportFragmentManager(), "crash");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.events) {
            Toast.makeText(MainActivity.this, "This API endpoint is currently disabled due to the megaserver changes", Toast.LENGTH_SHORT).show();
        } else if (findViewById(R.id.fragment) == null) {
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
            } else if (view.getId() == R.id.quaggans) {
                startActivity(new Intent(getApplicationContext(), QuaggansActivity.class));
            }
        } else {
            if (view.getId() == R.id.items) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, ItemSearchFragment.newInstance())
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
                        .replace(R.id.fragment, RecipeSearchFragment.newInstance())
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
            } else if (view.getId() == R.id.quaggans) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, QuaggansFragment.newInstance())
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
