package de.goddchen.android.gw2.api.fragments.dialogs;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Goddchen on 22.05.13.
 */
public class CrashDialogFragment extends DialogFragment {

    public static CrashDialogFragment newInstance() {
        CrashDialogFragment fragment = new CrashDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(de.goddchen.android.gw2.api.R.string.dialog_crash_title)
                .setMessage(de.goddchen.android.gw2.api.R.string.dialog_crash_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(Intent.createChooser(
                                new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(getString(de.goddchen.android.gw2.api.R.string.url_issue_tracker))),
                                ""
                        ));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
