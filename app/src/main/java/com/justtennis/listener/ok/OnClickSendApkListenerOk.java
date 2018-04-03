package com.justtennis.listener.ok;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.cameleon.common.tool.ApkTool;

public class OnClickSendApkListenerOk implements OnClickListener {

	private Activity context;

	public OnClickSendApkListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			try {
				String sourceDir = ApkTool.getInstance(context.getApplicationContext()).querySourceDir(context.getPackageName());
				if (sourceDir != null) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("application/octet-stream");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
					context.startActivity(intent);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}