package de.goddchen.android.gw2.api.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crittercism.app.Crittercism;
import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.fragments.dialogs.CrashDialogFragment;

public class MainActivity extends SherlockFragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.items).setOnClickListener(this);
        findViewById(R.id.events).setOnClickListener(this);
        findViewById(R.id.wvwvw).setOnClickListener(this);
        if (Crittercism.didCrashOnLastAppLoad()) {
            CrashDialogFragment.newInstance().show(getSupportFragmentManager(), "crash");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.items) {
            startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
        } else if (view.getId() == R.id.events) {
            startActivity(new Intent(getApplicationContext(), EventsActivity.class));
        } else if (view.getId() == R.id.wvwvw) {
            startActivity(new Intent(getApplicationContext(), WvWActivity.class));
        }
    }
}
