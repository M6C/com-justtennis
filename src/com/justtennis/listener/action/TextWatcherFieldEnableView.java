package com.justtennis.listener.action;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class TextWatcherFieldEnableView implements TextWatcher {

	private static final String TAG = TextWatcherFieldEnableView.class.getSimpleName();

	private View view;
	private int hiddenVisibility;

	public TextWatcherFieldEnableView(View view, int hiddenVisibility) {
		this.view = view;
		this.hiddenVisibility = hiddenVisibility;
	}

	@Override
	public void afterTextChanged(Editable editor) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence txt, int arg1, int arg2, int arg3) {
		if (txt.length() > 0) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(hiddenVisibility);
		}
	}
}