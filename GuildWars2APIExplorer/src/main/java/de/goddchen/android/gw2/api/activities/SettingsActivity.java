package de.goddchen.android.gw2.api.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.google.analytics.tracking.android.EasyTracker;
import de.goddchen.android.gw2.api.Application;
import de.goddchen.android.gw2.api.R;

/**
 * Created by Goddchen on 24.05.13.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends SherlockPreferenceActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        showVersion();
        setupIssueTracker();
        setupRate();
        setupContact();
    }

    private void setupContact() {
        Preference preference =
                findPreference("feedback.contact");
        if (preference != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO,
                            Uri.parse("mailto:paradise.android@googlemail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "GW2 API Explorer - Feedback");
                    startActivity(Intent.createChooser(intent, ""));
                    return true;
                }
            });
        }
    }

    private void setupRate() {
        Preference preference =
                findPreference("feedback.rate");
        if (preference != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())
                    ));
                    return true;
                }
            });
        }
    }

    private void setupIssueTracker() {
        Preference preference =
                findPreference("feedback.issue");
        if (preference != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                            getString(R.string.url_issue_tracker)
                    )));
                    return true;
                }
            });
        }
    }

    private void showVersion() {
        Preference preference = findPreference("version");
        if (preference != null) {
            try {
                preference.setSummary(
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionName
                );
            } catch (Exception e) {
                Log.w(Application.Constants.LOG_TAG, "Error setting version", e);
                preference.setSummary("---");
            }
        }
    }
}