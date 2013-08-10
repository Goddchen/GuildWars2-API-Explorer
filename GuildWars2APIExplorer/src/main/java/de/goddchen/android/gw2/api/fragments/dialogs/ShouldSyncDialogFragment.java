package de.goddchen.android.gw2.api.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import de.goddchen.android.gw2.api.R;
import de.goddchen.android.gw2.api.services.ItemSyncService;
import de.goddchen.android.gw2.api.services.RecipeSyncService;

/**
 * Created by Goddchen on 10.08.13.
 */
public class ShouldSyncDialogFragment extends DialogFragment {

    private static final String EXTRA_TYPE = "type";

    public static final String TYPE_ITEM_SYNC = "items";

    public static final String TYPE_RECIPE_SYNC = "recipes";

    public static ShouldSyncDialogFragment newInstance(String type) {
        ShouldSyncDialogFragment fragment = new ShouldSyncDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity().getApplicationContext();
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_sync)
                .setMessage(R.string.confirm_sync)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getArguments().getString(EXTRA_TYPE, "").equals(TYPE_ITEM_SYNC)) {
                            if (!ItemSyncService.isRunning(context)) {
                                context.startService(new Intent(context, ItemSyncService.class));
                            }
                        } else if (getArguments().getString(EXTRA_TYPE, "").equals(TYPE_RECIPE_SYNC)) {
                            if (!RecipeSyncService.isRunning(context)) {
                                context.startService(new Intent(context, RecipeSyncService.class));
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
