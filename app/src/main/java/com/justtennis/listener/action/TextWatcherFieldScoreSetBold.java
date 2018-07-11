package com.justtennis.listener.action;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class TextWatcherFieldScoreSetBold implements TextWatcher {

	private static final String TAG = TextWatcherFieldScoreSetBold.class.getSimpleName();
	private EditText etScore2;
	private EditText etScore1;

	public TextWatcherFieldScoreSetBold(EditText etScore1, EditText etScore2) {
		this.etScore1 = etScore1;
		this.etScore2 = etScore2;
	}

	@Override
	public void afterTextChanged(Editable editor) {
		/* Empty because nothing to do */
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		/* Empty because nothing to do */
	}

	@Override
	public void onTextChanged(CharSequence txt, int arg1, int arg2, int arg3) {
		String value2 = etScore2.getText().toString();
		if (value2.length() >0 &&  txt.length() > 0) {
			try {
				int score1 = Integer.parseInt(txt.toString());
				int score2 = Integer.parseInt(value2);

				if (score1 == score2) {
					etScore1.setTypeface(null, Typeface.NORMAL);
					etScore2.setTypeface(null, Typeface.NORMAL);
				} else if (score1 > score2) {
					etScore1.setTypeface(null, Typeface.BOLD);
					etScore2.setTypeface(null, Typeface.NORMAL);
				} else {
					etScore1.setTypeface(null, Typeface.NORMAL);
					etScore2.setTypeface(null, Typeface.BOLD);
				}
			} catch (NumberFormatException ex) {
				Log.e(TAG, "onTextChanged", ex);
			}
		}
	}
}