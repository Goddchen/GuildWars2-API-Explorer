package de.goddchen.android.gw2.api.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.SherlockActivity;
import de.goddchen.android.gw2.api.R;

public class MainActivity extends SherlockActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.items).setOnClickListener(this);
        findViewById(R.id.events).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.items) {
            startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
        } else if (view.getId() == R.id.events) {
            startActivity(new Intent(getApplicationContext(), WorldsActivity.class));
        }
    }
}
