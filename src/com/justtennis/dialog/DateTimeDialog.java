package com.justtennis.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import com.justtennis.R;

public class DateTimeDialog extends AlertDialog {

	private static final String TAG = DateTimeDialog.class.getSimpleName();
	private DatePicker datePicker;
	private DatePicker timePicker;

	public DateTimeDialog(Context context) {
		super(context, true, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.date_time);
		
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		timePicker = (DatePicker)findViewById(R.id.timePicker);
	}
}
