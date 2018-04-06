package com.justtennis.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;

import com.justtennis.R;

import java.util.List;

public class ToolPermission {
	public static final int MY_PERMISSIONS_REQUEST = 123;

	private ToolPermission() {}

	public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Activity context) {
		return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, "Read external storage permission is necessary", true);
	}

	public static boolean checkPermissionWRITE_EXTERNAL_STORAGE(final Activity context) {
		return checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, "Write external storage permission is necessary", true);
	}

	public static boolean checkPermissionREAD_CONTACTS(final Activity activity, boolean doRequest) {
		return checkPermission(activity, Manifest.permission.READ_CONTACTS, "Contact acces permission is necessary", doRequest);
	}

	public static void grantPermissionProvider(Context context, Intent intent, Uri uri) {
		List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
	}

	private static boolean checkPermission(final Activity context, String permission, String message, boolean doRequest) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
			int p = ContextCompat.checkSelfPermission(context, permission);
			if (p != PackageManager.PERMISSION_GRANTED) {
				if (doRequest) {
					if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
						showDialog(message, context, permission);
					} else {
						ActivityCompat.requestPermissions(context, new String[]{permission}, MY_PERMISSIONS_REQUEST);
					}
				}
				return false;
			} else {
				return true;
			}

		} else {
			return true;
		}
	}

	private static void showDialog(final String msg, final Activity context, final String permission) {
		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.AppDialog);
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(contextThemeWrapper);
		alertBuilder.setCancelable(false);
		alertBuilder.setTitle("Permission necessary");
		alertBuilder.setMessage(msg);
		alertBuilder.setPositiveButton(android.R.string.yes,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, MY_PERMISSIONS_REQUEST);
				}
			}
		);
		AlertDialog alert = alertBuilder.create();
		alert.setCancelable(false);
		alert.show();
	}}
