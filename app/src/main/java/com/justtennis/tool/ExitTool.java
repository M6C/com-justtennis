package com.justtennis.tool;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.justtennis.R;

public class ExitTool {

    private static boolean backPressedToExitOnce = false;
    private static Toast toast = null;

    private ExitTool() {}

    public static synchronized boolean onBackPressed(Activity activity) {
        if (backPressedToExitOnce) {
            return true;
        } else if (toast == null) {
            backPressedToExitOnce = true;
            toast = Toast.makeText(activity, R.string.press_again_to_exit, Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                    toast = null;
                }
            }, 2000);
        }
        return false;
    }
}
