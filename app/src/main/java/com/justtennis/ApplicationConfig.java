package com.justtennis;

import java.util.Locale;

public class ApplicationConfig {
	
	public static final boolean SHOW_ID = false;
	public static final boolean SHOW_LOG_TRACE_PARSER = false;
	public static final boolean SHOW_LOG_TRACE_MAPPER = false;
	public static final boolean SHOW_LOG_SMS_PROCESS = true;
	public static final boolean SHOW_LOG_CURSOR_PROCESS = false;
	public static final boolean SHOW_LOG_COMPUTER_RANK = true;
	public static final boolean CALENDAR_ADD_EVENT_CONFIRMED = true;
	public static final boolean CALENDAR_ADD_EVENT_CANCELED = true;

	public static final boolean FEED_DATABASE = false;

	@SuppressWarnings("unused")
	public static final boolean CALENDAR_ADD_EVENT = CALENDAR_ADD_EVENT_CONFIRMED || CALENDAR_ADD_EVENT_CANCELED;

	public static final boolean SHOW_CONFIRM_DIALOG_TEXT_SMS = true;

	private ApplicationConfig() {
	}
	
	public static final Locale getLocal() {
		return Locale.FRANCE;
	}
}
