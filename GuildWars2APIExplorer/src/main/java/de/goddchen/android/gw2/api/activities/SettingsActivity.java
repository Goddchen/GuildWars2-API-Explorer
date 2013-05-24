package de.goddchen.android.gw2.api.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import de.goddchen.android.gw2.api.R;

/**
 * Created by Goddchen on 24.05.13.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends SherlockPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}