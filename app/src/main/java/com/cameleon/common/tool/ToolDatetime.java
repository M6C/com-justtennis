package com.cameleon.common.tool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ToolDatetime {

	private static SimpleDateFormat dfCompactedDatetime = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat dfMySql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat dfDatetimeDefault = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.MEDIUM);
	private static DateFormat dfDatetimeMedium = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
	private static DateFormat dfDatetimeShort = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM);
	private static DateFormat dfTimeDefault = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
	private static DateFormat dfTimeGmt = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
	static {
		dfTimeGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Format a Date to a MySQL String representation
	 * @param date Date to format
	 * @return MySQL Date String representation
	 */
	public static String toMySqlDate(Date date) {
		return dfMySql.format(date);
	}

	/**
	 * Format a Time in milliseconds to a MySQL String representation
	 * @param time Time (in milliseconds)
	 * @return MySQL Date String representation
	 */
	public static String toMySqlDate(long time) {
		return dfMySql.format(new Date(time));
	}

	/**
	 * Format a Datetime to a Default String representation
	 * @param date Date to format
	 * @return Default Datetime String representation
	 */
	public static String toDatetimeDefault(Date date) {
		return dfDatetimeDefault.format(date);
	}

	/**
	 * Format a Datetime to a Medium format String representation
	 * @param date Date to format
	 * @return Medium format Datetime String representation
	 */
	public static String toDatetimeMedium(Date date) {
		return dfDatetimeMedium.format(date);
	}

	/**
	 * Format a Datetime to a Short format String representation
	 * @param date Date to format
	 * @return Short format Datetime String representation
	 */
	public static String toDatetimeShort(Date date) {
		return dfDatetimeShort.format(date);
	}

	/**
	 * Format a Time to a Default String representation
	 * @param time Time (in milliseconds)
	 * @return Default Time String representation
	 */
	public static String toTimeDefault(long time) {
		return (time < (60 * 60 * 1000)) ?  dfTimeGmt.format(new Date(time)) : dfTimeDefault.format(new Date(time));
	}

	/**
	 * Format a Date to a compacted datetime String representation
	 * @param date Date to format
	 * @return Compacted datetime String representation
	 */
	public static String toCompactedDatetime(Date date) {
		return dfCompactedDatetime.format(date);
	}

	/**
	 * Format a String representing time in nb Sec, Min, Hour and Day
	 * @param time to format
	 * @return String of nb Sec, Min, Hour and Day
	 */
	public static String formatToNbSecMinHourDay(long time) {
		StringBuilder ret = new StringBuilder();

		int nbSec = 1000;
		int nbMin = 60 * nbSec;
		int nbHou = 60 * nbMin;
		int nbDay = 24 * nbHou;

		time = appendToNbSecMinHourDay(ret, time, nbDay, "Day", "s");
		time = appendToNbSecMinHourDay(ret, time, nbHou, "Hour", "s");
		time = appendToNbSecMinHourDay(ret, time, nbMin, "Min", null);
		time = appendToNbSecMinHourDay(ret, time, nbSec, "Sec", null);

		return ret.toString().trim();
	}

	/**
	 * Decrease time of nb cnt and append txt in ret and pluriel if necessary
	 * @param ret StringBuilder formated
	 * @param time Time to decrease
	 * @param cnt Value to decrease time
	 * @param txt Text to append if time > cnt
	 * @param plurielTxt Text pluriel to append if necessary
	 * @return if time >= cnt return time decrease by nb cnt if not return time
	 */
	private static long appendToNbSecMinHourDay(StringBuilder ret, long time, long cnt, String txt, String plurielTxt) {
		if (time >= cnt) {
			// Calculate Time
			long nb = time / cnt;
			time -= nb * cnt;
			// Format String
			ret.append(" ").append(Long.toString(nb)).append(" ").append(txt);
			if (nb>1 && plurielTxt!=null)
				ret.append(plurielTxt);
		}
		return time;
	}
}
