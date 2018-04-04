package com.cameleon.common.android.view;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justtennis.R;

//http://www.vogella.com/articles/AndroidCustomViews/article.html
//http://static5.depositphotos.com/1007083/467/v/950/depositphotos_4678770-Vector-calendar-icon.jpg
public class ViewDate extends LinearLayout {

	public ViewDate(Context context, AttributeSet attrs) {
		super(context, attrs);

//		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorOptionsView, 0, 0);
//		String titleText = a.getString(R.styleable.ColorOptionsView_titleText);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.cameleon_common_view_date, this, true);

		TextView month = (TextView) getChildAt(0);
		TextView day = (TextView) getChildAt(1);
		TextView year = (TextView) getChildAt(2);
		TextView time = (TextView) getChildAt(3);
		
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		
		month.setText(calendar.get(Calendar.MONTH));
		day.setText(calendar.get(Calendar.DAY_OF_MONTH));
		year.setText(calendar.get(Calendar.YEAR));
	}
}
