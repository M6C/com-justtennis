package com.justtennis.listener.changed;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.justtennis.business.InviteBusiness;

public class InviteDateChangedListener implements OnDateChangedListener {

	private InviteBusiness business;

	public InviteDateChangedListener(InviteBusiness business) {
		this.business = business;
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Date date = business.getDate();
		Calendar calendar = GregorianCalendar.getInstance();
		if (date==null) {
			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
		} else {
			calendar.setTime(date);
		}
		calendar.set(year, monthOfYear, dayOfMonth);
		business.setDate(calendar.getTime());
	}

}
