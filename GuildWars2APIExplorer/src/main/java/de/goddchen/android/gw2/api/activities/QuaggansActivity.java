package de.goddchen.android.gw2.api.activities;

import android.os.Bundle;
import android.view.MenuItem;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.QuaggansFragment;

/**
 * Created by Goddchen on 23.05.13.
 */
public class QuaggansActivity extends BaseFragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fragment_container);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, QuaggansFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}