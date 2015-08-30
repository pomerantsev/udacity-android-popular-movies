package ru.pomerantsevp.udacity.popularmovies;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by pavel on 8/31/15.
 */
public class NetworkNotificationDialogFragment extends DialogFragment {
    public static String TAG = NetworkNotificationDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.network_unavailable_message)
            .setPositiveButton(R.string.ok, null);
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }
}
