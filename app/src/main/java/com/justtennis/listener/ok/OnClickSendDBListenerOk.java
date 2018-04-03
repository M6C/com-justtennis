package com.justtennis.listener.ok;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.tool.ApkTool;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.notifier.NotifierMessageLogger;

public class OnClickSendDBListenerOk implements OnClickListener {

	private Activity context;

	public OnClickSendDBListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			try {
				String sourceDir = ApkTool.getInstance(context.getApplicationContext()).querySourceDir(context.getPackageName());
				if (sourceDir != null) {
					NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
					sendDb(DBDictionary.getInstance(context, notifier).getDBHelperByDatabaseName());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO Finish
	private void sendDb(Collection<GenericJustTennisDBHelper> listHelper) {
		ArrayList<Uri> uriList = new ArrayList<Uri>();
		Uri fromFile = null;
		for(GenericDBHelper helper : listHelper) {
			String sourceDir = helper.getBackupFile().getPath();
			fromFile = Uri.fromFile(new File(sourceDir));
			uriList.add(fromFile);
		}

//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_SEND);
//		intent.setType("application/justtennisdb");
//		//OK
////		intent.setType("application/octe-stream");
//		intent.putExtra(Intent.EXTRA_STREAM, fromFile);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("application/justtennisdb");
		intent.putExtra(Intent.EXTRA_STREAM, uriList);
		context.startActivity(intent);
	}

}