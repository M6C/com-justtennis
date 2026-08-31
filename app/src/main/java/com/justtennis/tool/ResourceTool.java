package com.justtennis.tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.justtennis.R;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.File;
import java.util.Random;

public class ResourceTool {

    private static final String TAG = ResourceTool.class.getSimpleName();

    private static Random rnd = new Random(1);

    private ResourceTool() {}

    public static @DrawableRes int getIdDrawableInStyleAttr(@NonNull Activity activity, @AttrRes int idAttrRes) {
        return getIdDrawableInStyleAttr(activity, idAttrRes, -1);
    }

    public static @DrawableRes int getIdDrawableInStyleAttr(@NonNull Activity activity, @AttrRes int idAttrRes, int index) {
        try {
            activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).getThemeResource();
            TypedArray attr = activity.obtainStyledAttributes(R.style.AppTheme, new int[]{idAttrRes});
            if (attr != null) {
                int id = attr.getResourceId(0, -1);
                attr.recycle();

                if (id != -1) {
                    return ResourceTool.getIdDrawableInIntArray(activity, id);
                }
            } else {
                logMe("getIdDrawableInStyleAttr idAttrRes:" + idAttrRes + " not Found.");
            }
        } catch (PackageManager.NameNotFoundException ex) {
            logMe(ex);
        }
        return 0;
    }

    public static @DrawableRes int getIdDrawableInIntArray(@NonNull Context context, @ArrayRes int idIntArray) {
        return getIdDrawableInIntArray(context, idIntArray, -1);
    }

    public static @DrawableRes int getIdDrawableInIntArray(@NonNull Context context, @ArrayRes int idIntArray, int index) {
        try {
            String[] list = context.getResources().getStringArray(idIntArray);
            int idx = (index >= 0) ? index : rnd.nextInt(list.length);
            String draw = list[idx];
            String name = new File(draw).getName();
            String res = name.substring(0, name.lastIndexOf('.'));
            return context.getResources().getIdentifier(res, "drawable", context.getPackageName());
        } catch (RuntimeException ex) {
            logMe(ex);
        }
        return 0;
    }

    private static void logMe(String message) {
        Logger.logMe(TAG, message);
    }

    private static void logMe(Exception ex) {
        Logger.logMe(TAG, ex);
    }
}
