package de.goddchen.android.gw2.api.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crittercism.app.Crittercism;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.ItemSearchFragment;
import de.goddchen.android.gw2.api.fragments.MatchesFragment;
import de.goddchen.android.gw2.api.fragments.RecipeSearchFragment;
import de.goddchen.android.gw2.api.fragments.WorldsFragment;
import de.goddchen.android.gw2.api.fragments.dialogs.CrashDialogFragment;

public class MainActivity extends SherlockFragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.items).setOnClickListener(this);
        findViewById(R.id.events).setOnClickListener(this);
        findViewById(R.id.wvwvw).setOnClickListener(this);
        findViewById(R.id.recipes).setOnClickListener(this);
        if (Crittercism.didCrashOnLastAppLoad()) {
            CrashDialogFragment.newInstance().show(getSupportFragmentManager(), "crash");
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
            }
        }
    }
}
