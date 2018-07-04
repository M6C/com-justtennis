package com.justtennis.tool;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.justtennis.R;

import org.gdocument.gtracergps.launcher.log.Logger;

public class FragmentTool {

    private static final String TAG = FragmentTool.class.getSimpleName();

    private FragmentTool() {}

    public static void replaceFragment(@NonNull FragmentActivity activity, @NonNull Fragment fragment, @IdRes int idRes) {
        String tag = fragment.getClass().getName();
        logMe("replaceFragment tag:" + tag);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(idRes, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public static void hideFab(@NonNull FragmentActivity activity) {
        View fab = activity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }
    }

    public static void onClickFab(@NonNull FragmentActivity activity, View.OnClickListener listener) {
        View fab = activity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(listener);
            fab.setVisibility(listener == null ?  View.GONE : View.VISIBLE);
        }
    }

    private static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}
