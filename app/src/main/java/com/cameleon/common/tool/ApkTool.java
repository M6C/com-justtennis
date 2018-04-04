package com.cameleon.common.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

public class ApkTool {

	private static ApkTool instance = null;
	private static Context mContext;

	private ApkTool() {

	}

	public static ApkTool getInstance(Context context) {
		if (instance == null) {
			instance = new ApkTool();
			ApkTool.mContext = context;
		}
		return instance;
	}

	public synchronized List<File> backup(String destinationDirectory) throws IOException {
		return backup(destinationDirectory, null);
	}

	public synchronized List<File> backup(String destinationDirectory, String packageNameFilter) throws IOException {
		List<File> ret = new ArrayList<File>();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			if (packageNameFilter==null || packageNameFilter.equals(info.activityInfo.packageName)) {
				File destination = FileTool.getInstance().backupToSdcard(info.activityInfo.applicationInfo.publicSourceDir, destinationDirectory);
				if (destination != null) {
					ret.add(destination);
				}
			}
		}
		return ret;
	}
	
	public synchronized String querySourceDir(String packageNameFilter) throws IOException {
		String ret = null;
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			if (packageNameFilter==null || packageNameFilter.equals(info.activityInfo.packageName)) {
				ret = info.activityInfo.applicationInfo.publicSourceDir;
				break;
			}
		}
		return ret;
	}
}