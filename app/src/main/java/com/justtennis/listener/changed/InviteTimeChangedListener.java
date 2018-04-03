package com.justtennis.listener.changed;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.justtennis.business.InviteBusiness;

public class InviteTimeChangedListener implements OnTimeChangedListener {
	
	private InviteBusiness business;

	public InviteTimeChangedListener(InviteBusiness business) {
		this.business = business;
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		Date date = business.getDate();
		if (date==null) {
			date = new Date();
		}
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		
		business.setDate(calendar.getTime());
	}

}
