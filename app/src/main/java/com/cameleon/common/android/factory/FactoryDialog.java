package com.cameleon.common.android.factory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.justtennis.R;
import com.cameleon.common.android.factory.listener.OnClickViewListener;

public class FactoryDialog {

	private static FactoryDialog instance;
	private boolean activateTheme = false;

	private FactoryDialog() {
	}

	public static FactoryDialog getInstance() {
		if (instance==null)
			instance = new FactoryDialog();
		return instance;
	}

	public Dialog buildListView(Context context, int titleId, String[] textList, final OnItemClickListener listener) {
		//http://stackoverflow.com/questions/2874191/is-it-possible-to-create-listview-inside-dialog
		//http://stackoverflow.com/questions/7302365/style-attributes-of-custom-styled-alertdialog
		Context ctx = context;
		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogTheme);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		ListView modeList = new ListView(ctx);//new ContextThemeWrapper(context, R.style.AlertDialogList));
		builder.setView(modeList);

		if (activateTheme) {
			View customTitle = View.inflate( context, R.layout.cameleon_common_alert_dialog_title, null );
			if (customTitle!=null) {
				builder.setCustomTitle(customTitle);
				TextView title = (TextView) customTitle.findViewById( R.id.alertTitle );
				if (title!=null) {
					title.setText(titleId);
				}
			}
		}
		
		final Dialog dialog = builder.create();
		if (!activateTheme && titleId > 0) {
			dialog.setTitle(titleId);
		}

		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogItem);
		}
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, android.R.id.text1, textList);
		modeList.setAdapter(modeAdapter);

		modeList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listener.onItemClick(parent, view, position, id);
				dialog.dismiss();
			}
		});

		return dialog;
	}

	public void buildOkDialog(Context context, String title, String message) {
//		AlertDialog.Builder builder = new Builder(context);
		Context ctx = context;
		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogTheme);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	public Dialog buildOkCancelDialog(Context context, OnClickListener onClickOkListener, int titleId, int messageId) {
		Context ctx = context;
		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogTheme);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(titleId);
		builder.setMessage(messageId);
		builder.setPositiveButton("OK", onClickOkListener);
		builder.setNeutralButton("Cancel", null);
		return builder.create();
	}

	public Dialog buildYesNoDialog(Context context, OnClickListener onClickListener, int titleId, int messageId) {
		Context ctx = context;
		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogTheme);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(titleId);
		builder.setMessage(messageId);
		builder.setNegativeButton("No", onClickListener);
		builder.setPositiveButton("Yes", onClickListener);
		return builder.create();
	}

	public Dialog buildLayoutDialog(Context context, final OnClickViewListener onClickOkListener, final OnClickViewListener onClickCancelListener, int titleId, int layoutId, int viewId) {
		LayoutInflater service = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = service.inflate(layoutId, null, true);
		View view = layout.findViewById(viewId);

		return buildViewDialog(context, onClickOkListener, onClickCancelListener, layout, view, titleId);
	}
		
	public Dialog buildEditTextDialog(Context context, final OnClickViewListener onClickOkListener, int titleId, String text) {
		return buildEditTextDialog(context, onClickOkListener, null, titleId, text);
	}

	public Dialog buildEditTextDialog(Context context, final OnClickViewListener onClickOkListener, final OnClickViewListener onClickCancelListener, int titleId, String text) {
		LayoutInflater service = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = service.inflate(R.layout.cameleon_common_dialog_edit_text, null, true);
		TextView textView = (TextView)layout.findViewById(R.id.tvText);
		textView.setText(text);

		return buildViewDialog(context, onClickOkListener, onClickCancelListener, layout, textView, titleId);
	}

	public Dialog buildDatePickerDialog(Context context, final OnClickViewListener onClickOkListener, int titleId, Date date) {
		LayoutInflater service = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = service.inflate(R.layout.cameleon_common_date_picker, null, true);
		DatePicker datePicker = (DatePicker)layout.findViewById(R.id.datePicker);

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		datePicker.init(
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				null
		);

		return buildViewDialog(context, onClickOkListener, null, layout, datePicker, titleId);
	}

	public Dialog buildTimePickerDialog(Context context, final OnClickViewListener onClickOkListener, int titleId, Date date) {
		LayoutInflater service = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = service.inflate(R.layout.cameleon_common_time_picker, null, true);
		TimePicker timePicker = (TimePicker)layout.findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

		return buildViewDialog(context, onClickOkListener, null, layout, timePicker, titleId);
	}

	private Dialog buildViewDialog(Context context, final OnClickViewListener onClickOkListener, final OnClickViewListener onClickCancelListener, View layout, final View view, int titleId) {
		Context ctx = context;
		if (activateTheme) {
			ctx = new ContextThemeWrapper(context, R.style.AlertDialogTheme);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		if (titleId>0) {
			builder.setTitle(titleId);
		}
		String txtNegative = null;
		String txtPositive = null;
		if (onClickCancelListener==null) {
			txtNegative = "Cancel";
			txtPositive = "Ok";
		} else {
			txtNegative = "No";
			txtPositive = "Yes";
		}
		builder.setNegativeButton(txtNegative, (onClickCancelListener==null ? null : new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickCancelListener.onClick(dialog, view, which);
			}
		}));
		builder.setPositiveButton(txtPositive, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickOkListener.onClick(dialog, view, which);
			}
		});
		builder.setView(layout);
		return builder.create();
	}
}