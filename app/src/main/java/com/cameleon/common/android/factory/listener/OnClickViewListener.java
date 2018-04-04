package com.cameleon.common.android.factory.listener;

import android.content.DialogInterface;
import android.view.View;

public interface OnClickViewListener {
	public void onClick(DialogInterface dialog, View view, int which);
}