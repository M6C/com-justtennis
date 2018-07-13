package com.justtennis.tool;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.justtennis.R;
import com.justtennis.ui.rxjava.RxFragment;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.Objects;

public class FragmentTool {

    private static final String TAG = FragmentTool.class.getSimpleName();

    private FragmentTool() {}

    public enum INIT_FAB_IMAGE {
        VALIDATE(R.drawable.ic_check_black_24dp),
        ADD(android.R.drawable.ic_input_add),
        BACK(R.drawable.ic_arrow_back_black_24dp);

        @DrawableRes int res;
        INIT_FAB_IMAGE(@DrawableRes int res) {
            this.res = res;
        }
    }

    public static void replaceFragment(@NonNull FragmentActivity activity, @NonNull Fragment fragment) {
        replaceFragment(activity, fragment, R.id.item_detail_container);
    }

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

    public static void finish(Activity activity) {
        Objects.requireNonNull(activity).onBackPressed();
    }

    public static void onClickFab(@NonNull FragmentActivity activity, View.OnClickListener listener) {
        View fab = activity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(listener);
            fab.setVisibility(listener == null ?  View.GONE : View.VISIBLE);
        }
    }

    public static void initializeFabDrawable(@NonNull FragmentActivity activity, INIT_FAB_IMAGE type) {
        initializeFabDrawable(activity, type.res);
    }

    public static void initializeFabDrawable(@NonNull FragmentActivity activity, @DrawableRes int idRes) {
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageResource(idRes);
        }
    }

    private static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}
