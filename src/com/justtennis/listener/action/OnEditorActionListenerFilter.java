package com.justtennis.listener.action;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Filter;

public class OnEditorActionListenerFilter implements TextWatcher {

	private static final String TAG = OnEditorActionListenerFilter.class.getSimpleName();

	private Filter filter;

	public OnEditorActionListenerFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public void afterTextChanged(Editable editor) {
//		String txt = editor.toString();
//		if (txt.length()>2) {
//			filter.filter(txt);
//		} else {
//			filter.filter(null);
//		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence txt, int arg1, int arg2, int arg3) {
Logger.logMe(TAG, "---------------> txt:" + txt + " length:" + txt.length());
		if (txt.length()>2) {
			filter.filter(txt);
		} else {
			filter.filter(null);
		}
	}
}