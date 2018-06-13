package com.justtennis.tool;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

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

    private static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}
